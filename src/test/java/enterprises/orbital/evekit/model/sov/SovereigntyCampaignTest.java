package enterprises.orbital.evekit.model.sov;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;
import org.junit.Assert;
import org.junit.Test;

public class SovereigntyCampaignTest extends AbstractRefModelTester<SovereigntyCampaign> {

  private final int campaignID = TestBase.getRandomInt(100000000);
  private final long structureID = TestBase.getRandomInt(100000000);
  private final int systemID = TestBase.getRandomInt(100000000);
  private final int constellationID = TestBase.getRandomInt(100000000);
  private final String eventType = TestBase.getRandomText(50);
  private final long startTime = TestBase.getRandomLong();
  private final int defenderID = TestBase.getRandomInt(100000000);
  private final float defenderScore = (float) TestBase.getRandomDouble(1000);
  private final float attackersScore = (float) TestBase.getRandomDouble(1000);
  private final ClassUnderTestConstructor<SovereigntyCampaign> eol = () -> new SovereigntyCampaign(campaignID, structureID, systemID, constellationID, eventType, startTime, defenderID, defenderScore, attackersScore);
  private final ClassUnderTestConstructor<SovereigntyCampaign> live = () -> new SovereigntyCampaign(campaignID, structureID + 1, systemID, constellationID, eventType, startTime, defenderID, defenderScore, attackersScore);

  @Test
  public void testBasic() {
    runBasicTests(eol, () -> new SovereigntyCampaign[]{
        new SovereigntyCampaign(campaignID + 1, structureID, systemID, constellationID, eventType, startTime, defenderID, defenderScore, attackersScore),
        new SovereigntyCampaign(campaignID, structureID + 1, systemID, constellationID, eventType, startTime, defenderID, defenderScore, attackersScore),
        new SovereigntyCampaign(campaignID, structureID, systemID + 1, constellationID, eventType, startTime, defenderID, defenderScore, attackersScore),
        new SovereigntyCampaign(campaignID, structureID, systemID, constellationID + 1, eventType, startTime, defenderID, defenderScore, attackersScore),
        new SovereigntyCampaign(campaignID, structureID, systemID, constellationID, eventType + "1", startTime, defenderID, defenderScore, attackersScore),
        new SovereigntyCampaign(campaignID, structureID, systemID, constellationID, eventType, startTime + 1L, defenderID, defenderScore, attackersScore),
        new SovereigntyCampaign(campaignID, structureID, systemID, constellationID, eventType, startTime, defenderID + 1, defenderScore, attackersScore),
        new SovereigntyCampaign(campaignID, structureID, systemID, constellationID, eventType, startTime, defenderID, defenderScore + 1.0F, attackersScore),
        new SovereigntyCampaign(campaignID, structureID, systemID, constellationID, eventType, startTime, defenderID, defenderScore, attackersScore + 1.0F)
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (long time) -> SovereigntyCampaign.get(time, campaignID));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different campaign ID
    // - objects not live at the given time
    SovereigntyCampaign existing, keyed;

    keyed = new SovereigntyCampaign(campaignID, structureID, systemID, constellationID, eventType, startTime, defenderID, defenderScore, attackersScore);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Different campaign ID
    existing = new SovereigntyCampaign(campaignID + 1, structureID, systemID, constellationID, eventType, startTime, defenderID, defenderScore, attackersScore);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Not live at the given time
    existing = new SovereigntyCampaign(campaignID, structureID + 1, systemID, constellationID, eventType, startTime, defenderID, defenderScore, attackersScore);
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new SovereigntyCampaign(campaignID, structureID + 2, systemID, constellationID, eventType, startTime, defenderID, defenderScore, attackersScore);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    SovereigntyCampaign result = SovereigntyCampaign.get(8889L, campaignID);
    Assert.assertEquals(keyed, result);
  }

}
