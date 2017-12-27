package enterprises.orbital.evekit.model.eve;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;
import org.junit.Assert;
import org.junit.Test;

public class AllianceIconTest extends AbstractRefModelTester<AllianceIcon> {
  private final long allianceID = TestBase.getRandomInt(100000000);
  private final String px64x64 = TestBase.getRandomText(40);
  private final String px128x128 = TestBase.getRandomText(40);

  final ClassUnderTestConstructor<AllianceIcon> eol = () -> new AllianceIcon(allianceID, px64x64, px128x128);

  final ClassUnderTestConstructor<AllianceIcon> live = () -> new AllianceIcon(allianceID, px64x64, px128x128 + "A");

  @Test
  public void testBasic() {

    runBasicTests(eol, () -> new AllianceIcon[]{
        new AllianceIcon(allianceID + 1, px64x64, px128x128), new AllianceIcon(allianceID, px64x64 + "A", px128x128),
        new AllianceIcon(allianceID, px64x64, px128x128 + "A")
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (long time) ->
        AllianceIcon.get(time, allianceID));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different alliance ID
    // - objects not live at the given time
    AllianceIcon existing, keyed;

    keyed = new AllianceIcon(allianceID, px64x64, px128x128);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Different alliance ID
    existing = new AllianceIcon(allianceID + 1, px64x64, px128x128);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Not live at the given time
    existing = new AllianceIcon(allianceID, px64x64, px128x128 + "A");
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new AllianceIcon(allianceID, px64x64, px128x128 + "B");
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    AllianceIcon result = AllianceIcon.get(8889L, allianceID);
    Assert.assertEquals(keyed, result);
  }

}
