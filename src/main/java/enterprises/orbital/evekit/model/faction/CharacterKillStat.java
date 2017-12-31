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
    name = "evekit_eve_charkillstat")
@NamedQueries({
    @NamedQuery(
        name = "CharacterKillStat.get",
        query = "SELECT c FROM CharacterKillStat c WHERE c.attribute = :attr AND c.characterID = :charid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class CharacterKillStat extends AbstractKillStat {
  private static final Logger log = Logger.getLogger(CharacterKillStat.class.getName());
  private int characterID;

  protected CharacterKillStat() {
    super(StatAttribute.TOTAL, 0);
  }

  public CharacterKillStat(StatAttribute attribute, int kills, int characterID) {
    super(attribute, kills);
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
    if (!(sup instanceof CharacterKillStat)) return false;
    if (!super.equivalent(sup)) return false;
    CharacterKillStat other = (CharacterKillStat) sup;
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
    CharacterKillStat that = (CharacterKillStat) o;
    return characterID == that.characterID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), characterID);
  }

  @Override
  public String toString() {
    return "CharacterKillStat{" +
        "characterID=" + characterID +
        ", attribute=" + attribute +
        ", kills=" + kills +
        '}';
  }

  public static CharacterKillStat get(
      final long time,
      final StatAttribute attr,
      final int characterID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<CharacterKillStat> getter = EveKitRefDataProvider.getFactory()
                                                                                                .getEntityManager()
                                                                                                .createNamedQuery("CharacterKillStat.get",
                                                                                                                  CharacterKillStat.class);
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

  public static List<CharacterKillStat> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector attribute,
      final AttributeSelector characterID,
      final AttributeSelector kills) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM CharacterKillStat c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeParameters p = new AttributeParameters("att");
                                    AttributeSelector.addEnumSelector(qs, "c", "attribute", attribute, StatAttribute::valueOf, p);
                                    AttributeSelector.addIntSelector(qs, "c", "characterID", characterID);
                                    AttributeSelector.addIntSelector(qs, "c", "kills", kills);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<CharacterKillStat> query = EveKitRefDataProvider.getFactory()
                                                                                               .getEntityManager()
                                                                                               .createQuery(qs.toString(), CharacterKillStat.class);
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
