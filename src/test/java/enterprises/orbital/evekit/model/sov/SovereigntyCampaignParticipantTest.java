package enterprises.orbital.evekit.model.sov;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;
import org.junit.Assert;
import org.junit.Test;

public class SovereigntyCampaignParticipantTest extends AbstractRefModelTester<SovereigntyCampaignParticipant> {

  private final int campaignID = TestBase.getRandomInt(100000000);
  private final int allianceID = TestBase.getRandomInt(100000000);
  private final float score = (float) TestBase.getRandomDouble(1000);
  private final ClassUnderTestConstructor<SovereigntyCampaignParticipant> eol = () -> new SovereigntyCampaignParticipant(campaignID, allianceID, score);
  private final ClassUnderTestConstructor<SovereigntyCampaignParticipant> live = () -> new SovereigntyCampaignParticipant(campaignID, allianceID, score + 1.0F );

  @Test
  public void testBasic() {
    runBasicTests(eol, () -> new SovereigntyCampaignParticipant[]{
        new SovereigntyCampaignParticipant(campaignID + 1, allianceID, score),
        new SovereigntyCampaignParticipant(campaignID, allianceID + 1, score),
        new SovereigntyCampaignParticipant(campaignID, allianceID, score + 1)
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (long time) -> SovereigntyCampaignParticipant.get(time, campaignID, allianceID));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different campaign ID
    // - objects with different alliance ID
    // - objects not live at the given time
    SovereigntyCampaignParticipant existing, keyed;

    keyed = new SovereigntyCampaignParticipant(campaignID, allianceID, score);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Different campaign ID
    existing = new SovereigntyCampaignParticipant(campaignID + 1, allianceID, score);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Different alliance ID
    existing = new SovereigntyCampaignParticipant(campaignID, allianceID + 1, score);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Not live at the given time
    existing = new SovereigntyCampaignParticipant(campaignID, allianceID, score + 1.0F);
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new SovereigntyCampaignParticipant(campaignID, allianceID, score + 2.0F);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    SovereigntyCampaignParticipant result = SovereigntyCampaignParticipant.get(8889L, campaignID, allianceID);
    Assert.assertEquals(keyed, result);
  }

}
