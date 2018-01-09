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
    name = "evekit_eve_corpvpstat")
@NamedQueries({
    @NamedQuery(
        name = "CorporationVictoryPointStat.get",
        query = "SELECT c FROM CorporationVictoryPointStat c WHERE c.attribute = :attr AND c.corporationID = :corpid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class CorporationVictoryPointStat extends AbstractVictoryPointStat {
  private static final Logger log = Logger.getLogger(CorporationVictoryPointStat.class.getName());
  private int corporationID;

  protected CorporationVictoryPointStat() {
    super(StatAttribute.TOTAL, 0);
  }

  public CorporationVictoryPointStat(StatAttribute attribute, int victoryPoints, int corporationID) {
    super(attribute, victoryPoints);
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
    if (!(sup instanceof CorporationVictoryPointStat)) return false;
    if (!super.equivalent(sup)) return false;
    CorporationVictoryPointStat other = (CorporationVictoryPointStat) sup;
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
    CorporationVictoryPointStat that = (CorporationVictoryPointStat) o;
    return corporationID == that.corporationID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), corporationID);
  }

  @Override
  public String toString() {
    return "CorporationVictoryPointStat{" +
        "corporationID=" + corporationID +
        ", attribute=" + attribute +
        ", victoryPoints=" + victoryPoints +
        '}';
  }

  public static CorporationVictoryPointStat get(
      final long time,
      final StatAttribute attr,
      final int corporationID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<CorporationVictoryPointStat> getter = EveKitRefDataProvider.getFactory()
                                                                                                          .getEntityManager()
                                                                                                          .createNamedQuery("CorporationVictoryPointStat.get", CorporationVictoryPointStat.class);
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

  public static List<CorporationVictoryPointStat> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector attribute,
      final AttributeSelector corporationID,
      final AttributeSelector victoryPoints) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM CorporationVictoryPointStat c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeParameters p = new AttributeParameters("att");
                                    AttributeSelector.addEnumSelector(qs, "c", "attribute", attribute, StatAttribute::valueOf, p);
                                    AttributeSelector.addIntSelector(qs, "c", "corporationID", corporationID);
                                    AttributeSelector.addIntSelector(qs, "c", "victoryPoints", victoryPoints);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<CorporationVictoryPointStat> query = EveKitRefDataProvider.getFactory()
                                                                                                         .getEntityManager()
                                                                                                         .createQuery(qs.toString(),
                                                                                                                      CorporationVictoryPointStat.class);
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
