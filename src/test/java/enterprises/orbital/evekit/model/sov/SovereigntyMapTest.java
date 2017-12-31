package enterprises.orbital.evekit.model.sov;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;
import org.junit.Assert;
import org.junit.Test;

public class SovereigntyMapTest extends AbstractRefModelTester<SovereigntyMap> {

  private final int allianceID = TestBase.getRandomInt(100000000);
  private final int corporationID = TestBase.getRandomInt(100000000);
  private final int factionID = TestBase.getRandomInt(100000000);
  private final int systemID = TestBase.getRandomInt(100000000);
  private final ClassUnderTestConstructor<SovereigntyMap> eol = () -> new SovereigntyMap(allianceID, corporationID, factionID, systemID);
  private final ClassUnderTestConstructor<SovereigntyMap> live = () -> new SovereigntyMap(allianceID + 1, corporationID, factionID, systemID);

  @Test
  public void testBasic() {
    runBasicTests(eol, () -> new SovereigntyMap[]{
        new SovereigntyMap(allianceID + 1, corporationID, factionID, systemID),
        new SovereigntyMap(allianceID, corporationID + 1, factionID, systemID),
        new SovereigntyMap(allianceID, corporationID, factionID + 1, systemID),
        new SovereigntyMap(allianceID, corporationID, factionID, systemID + 1)
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (long time) -> SovereigntyMap.get(time, systemID));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different system ID
    // - objects not live at the given time
    SovereigntyMap existing, keyed;

    keyed = new SovereigntyMap(allianceID, corporationID, factionID, systemID);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Different system ID
    existing = new SovereigntyMap(allianceID, corporationID, factionID, systemID + 1);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Not live at the given time
    existing = new SovereigntyMap(allianceID + 1, corporationID, factionID, systemID);
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new SovereigntyMap(allianceID + 2, corporationID, factionID, systemID);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    SovereigntyMap result = SovereigntyMap.get(8889L, systemID);
    Assert.assertEquals(keyed, result);
  }

}
