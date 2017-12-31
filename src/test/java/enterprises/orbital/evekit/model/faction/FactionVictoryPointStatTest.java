package enterprises.orbital.evekit.model.faction;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;
import org.junit.Assert;
import org.junit.Test;

public class FactionVictoryPointStatTest extends AbstractRefModelTester<FactionVictoryPointStat> {

  private final StatAttribute attribute = StatAttribute.LAST_WEEK;
  private final int factionID = TestBase.getRandomInt(100000000);
  private final int victoryPoints = TestBase.getRandomInt(100000000);

  private final ClassUnderTestConstructor<FactionVictoryPointStat> eol = () -> new FactionVictoryPointStat(attribute, victoryPoints, factionID);
  private final ClassUnderTestConstructor<FactionVictoryPointStat> live = () -> new FactionVictoryPointStat(attribute, victoryPoints + 1, factionID);

  @Test
  public void testBasic() {
    runBasicTests(eol, () -> new FactionVictoryPointStat[]{
        new FactionVictoryPointStat(StatAttribute.TOTAL, victoryPoints, factionID),
        new FactionVictoryPointStat(attribute, victoryPoints + 1, factionID),
        new FactionVictoryPointStat(attribute, victoryPoints, factionID + 1),
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (long time) -> FactionVictoryPointStat.get(time, attribute, factionID));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different faction ID
    // - objects with different attribute
    // - objects not live at the given time
    FactionVictoryPointStat existing, keyed;

    keyed = new FactionVictoryPointStat(attribute, victoryPoints, factionID);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Different faction ID
    existing = new FactionVictoryPointStat(attribute, victoryPoints, factionID + 1);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Different attribute
    existing = new FactionVictoryPointStat(StatAttribute.TOTAL, victoryPoints, factionID);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Not live at the given time
    existing = new FactionVictoryPointStat(attribute, victoryPoints + 1, factionID);
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new FactionVictoryPointStat(attribute, victoryPoints + 2, factionID);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    FactionVictoryPointStat result = FactionVictoryPointStat.get(8889L, attribute, factionID);
    Assert.assertEquals(keyed, result);
  }

}
