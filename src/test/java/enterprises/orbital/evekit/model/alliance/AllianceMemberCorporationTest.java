package enterprises.orbital.evekit.model.alliance;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;
import enterprises.orbital.evekit.model.alliance.AllianceMemberCorporation;
import org.junit.Assert;
import org.junit.Test;

public class AllianceMemberCorporationTest extends AbstractRefModelTester<AllianceMemberCorporation> {
  private final long allianceID = TestBase.getRandomInt(100000000);
  private final long corporationID = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<AllianceMemberCorporation> eol = () -> new AllianceMemberCorporation(allianceID, corporationID);
  final ClassUnderTestConstructor<AllianceMemberCorporation> live = () -> new AllianceMemberCorporation(allianceID, corporationID);

  @Test
  public void testBasic() {
    runBasicTests(eol, () -> new AllianceMemberCorporation[]{
        new AllianceMemberCorporation(allianceID + 1, corporationID), new AllianceMemberCorporation(allianceID, corporationID + 1)
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (long time) -> AllianceMemberCorporation.get(time, allianceID, corporationID));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different alliance ID
    // - objects with different corporation ID
    // - objects not live at the given time
    AllianceMemberCorporation existing, keyed;

    keyed = new AllianceMemberCorporation(allianceID, corporationID);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Different alliance ID
    existing = new AllianceMemberCorporation(allianceID + 1, corporationID);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Different corporationID
    existing = new AllianceMemberCorporation(allianceID, corporationID + 1);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Not live at the given time
    existing = new AllianceMemberCorporation(allianceID, corporationID);
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new AllianceMemberCorporation(allianceID, corporationID);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    AllianceMemberCorporation result = AllianceMemberCorporation.get(8889L, allianceID, corporationID);
    Assert.assertEquals(keyed, result);
  }

}
