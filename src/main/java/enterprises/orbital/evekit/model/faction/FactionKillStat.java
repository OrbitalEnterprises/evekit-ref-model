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
    name = "evekit_eve_factionkillstat")
@NamedQueries({
    @NamedQuery(
        name = "FactionKillStat.get",
        query = "SELECT c FROM FactionKillStat c WHERE c.attribute = :attr AND c.factionID = :fid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class FactionKillStat extends AbstractKillStat {
  private static final Logger log = Logger.getLogger(FactionKillStat.class.getName());
  private int factionID;

  protected FactionKillStat() {
    super(StatAttribute.TOTAL, 0);
  }

  public FactionKillStat(StatAttribute attribute, int kills, int factionID) {
    super(attribute, kills);
    this.factionID = factionID;
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
    if (!(sup instanceof FactionKillStat)) return false;
    if (!super.equivalent(sup)) return false;
    FactionKillStat other = (FactionKillStat) sup;
    return factionID == other.factionID;
  }

  public int getFactionID() {
    return factionID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    FactionKillStat that = (FactionKillStat) o;
    return factionID == that.factionID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), factionID);
  }

  @Override
  public String toString() {
    return "FactionKillStat{" +
        "factionID=" + factionID +
        ", attribute=" + attribute +
        ", kills=" + kills +
        '}';
  }

  public static FactionKillStat get(
      final long time,
      final StatAttribute attr,
      final int factionID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<FactionKillStat> getter = EveKitRefDataProvider.getFactory()
                                                                                              .getEntityManager()
                                                                                              .createNamedQuery("FactionKillStat.get",
                                                                                                                FactionKillStat.class);
                                    getter.setParameter("point", time);
                                    getter.setParameter("attr", attr);
                                    getter.setParameter("fid", factionID);
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

  public static List<FactionKillStat> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector attribute,
      final AttributeSelector factionID,
      final AttributeSelector kills) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM FactionKillStat c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeParameters p = new AttributeParameters("att");
                                    AttributeSelector.addEnumSelector(qs, "c", "attribute", attribute, StatAttribute::valueOf, p);
                                    AttributeSelector.addIntSelector(qs, "c", "factionID", factionID);
                                    AttributeSelector.addIntSelector(qs, "c", "kills", kills);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<FactionKillStat> query = EveKitRefDataProvider.getFactory()
                                                                                             .getEntityManager()
                                                                                             .createQuery(qs.toString(), FactionKillStat.class);
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
