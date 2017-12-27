package enterprises.orbital.evekit.model.eve;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;
import org.junit.Assert;
import org.junit.Test;

public class AllianceTest extends AbstractRefModelTester<Alliance> {

  private final long allianceID = TestBase.getRandomInt(100000000);
  private final long executorCorpID = TestBase.getRandomInt(100000000);
  private final int memberCount = TestBase.getRandomInt(100000000);
  private final String name = TestBase.getRandomText(50);
  private final String shortName = TestBase.getRandomText(50);
  private final long startDate = TestBase.getRandomInt(100000000);
  private final long creatorID = TestBase.getRandomInt(100000000);
  private final long creatorCorpID = TestBase.getRandomInt(100000000);
  private final int factionID = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Alliance> eol = () -> new Alliance(allianceID, executorCorpID, memberCount, name, shortName, startDate, creatorID, creatorCorpID, factionID);
  final ClassUnderTestConstructor<Alliance> live = () -> new Alliance(allianceID, executorCorpID, memberCount + 1, name, shortName, startDate, creatorID, creatorCorpID, factionID);

  @Test
  public void testBasic() {

    runBasicTests(eol, () -> new Alliance[]{
        new Alliance(allianceID + 1, executorCorpID, memberCount, name, shortName, startDate, creatorID, creatorCorpID, factionID),
        new Alliance(allianceID, executorCorpID + 1, memberCount, name, shortName, startDate, creatorID, creatorCorpID, factionID),
        new Alliance(allianceID, executorCorpID, memberCount + 1, name, shortName, startDate, creatorID, creatorCorpID, factionID),
        new Alliance(allianceID, executorCorpID, memberCount, name + "1", shortName, startDate, creatorID, creatorCorpID, factionID),
        new Alliance(allianceID, executorCorpID, memberCount, name, shortName + "1", startDate, creatorID, creatorCorpID, factionID),
        new Alliance(allianceID, executorCorpID, memberCount, name, shortName, startDate + 1, creatorID, creatorCorpID, factionID),
        new Alliance(allianceID, executorCorpID, memberCount, name, shortName, startDate, creatorID + 1, creatorCorpID, factionID),
        new Alliance(allianceID, executorCorpID, memberCount, name, shortName, startDate, creatorID, creatorCorpID + 1, factionID),
        new Alliance(allianceID, executorCorpID, memberCount, name, shortName, startDate, creatorID, creatorCorpID, factionID + 1)
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (long time) -> Alliance.get(time, allianceID));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different alliance ID
    // - objects not live at the given time
    Alliance existing, keyed;

    keyed = new Alliance(allianceID, executorCorpID, memberCount, name, shortName, startDate, creatorID, creatorCorpID, factionID);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Different alliance ID
    existing = new Alliance(allianceID + 1, executorCorpID, memberCount, name, shortName, startDate, creatorID, creatorCorpID, factionID);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Not live at the given time
    existing = new Alliance(allianceID, executorCorpID, memberCount + 1, name, shortName, startDate, creatorID, creatorCorpID, factionID);
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new Alliance(allianceID, executorCorpID, memberCount + 2, name, shortName, startDate, creatorID, creatorCorpID, factionID);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    Alliance result = Alliance.get(8889L, allianceID);
    Assert.assertEquals(keyed, result);
  }

}
