package enterprises.orbital.evekit.model.eve;

import enterprises.orbital.evekit.account.EveKitRefDataProvider;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.RefCachedData;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_eve_alliance_member")
@NamedQueries({
    @NamedQuery(
        name = "AllianceMemberCorporation.get",
        query = "SELECT c FROM AllianceMemberCorporation c WHERE c.allianceID = :allianceid AND c.corporationID = :corpid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class AllianceMemberCorporation extends RefCachedData {
  private static final Logger log = Logger.getLogger(AllianceMemberCorporation.class.getName());
  private long allianceID;
  private long corporationID;

  @SuppressWarnings("unused")
  protected AllianceMemberCorporation() {}

  public AllianceMemberCorporation(long allianceID, long corporationID) {
    super();
    this.allianceID = allianceID;
    this.corporationID = corporationID;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      RefCachedData sup) {
    if (!(sup instanceof AllianceMemberCorporation)) return false;
    AllianceMemberCorporation other = (AllianceMemberCorporation) sup;
    return allianceID == other.allianceID && corporationID == other.corporationID;
  }

  public long getAllianceID() {
    return allianceID;
  }

  public long getCorporationID() {
    return corporationID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (allianceID ^ (allianceID >>> 32));
    result = prime * result + (int) (corporationID ^ (corporationID >>> 32));
    return result;
  }

  @Override
  public boolean equals(
      Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    AllianceMemberCorporation other = (AllianceMemberCorporation) obj;
    if (allianceID != other.allianceID) return false;
    if (corporationID != other.corporationID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "AllianceMemberCorporation{" +
        "allianceID=" + allianceID +
        ", corporationID=" + corporationID +
        '}';
  }

  public static AllianceMemberCorporation get(
      final long time,
      final long allianceID,
      final long corporationID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<AllianceMemberCorporation> getter = EveKitRefDataProvider.getFactory()
                                                                                                        .getEntityManager()
                                                                                                        .createNamedQuery("AllianceMemberCorporation.get", AllianceMemberCorporation.class);
                                    getter.setParameter("point", time);
                                    getter.setParameter("allianceid", allianceID);
                                    getter.setParameter("corpid", corporationID);
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

  public static List<AllianceMemberCorporation> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector allianceID,
      final AttributeSelector corporationID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM AllianceMemberCorporation c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeSelector.addLongSelector(qs, "c", "allianceID", allianceID);
                                    AttributeSelector.addLongSelector(qs, "c", "corporationID", corporationID);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<AllianceMemberCorporation> query = EveKitRefDataProvider.getFactory()
                                                                                                       .getEntityManager()
                                                                                                       .createQuery(qs.toString(),
                                                                                                                    AllianceMemberCorporation.class);
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
