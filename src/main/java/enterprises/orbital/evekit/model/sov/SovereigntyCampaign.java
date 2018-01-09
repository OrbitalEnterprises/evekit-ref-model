package enterprises.orbital.evekit.model.sov;

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
    name = "evekit_sov_campaign")
@NamedQueries({
    @NamedQuery(
        name = "SovereigntyCampaign.get",
        query = "SELECT c FROM SovereigntyCampaign c WHERE c.campaignID = :campaignid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class SovereigntyCampaign extends RefCachedData {
  private static final Logger log = Logger.getLogger(SovereigntyCampaign.class.getName());

  private int campaignID;
  private long structureID;
  private int systemID;
  private int constellationID;
  private String eventType;
  private long startTime;
  // optional
  private int defenderID;
  // optional
  private float defenderScore;
  // optional
  private float attackersScore;

  // Transient timestamp fields for better readability
  @Transient
  @ApiModelProperty(
      value = "Start Time Date")
  @JsonProperty("startTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date startTimeDate;

  @SuppressWarnings("unused")
  protected SovereigntyCampaign() {}

  public SovereigntyCampaign(int campaignID, long structureID, int systemID, int constellationID,
                             String eventType, long startTime, int defenderID, float defenderScore,
                             float attackersScore) {
    this.campaignID = campaignID;
    this.structureID = structureID;
    this.systemID = systemID;
    this.constellationID = constellationID;
    this.eventType = eventType;
    this.startTime = startTime;
    this.defenderID = defenderID;
    this.defenderScore = defenderScore;
    this.attackersScore = attackersScore;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    startTimeDate = assignDateField(startTime);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      RefCachedData sup) {
    if (!(sup instanceof SovereigntyCampaign)) return false;
    SovereigntyCampaign other = (SovereigntyCampaign) sup;
    return campaignID == other.campaignID &&
        structureID == other.structureID &&
        systemID == other.systemID &&
        constellationID == other.constellationID &&
        nullSafeObjectCompare(eventType, other.eventType) &&
        startTime == other.startTime &&
        defenderID == other.defenderID &&
        defenderScore == other.defenderScore &&
        attackersScore == other.attackersScore;
  }

  public int getCampaignID() {
    return campaignID;
  }

  public long getStructureID() {
    return structureID;
  }

  public int getSystemID() {
    return systemID;
  }

  public int getConstellationID() {
    return constellationID;
  }

  public String getEventType() {
    return eventType;
  }

  public long getStartTime() {
    return startTime;
  }

  public int getDefenderID() {
    return defenderID;
  }

  public float getDefenderScore() {
    return defenderScore;
  }

  public float getAttackersScore() {
    return attackersScore;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SovereigntyCampaign that = (SovereigntyCampaign) o;
    return campaignID == that.campaignID &&
        structureID == that.structureID &&
        systemID == that.systemID &&
        constellationID == that.constellationID &&
        startTime == that.startTime &&
        defenderID == that.defenderID &&
        Float.compare(that.defenderScore, defenderScore) == 0 &&
        Float.compare(that.attackersScore, attackersScore) == 0 &&
        Objects.equals(eventType, that.eventType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), campaignID, structureID, systemID, constellationID, eventType, startTime, defenderID, defenderScore, attackersScore);
  }

  @Override
  public String toString() {
    return "SovereigntyCampaign{" +
        "campaignID=" + campaignID +
        ", structureID=" + structureID +
        ", systemID=" + systemID +
        ", constellationID=" + constellationID +
        ", eventType='" + eventType + '\'' +
        ", startTime=" + startTime +
        ", defenderID=" + defenderID +
        ", defenderScore=" + defenderScore +
        ", attackersScore=" + attackersScore +
        ", startTimeDate=" + startTimeDate +
        '}';
  }

  public static SovereigntyCampaign get(
      final long time,
      final int campaignID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<SovereigntyCampaign> getter = EveKitRefDataProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createNamedQuery("SovereigntyCampaign.get", SovereigntyCampaign.class);
                                    getter.setParameter("point", time);
                                    getter.setParameter("campaignid", campaignID);
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

  public static List<SovereigntyCampaign> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector campaignID,
      final AttributeSelector structureID,
      final AttributeSelector systemID,
      final AttributeSelector constellationID,
      final AttributeSelector eventType,
      final AttributeSelector startTime,
      final AttributeSelector defenderID,
      final AttributeSelector defenderScore,
      final AttributeSelector attackersScore) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM SovereigntyCampaign c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeParameters p = new AttributeParameters("att");
                                    AttributeSelector.addIntSelector(qs, "c", "campaignID", campaignID);
                                    AttributeSelector.addLongSelector(qs, "c", "structureID", structureID);
                                    AttributeSelector.addIntSelector(qs, "c", "systemID", systemID);
                                    AttributeSelector.addIntSelector(qs, "c", "constellationID", constellationID);
                                    AttributeSelector.addStringSelector(qs, "c", "eventType", eventType, p);
                                    AttributeSelector.addLongSelector(qs, "c", "startTime", startTime);
                                    AttributeSelector.addIntSelector(qs, "c", "defenderID", defenderID);
                                    AttributeSelector.addFloatSelector(qs, "c", "defenderScore", defenderScore);
                                    AttributeSelector.addFloatSelector(qs, "c", "attackersScore", attackersScore);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<SovereigntyCampaign> query = EveKitRefDataProvider.getFactory()
                                                                                                 .getEntityManager()
                                                                                                 .createQuery(qs.toString(), SovereigntyCampaign.class);
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
