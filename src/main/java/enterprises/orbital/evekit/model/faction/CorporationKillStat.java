package enterprises.orbital.evekit.model.faction;

import enterprises.orbital.evekit.account.EveKitRefDataProvider;
import enterprises.orbital.evekit.model.AttributeParameters;
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
    name = "evekit_eve_corpkillstat")
@NamedQueries({
    @NamedQuery(
        name = "CorporationKillStat.get",
        query = "SELECT c FROM CorporationKillStat c WHERE c.attribute = :attr AND c.corporationID = :corpid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class CorporationKillStat extends AbstractKillStat {
  private static final Logger log = Logger.getLogger(CorporationKillStat.class.getName());
  private int corporationID;

  protected CorporationKillStat() {
    super(StatAttribute.TOTAL, 0);
  }

  public CorporationKillStat(StatAttribute attribute, int kills, int corporationID) {
    super(attribute, kills);
    this.corporationID = corporationID;
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
    if (!(sup instanceof CorporationKillStat)) return false;
    if (!super.equivalent(sup)) return false;
    CorporationKillStat other = (CorporationKillStat) sup;
    return corporationID == other.corporationID;
  }

  public int getCorporationID() {
    return corporationID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CorporationKillStat that = (CorporationKillStat) o;
    return corporationID == that.corporationID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), corporationID);
  }

  @Override
  public String toString() {
    return "CorporationKillStat{" +
        "corporationID=" + corporationID +
        ", attribute=" + attribute +
        ", kills=" + kills +
        '}';
  }

  public static CorporationKillStat get(
      final long time,
      final StatAttribute attr,
      final int corporationID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<CorporationKillStat> getter = EveKitRefDataProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createNamedQuery("CorporationKillStat.get",
                                                                                                                    CorporationKillStat.class);
                                    getter.setParameter("point", time);
                                    getter.setParameter("attr", attr);
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

  public static List<CorporationKillStat> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector attribute,
      final AttributeSelector corporationID,
      final AttributeSelector kills) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM CorporationKillStat c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeParameters p = new AttributeParameters("att");
                                    AttributeSelector.addEnumSelector(qs, "c", "attribute", attribute, StatAttribute::valueOf, p);
                                    AttributeSelector.addIntSelector(qs, "c", "corporationID", corporationID);
                                    AttributeSelector.addIntSelector(qs, "c", "kills", kills);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<CorporationKillStat> query = EveKitRefDataProvider.getFactory()
                                                                                                 .getEntityManager()
                                                                                                 .createQuery(qs.toString(), CorporationKillStat.class);
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
