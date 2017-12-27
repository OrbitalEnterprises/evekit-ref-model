package enterprises.orbital.evekit.model.eve;

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
    name = "evekit_eve_alliance_icon")
@NamedQueries({
    @NamedQuery(
        name = "AllianceIcon.get",
        query = "SELECT c FROM AllianceIcon c WHERE c.allianceID = :allianceid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class AllianceIcon extends RefCachedData {
  private static final Logger log = Logger.getLogger(AllianceIcon.class.getName());
  private long allianceID;
  private String px64x64;
  private String px128x128;

  @SuppressWarnings("unused")
  protected AllianceIcon() {}

  public AllianceIcon(long allianceID, String px64x64, String px128x128) {
    super();
    this.allianceID = allianceID;
    this.px64x64 = px64x64;
    this.px128x128 = px128x128;
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
    if (!(sup instanceof AllianceIcon)) return false;
    AllianceIcon other = (AllianceIcon) sup;
    return allianceID == other.allianceID && nullSafeObjectCompare(px64x64, other.px64x64) && nullSafeObjectCompare(px128x128, other.px128x128);
  }

  public long getAllianceID() {
    return allianceID;
  }

  public String getPx64x64() {
    return px64x64;
  }

  public String getPx128x128() {
    return px128x128;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    AllianceIcon that = (AllianceIcon) o;
    return allianceID == that.allianceID &&
        Objects.equals(px64x64, that.px64x64) &&
        Objects.equals(px128x128, that.px128x128);
  }

  @Override
  public int hashCode() {

    return Objects.hash(super.hashCode(), allianceID, px64x64, px128x128);
  }

  @Override
  public String toString() {
    return "AllianceIcon{" +
        "allianceID=" + allianceID +
        ", px64x64='" + px64x64 + '\'' +
        ", px128x128='" + px128x128 + '\'' +
        '}';
  }

  public static AllianceIcon get(
      final long time,
      final long allianceID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<AllianceIcon> getter = EveKitRefDataProvider.getFactory()
                                                                                           .getEntityManager()
                                                                                           .createNamedQuery("AllianceIcon.get", AllianceIcon.class);
                                    getter.setParameter("point", time);
                                    getter.setParameter("allianceid", allianceID);
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

  public static List<AllianceIcon> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector allianceID,
      final AttributeSelector px64x64,
      final AttributeSelector px128x128) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM AllianceIcon c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeParameters p = new AttributeParameters("att");
                                    AttributeSelector.addLongSelector(qs, "c", "allianceID", allianceID);
                                    AttributeSelector.addStringSelector(qs, "c", "px64x64", px64x64, p);
                                    AttributeSelector.addStringSelector(qs, "c", "px128x128", px128x128, p);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<AllianceIcon> query = EveKitRefDataProvider.getFactory()
                                                                                          .getEntityManager()
                                                                                          .createQuery(qs.toString(),
                                                                                                       AllianceIcon.class);
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
