package enterprises.orbital.evekit.model.faction;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;
import org.junit.Assert;
import org.junit.Test;

public class CorporationVictoryPointStatTest extends AbstractRefModelTester<CorporationVictoryPointStat> {

  private final StatAttribute attribute = StatAttribute.LAST_WEEK;
  private final int corporationID = TestBase.getRandomInt(100000000);
  private final int victoryPoints = TestBase.getRandomInt(100000000);

  private final ClassUnderTestConstructor<CorporationVictoryPointStat> eol = () -> new CorporationVictoryPointStat(
      attribute, victoryPoints, corporationID);
  private final ClassUnderTestConstructor<CorporationVictoryPointStat> live = () -> new CorporationVictoryPointStat(
      attribute, victoryPoints + 1, corporationID);

  @Test
  public void testBasic() {
    runBasicTests(eol, () -> new CorporationVictoryPointStat[]{
        new CorporationVictoryPointStat(StatAttribute.TOTAL, victoryPoints, corporationID),
        new CorporationVictoryPointStat(attribute, victoryPoints + 1, corporationID),
        new CorporationVictoryPointStat(attribute, victoryPoints, corporationID + 1)
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (long time) -> CorporationVictoryPointStat.get(time, attribute, corporationID));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different corporation ID
    // - objects with different attribute
    // - objects not live at the given time
    CorporationVictoryPointStat existing, keyed;

    keyed = new CorporationVictoryPointStat(attribute, victoryPoints, corporationID);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Different corporation ID
    existing = new CorporationVictoryPointStat(attribute, victoryPoints, corporationID + 1);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Different attribute
    existing = new CorporationVictoryPointStat(StatAttribute.TOTAL, victoryPoints, corporationID);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Not live at the given time
    existing = new CorporationVictoryPointStat(attribute, victoryPoints + 1, corporationID);
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new CorporationVictoryPointStat(attribute, victoryPoints + 2, corporationID);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    CorporationVictoryPointStat result = CorporationVictoryPointStat.get(8889L, attribute, corporationID);
    Assert.assertEquals(keyed, result);
  }

}
