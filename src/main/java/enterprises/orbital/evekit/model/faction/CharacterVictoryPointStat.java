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
    name = "evekit_eve_charvpstat")
@NamedQueries({
    @NamedQuery(
        name = "CharacterVictoryPointStat.get",
        query = "SELECT c FROM CharacterVictoryPointStat c WHERE c.attribute = :attr AND c.characterID = :charid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class CharacterVictoryPointStat extends AbstractVictoryPointStat {
  private static final Logger log = Logger.getLogger(CharacterVictoryPointStat.class.getName());
  private int characterID;

  protected CharacterVictoryPointStat() {
    super(StatAttribute.TOTAL, 0);
  }

  public CharacterVictoryPointStat(StatAttribute attribute, int victoryPoints, int characterID) {
    super(attribute, victoryPoints);
    this.characterID = characterID;
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
    if (!(sup instanceof CharacterVictoryPointStat)) return false;
    if (!super.equivalent(sup)) return false;
    CharacterVictoryPointStat other = (CharacterVictoryPointStat) sup;
    return characterID == other.characterID;
  }

  public int getCharacterID() {
    return characterID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterVictoryPointStat that = (CharacterVictoryPointStat) o;
    return characterID == that.characterID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), characterID);
  }

  @Override
  public String toString() {
    return "CharacterVictoryPointStat{" +
        "characterID=" + characterID +
        ", attribute=" + attribute +
        ", victoryPoints=" + victoryPoints +
        '}';
  }

  public static CharacterVictoryPointStat get(
      final long time,
      final StatAttribute attr,
      final int characterID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<CharacterVictoryPointStat> getter = EveKitRefDataProvider.getFactory()
                                                                                                        .getEntityManager()
                                                                                                        .createNamedQuery("CharacterVictoryPointStat.get", CharacterVictoryPointStat.class);
                                    getter.setParameter("point", time);
                                    getter.setParameter("attr", attr);
                                    getter.setParameter("charid", characterID);
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

  public static List<CharacterVictoryPointStat> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector attribute,
      final AttributeSelector characterID,
      final AttributeSelector victoryPoints) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM CharacterVictoryPointStat c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeParameters p = new AttributeParameters("att");
                                    AttributeSelector.addEnumSelector(qs, "c", "attribute", attribute, StatAttribute::valueOf, p);
                                    AttributeSelector.addIntSelector(qs, "c", "characterID", characterID);
                                    AttributeSelector.addIntSelector(qs, "c", "victoryPoints", victoryPoints);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<CharacterVictoryPointStat> query = EveKitRefDataProvider.getFactory()
                                                                                                       .getEntityManager()
                                                                                                       .createQuery(qs.toString(),
                                                                                                                    CharacterVictoryPointStat.class);
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
