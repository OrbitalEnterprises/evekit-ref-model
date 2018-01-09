package enterprises.orbital.evekit.model.sov;

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
    name = "evekit_sov_campaign_participant")
@NamedQueries({
    @NamedQuery(
        name = "SovereigntyCampaignParticipant.get",
        query = "SELECT c FROM SovereigntyCampaignParticipant c WHERE c.campaignID = :campaignid AND c.allianceID = :allianceid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class SovereigntyCampaignParticipant extends RefCachedData {
  private static final Logger log = Logger.getLogger(SovereigntyCampaignParticipant.class.getName());
  private int campaignID;
  private int allianceID;
  private float score;

  @SuppressWarnings("unused")
  protected SovereigntyCampaignParticipant() {}

  public SovereigntyCampaignParticipant(int campaignID, int allianceID, float score) {
    this.campaignID = campaignID;
    this.allianceID = allianceID;
    this.score = score;
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
    if (!(sup instanceof SovereigntyCampaignParticipant)) return false;
    SovereigntyCampaignParticipant other = (SovereigntyCampaignParticipant) sup;
    return campaignID == other.campaignID && allianceID == other.allianceID && score == other.score;
  }

  public int getCampaignID() {
    return campaignID;
  }

  public int getAllianceID() {
    return allianceID;
  }

  public float getScore() {
    return score;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SovereigntyCampaignParticipant that = (SovereigntyCampaignParticipant) o;
    return campaignID == that.campaignID &&
        allianceID == that.allianceID &&
        Float.compare(that.score, score) == 0;
  }

  @Override
  public int hashCode() {

    return Objects.hash(super.hashCode(), campaignID, allianceID, score);
  }

  @Override
  public String toString() {
    return "SovereigntyCampaignParticipant{" +
        "campaignID=" + campaignID +
        ", allianceID=" + allianceID +
        ", score=" + score +
        '}';
  }

  public static SovereigntyCampaignParticipant get(
      final long time,
      final int campaignID,
      final int allianceID) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    TypedQuery<SovereigntyCampaignParticipant> getter = EveKitRefDataProvider.getFactory()
                                                                                                             .getEntityManager()
                                                                                                             .createNamedQuery("SovereigntyCampaignParticipant.get", SovereigntyCampaignParticipant.class);
                                    getter.setParameter("point", time);
                                    getter.setParameter("campaignid", campaignID);
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

  public static List<SovereigntyCampaignParticipant> accessQuery(
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector campaignID,
      final AttributeSelector allianceID,
      final AttributeSelector score) throws IOException {
    try {
      return EveKitRefDataProvider.getFactory()
                                  .runTransaction(() -> {
                                    StringBuilder qs = new StringBuilder();
                                    qs.append("SELECT c FROM SovereigntyCampaignParticipant c WHERE 1=1");
                                    // Constrain lifeline
                                    AttributeSelector.addLifelineSelector(qs, "c", at);
                                    // Constrain attributes
                                    AttributeSelector.addIntSelector(qs, "c", "campaignID", campaignID);
                                    AttributeSelector.addIntSelector(qs, "c", "allianceID", allianceID);
                                    AttributeSelector.addFloatSelector(qs, "c", "score", score);
                                    // Set CID constraint and ordering
                                    setCIDOrdering(qs, contid, reverse);
                                    // Return result
                                    TypedQuery<SovereigntyCampaignParticipant> query = EveKitRefDataProvider.getFactory()
                                                                                                            .getEntityManager()
                                                                                                            .createQuery(qs.toString(), SovereigntyCampaignParticipant.class);
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
