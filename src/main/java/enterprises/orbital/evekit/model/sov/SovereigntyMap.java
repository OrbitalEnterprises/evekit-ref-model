package enterprises.orbital.evekit.model.sov;

import enterprises.orbital.evekit.account.EveKitRefDataProvider;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.RefCachedData;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_sov_map")
@NamedQueries({
    @NamedQuery(
        name = "SovereigntyMap.get",
        query = "SELECT c FROM SovereigntyMap c WHERE c.systemID = :sid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class SovereigntyMap extends RefCachedData {
  private static final Logger log = Logger.getLogger(SovereigntyMap.class.getName());
  // optional
  private int allianceID;
  // optional
  private int corporationID;
  // optional
  private int factionID;
  private int systemID;

  @SuppressWarnings("unused")
  protected SovereigntyMap() {}

  public SovereigntyMap(int allianceID, int corporationID, int factionID, int systemID) {
    super();
    this.allianceID = allianceID;
    this.corporationID = corporationID;
    this.factionID = factionID;
    this.systemID = systemID;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      RefCachedData sup) {
    if (!(sup instanceof SovereigntyMap)) return false;
    SovereigntyMap other = (SovereigntyMap) sup;
    return allianceID == other.allianceID && corporationID == other.corporationID && factionID == other.factionID && systemID == other.systemID;
  }

  public long getAllianceID() {
    return allianceID;
  }

  public long getCorporationID() {
    return corporationID;
  }

  public long getFactionID() {
    return factionID;
  }

  public int getSystemID() {
    return systemID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SovereigntyMap that = (SovereigntyMap) o;
    return allianceID == that.allianceID &&
        corporationID == that.corporationID &&
        factionID == that.factionID &&
        systemID == that.systemID;
  }

  @Override
  public int hashCode() {

    return Objects.hash(super.hashCode(), allianceID, corporationID, factionID, systemID);
  }

  @Override
  public String toString() {
    return "SovereigntyMap{" +
        "allianceID=" + allianceID +
        ", corporationID=" + corporationID +
        ", factionID=" + factionID +
        ", systemID=" + systemID +
        '}';
  }

  public static SovereigntyMap get(
      final long time,
      final int systemID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<SovereigntyMap> getter = EveKitRefDataProvider.getFactory()
                                                                                             .getEntityManager()
                                                                                             .createNamedQuery("SovereigntyMap.get", SovereigntyMap.class);
                                    getter.setParameter("point", time);
                                    getter.setParameter("sid", systemID);
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

  public static List<SovereigntyMap> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector allianceID,
      final AttributeSelector corporationID,
      final AttributeSelector factionID,
      final AttributeSelector systemID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM SovereigntyMap c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeSelector.addIntSelector(qs, "c", "allianceID", allianceID);
                                    AttributeSelector.addIntSelector(qs, "c", "corporationID", corporationID);
                                    AttributeSelector.addIntSelector(qs, "c", "factionID", factionID);
                                    AttributeSelector.addIntSelector(qs, "c", "systemID", systemID);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<SovereigntyMap> query = EveKitRefDataProvider.getFactory()
                                                                                            .getEntityManager()
                                                                                            .createQuery(qs.toString(), SovereigntyMap.class);
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
