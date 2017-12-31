package enterprises.orbital.evekit.model.sov;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;
import org.junit.Assert;
import org.junit.Test;

public class SovereigntyStructureTest extends AbstractRefModelTester<SovereigntyStructure> {

  private final int allianceID = TestBase.getRandomInt(100000000);
  private final int systemID = TestBase.getRandomInt(100000000);
  private final long structureID = TestBase.getRandomInt(100000000);
  private final int structureTypeID = TestBase.getRandomInt(100000000);
  private final float vulnerabilityOccupancyLevel = (float) TestBase.getRandomDouble(100);
  private final long vulnerableStartTime = TestBase.getRandomLong();
  private final long vulnerableEndTime = TestBase.getRandomLong();
  private final ClassUnderTestConstructor<SovereigntyStructure> eol = () -> new SovereigntyStructure(allianceID, systemID, structureID, structureTypeID, vulnerabilityOccupancyLevel, vulnerableStartTime, vulnerableEndTime);
  private final ClassUnderTestConstructor<SovereigntyStructure> live = () -> new SovereigntyStructure(allianceID + 1, systemID, structureID, structureTypeID, vulnerabilityOccupancyLevel, vulnerableStartTime, vulnerableEndTime);

  @Test
  public void testBasic() {
    runBasicTests(eol, () -> new SovereigntyStructure[]{
        new SovereigntyStructure(allianceID + 1, systemID, structureID, structureTypeID, vulnerabilityOccupancyLevel, vulnerableStartTime, vulnerableEndTime),
        new SovereigntyStructure(allianceID, systemID + 1, structureID, structureTypeID, vulnerabilityOccupancyLevel, vulnerableStartTime, vulnerableEndTime),
        new SovereigntyStructure(allianceID, systemID, structureID + 1, structureTypeID, vulnerabilityOccupancyLevel, vulnerableStartTime, vulnerableEndTime),
        new SovereigntyStructure(allianceID, systemID, structureID, structureTypeID + 1, vulnerabilityOccupancyLevel, vulnerableStartTime, vulnerableEndTime),
        new SovereigntyStructure(allianceID, systemID, structureID, structureTypeID, vulnerabilityOccupancyLevel + 1.0F, vulnerableStartTime, vulnerableEndTime),
        new SovereigntyStructure(allianceID, systemID, structureID, structureTypeID, vulnerabilityOccupancyLevel, vulnerableStartTime + 1L, vulnerableEndTime),
        new SovereigntyStructure(allianceID, systemID, structureID, structureTypeID, vulnerabilityOccupancyLevel, vulnerableStartTime, vulnerableEndTime + 1L)
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (long time) -> SovereigntyStructure.get(time, structureID));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different structure ID
    // - objects not live at the given time
    SovereigntyStructure existing, keyed;

    keyed = new SovereigntyStructure(allianceID, systemID, structureID, structureTypeID, vulnerabilityOccupancyLevel, vulnerableStartTime, vulnerableEndTime);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Different structure ID
    existing = new SovereigntyStructure(allianceID, systemID, structureID + 1, structureTypeID, vulnerabilityOccupancyLevel, vulnerableStartTime, vulnerableEndTime);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Not live at the given time
    existing = new SovereigntyStructure(allianceID + 1, systemID, structureID, structureTypeID, vulnerabilityOccupancyLevel, vulnerableStartTime, vulnerableEndTime);
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new SovereigntyStructure(allianceID + 2, systemID, structureID, structureTypeID, vulnerabilityOccupancyLevel, vulnerableStartTime, vulnerableEndTime);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    SovereigntyStructure result = SovereigntyStructure.get(8889L, structureID);
    Assert.assertEquals(keyed, result);
  }

}
