package enterprises.orbital.evekit.model.faction;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;
import org.junit.Assert;
import org.junit.Test;

public class CharacterVictoryPointStatTest extends AbstractRefModelTester<CharacterVictoryPointStat> {

  private final StatAttribute attribute = StatAttribute.LAST_WEEK;
  private final int characterID = TestBase.getRandomInt(100000000);
  private final int victoryPoints = TestBase.getRandomInt(100000000);

  private final ClassUnderTestConstructor<CharacterVictoryPointStat> eol = () -> new CharacterVictoryPointStat(
      attribute, victoryPoints, characterID);
  private final ClassUnderTestConstructor<CharacterVictoryPointStat> live = () -> new CharacterVictoryPointStat(
      attribute, victoryPoints + 1, characterID);

  @Test
  public void testBasic() {
    runBasicTests(eol, () -> new CharacterVictoryPointStat[]{
        new CharacterVictoryPointStat(StatAttribute.TOTAL, victoryPoints, characterID),
        new CharacterVictoryPointStat(attribute, victoryPoints + 1, characterID),
        new CharacterVictoryPointStat(attribute, victoryPoints, characterID + 1),
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (long time) -> CharacterVictoryPointStat.get(time, attribute, characterID));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different character ID
    // - objects with different attribute
    // - objects not live at the given time
    CharacterVictoryPointStat existing, keyed;

    keyed = new CharacterVictoryPointStat(attribute, victoryPoints, characterID);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Different character ID
    existing = new CharacterVictoryPointStat(attribute, victoryPoints, characterID + 1);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Different attribute
    existing = new CharacterVictoryPointStat(StatAttribute.TOTAL, victoryPoints, characterID);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Not live at the given time
    existing = new CharacterVictoryPointStat(attribute, victoryPoints + 1, characterID);
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new CharacterVictoryPointStat(attribute, victoryPoints + 2, characterID);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    CharacterVictoryPointStat result = CharacterVictoryPointStat.get(8889L, attribute, characterID);
    Assert.assertEquals(keyed, result);
  }

}
