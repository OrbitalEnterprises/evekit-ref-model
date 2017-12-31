package enterprises.orbital.evekit.model.server;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.evekit.account.EveKitRefDataProvider;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.RefCachedData;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_server_server_status")
@NamedQueries({
    @NamedQuery(
        name = "ServerStatus.get",
        query = "SELECT c FROM ServerStatus c where c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class ServerStatus extends RefCachedData {
  private static final Logger log = Logger.getLogger(ServerStatus.class.getName());
  private int onlinePlayers;
  private long startTime;
  private String serverVersion;
  private boolean vip;

  // Transient timestamp fields for better readability
  @Transient
  @ApiModelProperty(
      value = "StartTime Date")
  @JsonProperty("startTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date startTimeDate;

  @SuppressWarnings("unused")
  protected ServerStatus() {}

  public ServerStatus(int onlinePlayers, long startTime, String serverVersion, boolean vip) {
    super();
    this.onlinePlayers = onlinePlayers;
    this.startTime = startTime;
    this.serverVersion = serverVersion;
    this.vip = vip;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
    startTimeDate = assignDateField(startTime);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      RefCachedData sup) {
    if (!(sup instanceof ServerStatus)) return false;
    ServerStatus other = (ServerStatus) sup;
    return onlinePlayers == other.onlinePlayers && startTime == other.startTime && nullSafeObjectCompare(serverVersion, other.serverVersion) &&
        vip == other.vip;
  }

  public int getOnlinePlayers() {
    return onlinePlayers;
  }

  public long getStartTime() {
    return startTime;
  }

  public String getServerVersion() {
    return serverVersion;
  }

  public boolean isVip() {
    return vip;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ServerStatus that = (ServerStatus) o;
    return onlinePlayers == that.onlinePlayers &&
        startTime == that.startTime &&
        vip == that.vip &&
        Objects.equals(serverVersion, that.serverVersion);
  }

  @Override
  public int hashCode() {

    return Objects.hash(super.hashCode(), onlinePlayers, startTime, serverVersion, vip);
  }

  @Override
  public String toString() {
    return "ServerStatus{" +
        "onlinePlayers=" + onlinePlayers +
        ", startTime=" + startTime +
        ", serverVersion='" + serverVersion + '\'' +
        ", vip=" + vip +
        ", startTimeDate=" + startTimeDate +
        '}';
  }

  public static ServerStatus get(
      final long time) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<ServerStatus> getter = EveKitRefDataProvider.getFactory()
                                                                                           .getEntityManager()
                                                                                           .createNamedQuery("ServerStatus.get", ServerStatus.class);
                                    getter.setParameter("point", time);
                                    try {
                                      return getter.getSingleResult();
                                    } catch (NoResultException e) {
                                      return null;
                                    }
                                  });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

  public static List<ServerStatus> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector onlinePlayers,
      final AttributeSelector startTime,
      final AttributeSelector serverVersion,
      final AttributeSelector vip) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM ServerStatus c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeParameters p = new AttributeParameters("att");
                                    AttributeSelector.addIntSelector(qs, "c", "onlinePlayers", onlinePlayers);
                                    AttributeSelector.addLongSelector(qs, "c", "startTime", startTime);
                                    AttributeSelector.addStringSelector(qs, "c", "serverVersion", serverVersion, p);
                                    AttributeSelector.addBooleanSelector(qs, "c", "vip", vip);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<ServerStatus> query = EveKitRefDataProvider.getFactory()
                                                                                          .getEntityManager()
                                                                                          .createQuery(qs.toString(), ServerStatus.class);
                                    p.fillParams(query);
                                    query.setMaxResults(maxresults);
                                    return query.getResultList();
                                  });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

}
