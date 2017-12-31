package enterprises.orbital.evekit.model.faction;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;
import org.junit.Assert;
import org.junit.Test;

public class FactionWarTest extends AbstractRefModelTester<FactionWar> {

  private final int againstID = TestBase.getRandomInt(100000000);
  private final int factionID = TestBase.getRandomInt(100000000);
  private final ClassUnderTestConstructor<FactionWar> eol = () -> new FactionWar(againstID, factionID);
  private final ClassUnderTestConstructor<FactionWar> live = () -> new FactionWar(againstID, factionID);

  @Test
  public void testBasic() {
    runBasicTests(eol, () -> new FactionWar[]{
        new FactionWar(againstID + 1, factionID),
        new FactionWar(againstID, factionID + 1)
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (long time) -> FactionWar.get(time, againstID, factionID));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different against ID
    // - objects with different faction ID
    // - objects not live at the given time
    FactionWar existing, keyed;

    keyed = new FactionWar(againstID, factionID);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Different against ID
    existing = new FactionWar(againstID + 1, factionID);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Different faction ID
    existing = new FactionWar(againstID, factionID + 1);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Not live at the given time
    existing = new FactionWar(againstID, factionID);
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new FactionWar(againstID, factionID);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    FactionWar result = FactionWar.get(8889L, againstID, factionID);
    Assert.assertEquals(keyed, result);
  }

}
