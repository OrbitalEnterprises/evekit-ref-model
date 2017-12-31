package enterprises.orbital.evekit.model.faction;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.RefCachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CharacterKillStatTest extends AbstractRefModelTester<CharacterKillStat> {

  private final StatAttribute attribute = StatAttribute.LAST_WEEK;
  private final int characterID = TestBase.getRandomInt(100000000);
  private final int kills = TestBase.getRandomInt(100000000);

  private final ClassUnderTestConstructor<CharacterKillStat> eol = () -> new CharacterKillStat(attribute, kills, characterID);
  private final ClassUnderTestConstructor<CharacterKillStat> live = () -> new CharacterKillStat(attribute, kills + 1, characterID);

  @Test
  public void testBasic() {

    runBasicTests(eol, () -> new CharacterKillStat[]{
        new CharacterKillStat(StatAttribute.TOTAL, kills, characterID),
        new CharacterKillStat(attribute, kills + 1, characterID),
        new CharacterKillStat(attribute, kills, characterID + 1)
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (long time) -> CharacterKillStat.get(time, attribute, characterID));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different character ID
    // - objects with a different attribute
    // - objects not live at the given time
    CharacterKillStat existing, keyed;

    keyed = new CharacterKillStat(attribute, kills, characterID);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Different character ID
    existing = new CharacterKillStat(attribute, kills, characterID + 1);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Different attribute
    existing = new CharacterKillStat(StatAttribute.TOTAL, kills, characterID);
    existing.setup(8888L);
    RefCachedData.update(existing);

    // Not live at the given time
    existing = new CharacterKillStat(attribute, kills + 1, characterID);
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new CharacterKillStat(attribute, kills + 2, characterID);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    CharacterKillStat result = CharacterKillStat.get(8889L, attribute, characterID);
    Assert.assertEquals(keyed, result);
  }

  @Test
  public void testEnumSelector() throws Exception {
    // Convenient selector
    // Setup three different selectors
    long stamp = 5555L;
    CharacterKillStat lw, t, y;
    lw = new CharacterKillStat(StatAttribute.LAST_WEEK, kills, characterID);
    lw.setup(stamp);
    lw = RefCachedData.update(lw);
    t = new CharacterKillStat(StatAttribute.TOTAL, kills, characterID);
    t.setup(stamp);
    t = RefCachedData.update(t);
    y = new CharacterKillStat(StatAttribute.YESTERDAY, kills, characterID);
    y.setup(stamp);
    y = RefCachedData.update(y);
    // Verify attribute selector works properly
    final AttributeSelector ANY_SELECTOR = new AttributeSelector("{ any: true }");
    final AttributeSelector atSel = new AttributeSelector("{values: [" + stamp + "]}");
    // Single select
    List<CharacterKillStat> sel = CharacterKillStat.accessQuery(-1, 1000, false, atSel, new AttributeSelector("{values: ['LAST_WEEK']}"), ANY_SELECTOR,
                                                                ANY_SELECTOR);
    Assert.assertEquals(1, sel.size());
    Assert.assertEquals(lw, sel.get(0));
    // Multi select - note that results should be returned in order of created
    sel = CharacterKillStat.accessQuery(-1, 1000, false, atSel, new AttributeSelector("{values: ['LAST_WEEK', 'YESTERDAY']}"), ANY_SELECTOR,
                                        ANY_SELECTOR);
    Assert.assertEquals(2, sel.size());
    Assert.assertEquals(lw, sel.get(0));
    Assert.assertEquals(y, sel.get(1));
    // Check last pair
    sel = CharacterKillStat.accessQuery(-1, 1000, false, atSel, new AttributeSelector("{values: ['YESTERDAY', 'TOTAL']}"), ANY_SELECTOR,
                                        ANY_SELECTOR);
    Assert.assertEquals(2, sel.size());
    Assert.assertEquals(t, sel.get(0));
    Assert.assertEquals(y, sel.get(1));
  }

}
