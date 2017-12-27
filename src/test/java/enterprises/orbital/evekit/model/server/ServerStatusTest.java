package enterprises.orbital.evekit.model.server;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;
import org.junit.Assert;
import org.junit.Test;

public class ServerStatusTest extends AbstractRefModelTester<ServerStatus> {

  private final int onlinePlayers = TestBase.getRandomInt(100000000);
  private final long startTime = TestBase.getRandomInt(100000000);
  private final String serverVersion = TestBase.getRandomText(40);
  private final boolean vip = TestBase.getRandomBoolean();


  private final ClassUnderTestConstructor<ServerStatus> eol = () ->
      new ServerStatus(onlinePlayers, startTime, serverVersion, vip);

  private final ClassUnderTestConstructor<ServerStatus> live = () ->
      new ServerStatus(onlinePlayers + 1, startTime, serverVersion, vip);

  @Test
  public void testBasic() {
    runBasicTests(eol, () -> new ServerStatus[]{
        new ServerStatus(onlinePlayers + 1, startTime, serverVersion, vip),
        new ServerStatus(onlinePlayers, startTime + 1, serverVersion, vip), new ServerStatus(onlinePlayers, startTime, serverVersion + "A", vip),
        new ServerStatus(onlinePlayers, startTime, serverVersion, !vip)
    });
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, ServerStatus::get);
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - statuses not live at the given time
    ServerStatus existing, keyed;

    keyed = new ServerStatus(onlinePlayers, startTime, serverVersion, vip);
    keyed.setup(8888L);
    keyed = RefCachedData.update(keyed);

    // Not live at the given time
    existing = new ServerStatus(onlinePlayers + 3, startTime, serverVersion, vip);
    existing.setup(9999L);
    RefCachedData.update(existing);

    // EOL before the given time
    existing = new ServerStatus(onlinePlayers + 4, startTime, serverVersion, vip);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.update(existing);

    ServerStatus result = ServerStatus.get(8889L);
    Assert.assertEquals(keyed, result);
  }

}
