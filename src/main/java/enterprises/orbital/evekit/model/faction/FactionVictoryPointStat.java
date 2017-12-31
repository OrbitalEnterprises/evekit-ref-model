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
    name = "evekit_eve_factionvpstat")
@NamedQueries({
    @NamedQuery(
        name = "FactionVictoryPointStat.get",
        query = "SELECT c FROM FactionVictoryPointStat c WHERE c.attribute = :attr AND c.factionID = :fid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class FactionVictoryPointStat extends AbstractVictoryPointStat {
  private static final Logger log = Logger.getLogger(FactionVictoryPointStat.class.getName());
  private int factionID;

  protected FactionVictoryPointStat() {
    super(StatAttribute.TOTAL, 0);
  }

  public FactionVictoryPointStat(StatAttribute attribute, int victoryPoints, int factionID) {
    super(attribute, victoryPoints);
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
    if (!(sup instanceof FactionVictoryPointStat)) return false;
    if (!super.equivalent(sup)) return false;
    FactionVictoryPointStat other = (FactionVictoryPointStat) sup;
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
    FactionVictoryPointStat that = (FactionVictoryPointStat) o;
    return factionID == that.factionID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), factionID);
  }

  @Override
  public String toString() {
    return "FactionVictoryPointStat{" +
        "factionID=" + factionID +
        ", attribute=" + attribute +
        ", victoryPoints=" + victoryPoints +
        '}';
  }

  public static FactionVictoryPointStat get(
      final long time,
      final StatAttribute attr,
      final int factionID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<FactionVictoryPointStat> getter = EveKitRefDataProvider.getFactory()
                                                                                                      .getEntityManager()
                                                                                                      .createNamedQuery("FactionVictoryPointStat.get",
                                                                                                                        FactionVictoryPointStat.class);
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

  public static List<FactionVictoryPointStat> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector attribute,
      final AttributeSelector factionID,
      final AttributeSelector victoryPoints) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM FactionVictoryPointStat c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeParameters p = new AttributeParameters("att");
                                    AttributeSelector.addEnumSelector(qs, "c", "attribute", attribute, StatAttribute::valueOf, p);
                                    AttributeSelector.addLongSelector(qs, "c", "factionID", factionID);
                                    AttributeSelector.addIntSelector(qs, "c", "victoryPoints", victoryPoints);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<FactionVictoryPointStat> query = EveKitRefDataProvider.getFactory()
                                                                                                     .getEntityManager()
                                                                                                     .createQuery(qs.toString(),
                                                                                                                  FactionVictoryPointStat.class);
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
