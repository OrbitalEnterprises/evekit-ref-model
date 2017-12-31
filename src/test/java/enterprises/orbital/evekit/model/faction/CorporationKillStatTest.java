package enterprises.orbital.evekit.model.faction;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;
import org.junit.Assert;
import org.junit.Test;

public class CorporationKillStatTest extends AbstractRefModelTester<CorporationKillStat> {

  private final StatAttribute attribute = StatAttribute.LAST_WEEK;
  private final int corporationID = TestBase.getRandomInt(100000000);
  private final int kills = TestBase.getRandomInt(100000000);

  private final ClassUnderTestConstructor<CorporationKillStat> eol = () -> new CorporationKillStat(attribute, kills, corporationID);
  private final ClassUnderTestConstructor<CorporationKillStat> live = () -> new CorporationKillStat(attribute, kills + 1, corporationID);

  @Test
  public void testBasic() {
    runBasicTests(eol, () -> new CorporationKillStat[]{
        new CorporationKillStat(StatAttribute.TOTAL, kills, corporationID),
        new CorporationKillStat(attribute, kills + 1, corporationID),
        new CorporationKillStat(attribute, kills, corporationID + 1)
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (long time) -> CorporationKillStat.get(time, attribute, corporationID));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different corporation ID
    // - objects with different attribute
    // - objects not live at the given time
    CorporationKillStat existing, keyed;

    keyed = new CorporationKillStat(attribute, kills, corporationID);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Different corporation ID
    existing = new CorporationKillStat(attribute, kills, corporationID + 1);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Different attribute
    existing = new CorporationKillStat(StatAttribute.TOTAL, kills, corporationID);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Not live at the given time
    existing = new CorporationKillStat(attribute, kills + 1, corporationID);
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new CorporationKillStat(attribute, kills + 2, corporationID);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    CorporationKillStat result = CorporationKillStat.get(8889L, attribute, corporationID);
    Assert.assertEquals(keyed, result);
  }

}
