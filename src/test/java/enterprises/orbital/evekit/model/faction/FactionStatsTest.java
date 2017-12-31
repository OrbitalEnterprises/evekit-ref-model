package enterprises.orbital.evekit.model.faction;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;
import org.junit.Assert;
import org.junit.Test;

public class FactionStatsTest extends AbstractRefModelTester<FactionStats> {

  private final int factionID = TestBase.getRandomInt(100000000);
  private final int killsLastWeek = TestBase.getRandomInt(100000000);
  private final int killsTotal = TestBase.getRandomInt(100000000);
  private final int killsYesterday = TestBase.getRandomInt(100000000);
  private final int pilots = TestBase.getRandomInt(100000000);
  private final int systemsControlled = TestBase.getRandomInt(100000000);
  private final int victoryPointsLastWeek = TestBase.getRandomInt(100000000);
  private final int victoryPointsTotal = TestBase.getRandomInt(100000000);
  private final int victoryPointsYesterday = TestBase.getRandomInt(100000000);

  private final ClassUnderTestConstructor<FactionStats> eol = () -> new FactionStats(
      factionID, killsLastWeek, killsTotal, killsYesterday, pilots,
      systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
      victoryPointsYesterday);
  private final ClassUnderTestConstructor<FactionStats> live = () -> new FactionStats(
      factionID, killsLastWeek + 1, killsTotal, killsYesterday, pilots,
      systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
      victoryPointsYesterday);

  @Test
  public void testBasic() {

    runBasicTests(eol, () -> new FactionStats[]{
        new FactionStats(
            factionID + 1, killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
            victoryPointsYesterday),
        new FactionStats(
            factionID, killsLastWeek + 1, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
            victoryPointsYesterday),
        new FactionStats(
            factionID, killsLastWeek, killsTotal + 1, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
            victoryPointsYesterday),
        new FactionStats(
            factionID, killsLastWeek, killsTotal, killsYesterday + 1, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
            victoryPointsYesterday),
        new FactionStats(
            factionID, killsLastWeek, killsTotal, killsYesterday, pilots + 1, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
            victoryPointsYesterday),
        new FactionStats(
            factionID, killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled + 1, victoryPointsLastWeek, victoryPointsTotal,
            victoryPointsYesterday),
        new FactionStats(
            factionID, killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek + 1, victoryPointsTotal,
            victoryPointsYesterday),
        new FactionStats(
            factionID, killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal + 1,
            victoryPointsYesterday),
        new FactionStats(
            factionID, killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
            victoryPointsYesterday + 1)
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (long time) -> FactionStats.get(time, factionID));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different faction ID
    // - objects not live at the given time
    FactionStats existing, keyed;

    keyed = new FactionStats(
        factionID, killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
        victoryPointsYesterday);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Different faction ID
    existing = new FactionStats(
        factionID + 1, killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
        victoryPointsYesterday);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Not live at the given time
    existing = new FactionStats(
        factionID, killsLastWeek + 1, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
        victoryPointsYesterday);
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new FactionStats(
        factionID, killsLastWeek + 2, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
        victoryPointsYesterday);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    FactionStats result = FactionStats.get(8889L, factionID);
    Assert.assertEquals(keyed, result);
  }

}
