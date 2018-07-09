package enterprises.orbital.evekit.model.faction;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;
import org.junit.Assert;
import org.junit.Test;

public class FactionWarSystemTest extends AbstractRefModelTester<FactionWarSystem> {

  private final int occupyingFactionID = TestBase.getRandomInt(100000000);
  private final int owningFactionID = TestBase.getRandomInt(100000000);
  private final int solarSystemID = TestBase.getRandomInt(100000000);
  private final int victoryPoints = TestBase.getRandomInt(100000000);
  private final int victoryPointsThreshold = TestBase.getRandomInt(100000000);
  private final String contested = TestBase.getRandomText(50);

  private final ClassUnderTestConstructor<FactionWarSystem> eol = () -> new FactionWarSystem(
      occupyingFactionID, owningFactionID,
      solarSystemID, victoryPoints, victoryPointsThreshold, contested);
  private final ClassUnderTestConstructor<FactionWarSystem> live = () -> new FactionWarSystem(
      occupyingFactionID + 1, owningFactionID,
      solarSystemID, victoryPoints, victoryPointsThreshold, contested);

  @Test
  public void testBasic() {

    runBasicTests(eol, () -> new FactionWarSystem[]{
        new FactionWarSystem(occupyingFactionID + 1, owningFactionID, solarSystemID, victoryPoints, victoryPointsThreshold, contested),
        new FactionWarSystem(occupyingFactionID, owningFactionID + 1, solarSystemID, victoryPoints, victoryPointsThreshold, contested),
        new FactionWarSystem(occupyingFactionID, owningFactionID, solarSystemID + 1, victoryPoints, victoryPointsThreshold, contested),
        new FactionWarSystem(occupyingFactionID, owningFactionID, solarSystemID, victoryPoints + 1, victoryPointsThreshold, contested),
        new FactionWarSystem(occupyingFactionID, owningFactionID, solarSystemID, victoryPoints, victoryPointsThreshold + 1, contested),
        new FactionWarSystem(occupyingFactionID, owningFactionID, solarSystemID, victoryPoints, victoryPointsThreshold, contested + "1")
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (long time) -> FactionWarSystem.get(time, solarSystemID));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different solar system ID
    // - objects not live at the given time
    FactionWarSystem existing, keyed;

    keyed = new FactionWarSystem(occupyingFactionID, owningFactionID, solarSystemID, victoryPoints, victoryPointsThreshold, contested);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Different solaar system ID
    existing = new FactionWarSystem(
        occupyingFactionID, owningFactionID, solarSystemID + 1, victoryPoints, victoryPointsThreshold, contested);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Not live at the given time
    existing = new FactionWarSystem(
        occupyingFactionID + 1, owningFactionID, solarSystemID, victoryPoints, victoryPointsThreshold, contested);
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new FactionWarSystem(
        occupyingFactionID + 2, owningFactionID, solarSystemID, victoryPoints, victoryPointsThreshold, contested);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    FactionWarSystem result = FactionWarSystem.get(8889L, solarSystemID);
    Assert.assertEquals(keyed, result);
  }

}
