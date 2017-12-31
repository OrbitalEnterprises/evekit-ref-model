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
    name = "evekit_map_facwarsystem")
@NamedQueries({
    @NamedQuery(
        name = "FactionWarSystem.get",
        query = "SELECT c FROM FactionWarSystem c WHERE c.solarSystemID = :sid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class FactionWarSystem extends RefCachedData {
  private static final Logger log = Logger.getLogger(FactionWarSystem.class.getName());
  private int occupyingFactionID;
  private int owningFactionID;
  private int solarSystemID;
  private int victoryPoints;
  private int victoryPointsThreshold;
  private boolean contested;

  @SuppressWarnings("unused")
  protected FactionWarSystem() {}

  public FactionWarSystem(int occupyingFactionID, int owningFactionID, int solarSystemID, int victoryPoints,
                          int victoryPointsThreshold, boolean contested) {
    this.occupyingFactionID = occupyingFactionID;
    this.owningFactionID = owningFactionID;
    this.solarSystemID = solarSystemID;
    this.victoryPoints = victoryPoints;
    this.victoryPointsThreshold = victoryPointsThreshold;
    this.contested = contested;
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
    if (!(sup instanceof FactionWarSystem)) return false;
    FactionWarSystem other = (FactionWarSystem) sup;
    return occupyingFactionID == other.occupyingFactionID
        && owningFactionID == other.owningFactionID
        && solarSystemID == other.solarSystemID
        && victoryPoints == other.victoryPoints
        && victoryPointsThreshold == other.victoryPointsThreshold
        && contested == other.contested;
  }

  public int getOccupyingFactionID() {
    return occupyingFactionID;
  }

  public int getOwningFactionID() {
    return owningFactionID;
  }

  public int getSolarSystemID() {
    return solarSystemID;
  }

  public int getVictoryPoints() {
    return victoryPoints;
  }

  public int getVictoryPointsThreshold() {
    return victoryPointsThreshold;
  }

  public boolean isContested() {
    return contested;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    FactionWarSystem that = (FactionWarSystem) o;
    return occupyingFactionID == that.occupyingFactionID &&
        owningFactionID == that.owningFactionID &&
        solarSystemID == that.solarSystemID &&
        victoryPoints == that.victoryPoints &&
        victoryPointsThreshold == that.victoryPointsThreshold &&
        contested == that.contested;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), occupyingFactionID, owningFactionID, solarSystemID, victoryPoints, victoryPointsThreshold, contested);
  }

  @Override
  public String toString() {
    return "FactionWarSystem{" +
        "occupyingFactionID=" + occupyingFactionID +
        ", owningFactionID=" + owningFactionID +
        ", solarSystemID=" + solarSystemID +
        ", victoryPoints=" + victoryPoints +
        ", victoryPointsThreshold=" + victoryPointsThreshold +
        ", contested=" + contested +
        '}';
  }

  public static FactionWarSystem get(
      final long time,
      final int solarSystemID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<FactionWarSystem> getter = EveKitRefDataProvider.getFactory()
                                                                                               .getEntityManager()
                                                                                               .createNamedQuery("FactionWarSystem.get",
                                                                                                                 FactionWarSystem.class);
                                    getter.setParameter("point", time);
                                    getter.setParameter("sid", solarSystemID);
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

  public static List<FactionWarSystem> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector occupyingFactionID,
      final AttributeSelector owningFactionID,
      final AttributeSelector solarSystemID,
      final AttributeSelector victoryPoints,
      final AttributeSelector victoryPointsThreshold,
      final AttributeSelector contested) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM FactionWarSystem c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeParameters p = new AttributeParameters("att");
                                    AttributeSelector.addIntSelector(qs, "c", "occupyingFactionID", occupyingFactionID);
                                    AttributeSelector.addIntSelector(qs, "c", "owningFactionID", owningFactionID);
                                    AttributeSelector.addIntSelector(qs, "c", "solarSystemID", solarSystemID);
                                    AttributeSelector.addIntSelector(qs, "c", "victoryPoints", victoryPoints);
                                    AttributeSelector.addIntSelector(qs, "c", "victoryPointsThreshold", victoryPointsThreshold);
                                    AttributeSelector.addBooleanSelector(qs, "c", "contested", contested);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<FactionWarSystem> query = EveKitRefDataProvider.getFactory()
                                                                                              .getEntityManager()
                                                                                              .createQuery(qs.toString(), FactionWarSystem.class);
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
