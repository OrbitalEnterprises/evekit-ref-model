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
    name = "evekit_eve_factionwar")
@NamedQueries({
    @NamedQuery(
        name = "FactionWar.get",
        query = "SELECT c FROM FactionWar c WHERE c.againstID = :aid AND c.factionID = :fid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class FactionWar extends RefCachedData {
  private static final Logger log = Logger.getLogger(FactionWar.class.getName());
  private int againstID;
  private int factionID;

  @SuppressWarnings("unused")
  protected FactionWar() {}

  public FactionWar(int againstID, int factionID) {
    super();
    this.againstID = againstID;
    this.factionID = factionID;
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
    if (!(sup instanceof FactionWar)) return false;
    FactionWar other = (FactionWar) sup;
    return againstID == other.againstID && factionID == other.factionID;
  }

  public int getAgainstID() {
    return againstID;
  }

  public int getFactionID() {
    return factionID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    FactionWar that = (FactionWar) o;
    return againstID == that.againstID &&
        factionID == that.factionID;
  }

  @Override
  public int hashCode() {

    return Objects.hash(super.hashCode(), againstID, factionID);
  }

  @Override
  public String toString() {
    return "FactionWar{" +
        "againstID=" + againstID +
        ", factionID=" + factionID +
        '}';
  }

  public static FactionWar get(
      final long time,
      final int againstID,
      final int factionID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<FactionWar> getter = EveKitRefDataProvider.getFactory()
                                                                                         .getEntityManager()
                                                                                         .createNamedQuery("FactionWar.get", FactionWar.class);
                                    getter.setParameter("point", time);
                                    getter.setParameter("aid", againstID);
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

  public static List<FactionWar> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector againstID,
      final AttributeSelector factionID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM FactionWar c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeSelector.addIntSelector(qs, "c", "againstID", againstID);
                                    AttributeSelector.addIntSelector(qs, "c", "factionID", factionID);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<FactionWar> query = EveKitRefDataProvider.getFactory()
                                                                                        .getEntityManager()
                                                                                        .createQuery(qs.toString(), FactionWar.class);
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
