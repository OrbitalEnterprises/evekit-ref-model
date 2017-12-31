package enterprises.orbital.evekit.model.faction;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;
import org.junit.Assert;
import org.junit.Test;

public class FactionKillStatTest extends AbstractRefModelTester<FactionKillStat> {

  private final StatAttribute attribute = StatAttribute.LAST_WEEK;
  private final int factionID = TestBase.getRandomInt(100000000);
  private final int kills = TestBase.getRandomInt(100000000);

  private final ClassUnderTestConstructor<FactionKillStat> eol = () -> new FactionKillStat(attribute, kills, factionID);
  private final ClassUnderTestConstructor<FactionKillStat> live = () -> new FactionKillStat(attribute, kills + 1, factionID);

  @Test
  public void testBasic() {
    runBasicTests(eol, () -> new FactionKillStat[]{
        new FactionKillStat(StatAttribute.YESTERDAY, kills, factionID), new FactionKillStat(attribute, kills + 1, factionID),
        new FactionKillStat(attribute, kills, factionID + 1)
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (long time) -> FactionKillStat.get(time, attribute, factionID));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different faction ID
    // - objects with different attribute
    // - objects not live at the given time
    FactionKillStat existing, keyed;

    keyed = new FactionKillStat(attribute, kills, factionID);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Different faction ID
    existing = new FactionKillStat(attribute, kills, factionID + 1);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Different attribute
    existing = new FactionKillStat(StatAttribute.YESTERDAY, kills, factionID);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Not live at the given time
    existing = new FactionKillStat(attribute, kills + 1, factionID);
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new FactionKillStat(attribute, kills + 2, factionID);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    FactionKillStat result = FactionKillStat.get(8889L, attribute, factionID);
    Assert.assertEquals(keyed, result);
  }

}
