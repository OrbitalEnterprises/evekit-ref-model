package enterprises.orbital.evekit.model.sov;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.evekit.account.EveKitRefDataProvider;
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
    name = "evekit_sov_structure")
@NamedQueries({
    @NamedQuery(
        name = "SovereigntyStructure.get",
        query = "SELECT c FROM SovereigntyStructure c WHERE c.structureID = :structureid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class SovereigntyStructure extends RefCachedData {
  private static final Logger log = Logger.getLogger(SovereigntyStructure.class.getName());
  private int allianceID;
  private int systemID;
  private long structureID;
  private int structureTypeID;
  // optional
  private float vulnerabilityOccupancyLevel;
  // optional
  private long vulnerableStartTime;
  // optional
  private long vulnerableEndTime;

  // Transient timestamp fields for better readability
  @Transient
  @ApiModelProperty(
      value = "Vulnerable Start Time Date")
  @JsonProperty("vulnerableStartTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date vulnerableStartTimeDate;
  @Transient
  @ApiModelProperty(
      value = "Vulnerable End Time Date")
  @JsonProperty("vulnerableEndTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date vulnerableEndTimeDate;

  @SuppressWarnings("unused")
  protected SovereigntyStructure() {}

  public SovereigntyStructure(int allianceID, int systemID, long structureID, int structureTypeID,
                              float vulnerabilityOccupancyLevel, long vulnerableStartTime, long vulnerableEndTime) {
    this.allianceID = allianceID;
    this.systemID = systemID;
    this.structureID = structureID;
    this.structureTypeID = structureTypeID;
    this.vulnerabilityOccupancyLevel = vulnerabilityOccupancyLevel;
    this.vulnerableStartTime = vulnerableStartTime;
    this.vulnerableEndTime = vulnerableEndTime;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
    vulnerableStartTimeDate = assignDateField(vulnerableStartTime);
    vulnerableEndTimeDate = assignDateField(vulnerableEndTime);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      RefCachedData sup) {
    if (!(sup instanceof SovereigntyStructure)) return false;
    SovereigntyStructure other = (SovereigntyStructure) sup;
    return allianceID == other.allianceID && systemID == other.systemID &&
        structureID == other.structureID &&
        structureTypeID == other.structureTypeID &&
        vulnerabilityOccupancyLevel == other.vulnerabilityOccupancyLevel &&
        vulnerableStartTime == other.vulnerableStartTime &&
        vulnerableEndTime == other.vulnerableEndTime;
  }

  public int getAllianceID() {
    return allianceID;
  }

  public int getSystemID() {
    return systemID;
  }

  public long getStructureID() {
    return structureID;
  }

  public int getStructureTypeID() {
    return structureTypeID;
  }

  public float getVulnerabilityOccupancyLevel() {
    return vulnerabilityOccupancyLevel;
  }

  public long getVulnerableStartTime() {
    return vulnerableStartTime;
  }

  public long getVulnerableEndTime() {
    return vulnerableEndTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SovereigntyStructure that = (SovereigntyStructure) o;
    return allianceID == that.allianceID &&
        systemID == that.systemID &&
        structureID == that.structureID &&
        structureTypeID == that.structureTypeID &&
        Float.compare(that.vulnerabilityOccupancyLevel, vulnerabilityOccupancyLevel) == 0 &&
        vulnerableStartTime == that.vulnerableStartTime &&
        vulnerableEndTime == that.vulnerableEndTime;
  }

  @Override
  public int hashCode() {

    return Objects.hash(super.hashCode(), allianceID, systemID, structureID, structureTypeID, vulnerabilityOccupancyLevel, vulnerableStartTime, vulnerableEndTime);
  }

  @Override
  public String toString() {
    return "SovereigntyStructure{" +
        "allianceID=" + allianceID +
        ", systemID=" + systemID +
        ", structureID=" + structureID +
        ", structureTypeID=" + structureTypeID +
        ", vulnerabilityOccupancyLevel=" + vulnerabilityOccupancyLevel +
        ", vulnerableStartTime=" + vulnerableStartTime +
        ", vulnerableEndTime=" + vulnerableEndTime +
        ", vulnerableStartTimeDate=" + vulnerableStartTimeDate +
        ", vulnerableEndTimeDate=" + vulnerableEndTimeDate +
        '}';
  }

  public static SovereigntyStructure get(
      final long time,
      final long structureID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<SovereigntyStructure> getter = EveKitRefDataProvider.getFactory()
                                                                                                   .getEntityManager()
                                                                                                   .createNamedQuery("SovereigntyStructure.get", SovereigntyStructure.class);
                                    getter.setParameter("point", time);
                                    getter.setParameter("structureid", structureID);
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

  public static List<SovereigntyStructure> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector allianceID,
      final AttributeSelector systemID,
      final AttributeSelector structureID,
      final AttributeSelector structureTypeID,
      final AttributeSelector vulnerabilityOccupancyLevel,
      final AttributeSelector vulnerableStartTime,
      final AttributeSelector vulnerableEndTime) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM SovereigntyStructure c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeSelector.addIntSelector(qs, "c", "allianceID", allianceID);
                                    AttributeSelector.addIntSelector(qs, "c", "systemID", systemID);
                                    AttributeSelector.addLongSelector(qs, "c", "structureID", structureID);
                                    AttributeSelector.addIntSelector(qs, "c", "structureTypeID", structureTypeID);
                                    AttributeSelector.addFloatSelector(qs, "c", "vulnerabilityOccupancyLevel", vulnerabilityOccupancyLevel);
                                    AttributeSelector.addLongSelector(qs, "c", "vulnerableStartTime", vulnerableStartTime);
                                    AttributeSelector.addLongSelector(qs, "c", "vulnerableEndTime", vulnerableEndTime);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<SovereigntyStructure> query = EveKitRefDataProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createQuery(qs.toString(), SovereigntyStructure.class);
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
