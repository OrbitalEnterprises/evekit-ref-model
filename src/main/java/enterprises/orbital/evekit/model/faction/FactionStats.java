package enterprises.orbital.evekit.model.faction;

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
    name = "evekit_eve_factionstats")
@NamedQueries({
    @NamedQuery(
        name = "FactionStats.get",
        query = "SELECT c FROM FactionStats c WHERE c.factionID = :fid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class FactionStats extends RefCachedData {
  private static final Logger log = Logger.getLogger(FactionStats.class.getName());
  private int factionID;
  private int killsLastWeek;
  private int killsTotal;
  private int killsYesterday;
  private int pilots;
  private int systemsControlled;
  private int victoryPointsLastWeek;
  private int victoryPointsTotal;
  private int victoryPointsYesterday;

  @SuppressWarnings("unused")
  protected FactionStats() {}

  public FactionStats(int factionID, int killsLastWeek, int killsTotal, int killsYesterday, int pilots,
                      int systemsControlled,
                      int victoryPointsLastWeek, int victoryPointsTotal, int victoryPointsYesterday) {
    super();
    this.factionID = factionID;
    this.killsLastWeek = killsLastWeek;
    this.killsTotal = killsTotal;
    this.killsYesterday = killsYesterday;
    this.pilots = pilots;
    this.systemsControlled = systemsControlled;
    this.victoryPointsLastWeek = victoryPointsLastWeek;
    this.victoryPointsTotal = victoryPointsTotal;
    this.victoryPointsYesterday = victoryPointsYesterday;
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
    if (!(sup instanceof FactionStats)) return false;
    FactionStats other = (FactionStats) sup;
    return factionID == other.factionID && killsLastWeek == other.killsLastWeek
        && killsTotal == other.killsTotal && killsYesterday == other.killsYesterday && pilots == other.pilots && systemsControlled == other.systemsControlled
        && victoryPointsLastWeek == other.victoryPointsLastWeek && victoryPointsTotal == other.victoryPointsTotal
        && victoryPointsYesterday == other.victoryPointsYesterday;
  }

  public int getFactionID() {
    return factionID;
  }

  public int getKillsLastWeek() {
    return killsLastWeek;
  }

  public int getKillsTotal() {
    return killsTotal;
  }

  public int getKillsYesterday() {
    return killsYesterday;
  }

  public int getPilots() {
    return pilots;
  }

  public int getSystemsControlled() {
    return systemsControlled;
  }

  public int getVictoryPointsLastWeek() {
    return victoryPointsLastWeek;
  }

  public int getVictoryPointsTotal() {
    return victoryPointsTotal;
  }

  public int getVictoryPointsYesterday() {
    return victoryPointsYesterday;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    FactionStats that = (FactionStats) o;
    return factionID == that.factionID &&
        killsLastWeek == that.killsLastWeek &&
        killsTotal == that.killsTotal &&
        killsYesterday == that.killsYesterday &&
        pilots == that.pilots &&
        systemsControlled == that.systemsControlled &&
        victoryPointsLastWeek == that.victoryPointsLastWeek &&
        victoryPointsTotal == that.victoryPointsTotal &&
        victoryPointsYesterday == that.victoryPointsYesterday;
  }

  @Override
  public int hashCode() {

    return Objects.hash(super.hashCode(), factionID, killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal, victoryPointsYesterday);
  }

  @Override
  public String toString() {
    return "FactionStats{" +
        "factionID=" + factionID +
        ", killsLastWeek=" + killsLastWeek +
        ", killsTotal=" + killsTotal +
        ", killsYesterday=" + killsYesterday +
        ", pilots=" + pilots +
        ", systemsControlled=" + systemsControlled +
        ", victoryPointsLastWeek=" + victoryPointsLastWeek +
        ", victoryPointsTotal=" + victoryPointsTotal +
        ", victoryPointsYesterday=" + victoryPointsYesterday +
        '}';
  }

  public static FactionStats get(
      final long time,
      final int factionID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<FactionStats> getter = EveKitRefDataProvider.getFactory()
                                                                                           .getEntityManager()
                                                                                           .createNamedQuery("FactionStats.get", FactionStats.class);
                                    getter.setParameter("point", time);
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

  public static List<FactionStats> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector factionID,
      final AttributeSelector killsLastWeek,
      final AttributeSelector killsTotal,
      final AttributeSelector killsYesterday,
      final AttributeSelector pilots,
      final AttributeSelector systemsControlled,
      final AttributeSelector victoryPointsLastWeek,
      final AttributeSelector victoryPointsTotal,
      final AttributeSelector victoryPointsYesterday) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM FactionStats c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeSelector.addIntSelector(qs, "c", "factionID", factionID);
                                    AttributeSelector.addIntSelector(qs, "c", "killsLastWeek", killsLastWeek);
                                    AttributeSelector.addIntSelector(qs, "c", "killsTotal", killsTotal);
                                    AttributeSelector.addIntSelector(qs, "c", "killsYesterday", killsYesterday);
                                    AttributeSelector.addIntSelector(qs, "c", "pilots", pilots);
                                    AttributeSelector.addIntSelector(qs, "c", "systemsControlled", systemsControlled);
                                    AttributeSelector.addIntSelector(qs, "c", "victoryPointsLastWeek", victoryPointsLastWeek);
                                    AttributeSelector.addIntSelector(qs, "c", "victoryPointsTotal", victoryPointsTotal);
                                    AttributeSelector.addIntSelector(qs, "c", "victoryPointsYesterday", victoryPointsYesterday);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<FactionStats> query = EveKitRefDataProvider.getFactory()
                                                                                          .getEntityManager()
                                                                                          .createQuery(qs.toString(), FactionStats.class);
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
