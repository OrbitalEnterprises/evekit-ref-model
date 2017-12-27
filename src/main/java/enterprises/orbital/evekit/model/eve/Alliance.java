package enterprises.orbital.evekit.model.eve;

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
    name = "evekit_eve_alliance")
@NamedQueries({
    @NamedQuery(
        name = "Alliance.get",
        query = "SELECT c FROM Alliance c WHERE c.allianceID = :allianceid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class Alliance extends RefCachedData {
  private static final Logger log = Logger.getLogger(Alliance.class.getName());
  private long allianceID;
  private long executorCorpID;
  private int memberCount;
  private String name;
  private String shortName;
  private long startDate;
  @Transient
  @ApiModelProperty(
      value = "startDate Date")
  @JsonProperty("startDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date startDateDate;
  private long creatorID;
  private long creatorCorpID;
  private int factionID;

  @SuppressWarnings("unused")
  protected Alliance() {}

  public Alliance(long allianceID, long executorCorpID, int memberCount, String name, String shortName, long startDate,
                  long creatorID, long creatorCorpID, int factionID) {
    super();
    this.allianceID = allianceID;
    this.executorCorpID = executorCorpID;
    this.memberCount = memberCount;
    this.name = name;
    this.shortName = shortName;
    this.startDate = startDate;
    this.creatorID = creatorID;
    this.creatorCorpID = creatorCorpID;
    this.factionID = factionID;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
    startDateDate = assignDateField(startDate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      RefCachedData sup) {
    if (!(sup instanceof Alliance)) return false;
    Alliance other = (Alliance) sup;
    return allianceID == other.allianceID && executorCorpID == other.executorCorpID && memberCount == other.memberCount
        && nullSafeObjectCompare(name, other.name) && nullSafeObjectCompare(shortName, other.shortName) && startDate == other.startDate &&
        creatorID == other.creatorID && creatorCorpID == other.creatorCorpID && factionID == other.factionID;
  }

  public long getAllianceID() {
    return allianceID;
  }

  public long getExecutorCorpID() {
    return executorCorpID;
  }

  public int getMemberCount() {
    return memberCount;
  }

  public String getName() {
    return name;
  }

  public String getShortName() {
    return shortName;
  }

  public long getStartDate() {
    return startDate;
  }

  public long getCreatorID() {
    return creatorID;
  }

  public long getCreatorCorpID() {
    return creatorCorpID;
  }

  public int getFactionID() {
    return factionID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Alliance alliance = (Alliance) o;
    return allianceID == alliance.allianceID &&
        executorCorpID == alliance.executorCorpID &&
        memberCount == alliance.memberCount &&
        startDate == alliance.startDate &&
        creatorID == alliance.creatorID &&
        creatorCorpID == alliance.creatorCorpID &&
        factionID == alliance.factionID &&
        Objects.equals(name, alliance.name) &&
        Objects.equals(shortName, alliance.shortName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), allianceID, executorCorpID, memberCount, name, shortName, startDate, creatorID, creatorCorpID, factionID);
  }

  @Override
  public String toString() {
    return "Alliance{" +
        "allianceID=" + allianceID +
        ", executorCorpID=" + executorCorpID +
        ", memberCount=" + memberCount +
        ", name='" + name + '\'' +
        ", shortName='" + shortName + '\'' +
        ", startDate=" + startDate +
        ", startDateDate=" + startDateDate +
        ", creatorID=" + creatorID +
        ", creatorCorpID=" + creatorCorpID +
        ", factionID=" + factionID +
        '}';
  }

  public static Alliance get(
      final long time,
      final long allianceID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<Alliance> getter = EveKitRefDataProvider.getFactory()
                                                                                       .getEntityManager()
                                                                                       .createNamedQuery("Alliance.get", Alliance.class);
                                    getter.setParameter("point", time);
                                    getter.setParameter("allianceid", allianceID);
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

  public static List<Alliance> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector allianceID,
      final AttributeSelector executorCorpID,
      final AttributeSelector memberCount,
      final AttributeSelector name,
      final AttributeSelector shortName,
      final AttributeSelector startDate,
      final AttributeSelector creatorID,
      final AttributeSelector creatorCorpID,
      final AttributeSelector factionID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM Alliance c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeParameters p = new AttributeParameters("att");
                                    AttributeSelector.addLongSelector(qs, "c", "allianceID", allianceID);
                                    AttributeSelector.addLongSelector(qs, "c", "executorCorpID", executorCorpID);
                                    AttributeSelector.addIntSelector(qs, "c", "memberCount", memberCount);
                                    AttributeSelector.addStringSelector(qs, "c", "name", name, p);
                                    AttributeSelector.addStringSelector(qs, "c", "shortName", shortName, p);
                                    AttributeSelector.addLongSelector(qs, "c", "startDate", startDate);
                                    AttributeSelector.addLongSelector(qs, "c", "creatorID", creatorID);
                                    AttributeSelector.addLongSelector(qs, "c", "creatorCorpID", creatorCorpID);
                                    AttributeSelector.addIntSelector(qs, "c", "factionID", factionID);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<Alliance> query = EveKitRefDataProvider.getFactory()
                                                                                      .getEntityManager()
                                                                                      .createQuery(qs.toString(), Alliance.class);
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
