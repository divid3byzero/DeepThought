package net.deepthought.communication.registration;

import net.deepthought.communication.CommunicationTestBase;
import net.deepthought.communication.NetworkHelper;
import net.deepthought.communication.model.HostInfo;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by ganymed on 19/08/15.
 */
public class LookingForRegistrationServersClientTest extends CommunicationTestBase {


  @Test
  public void noRegistrationServersOpen_ClientReceivesNoResponse() {
    final List<HostInfo> serverResponse = new ArrayList<>();

    LookingForRegistrationServersClient client = new LookingForRegistrationServersClient(messagesCreator);
    client.findRegistrationServersAsync(new RegistrationRequestListener() {
      @Override
      public void openRegistrationServerFound(HostInfo serverInfo) {
        serverResponse.add(serverInfo);
      }
    });

    CountDownLatch latch = new CountDownLatch(1);
    try { latch.await(1, TimeUnit.SECONDS); } catch(Exception ex) { }
    Assert.assertEquals(0, serverResponse.size());

    client.stopSearchingForRegistrationServers();
  }

  @Test
  public void registrationServerIsOpen_RequestGetsReplied() {
    final List<HostInfo> serverInfos = new ArrayList<>();
    final CountDownLatch waitForResponseLatch = new CountDownLatch(1);

    RegistrationServer registrationServer = new RegistrationServer(messagesCreator);
    registrationServer.startRegistrationServerAsync();

    LookingForRegistrationServersClient client = new LookingForRegistrationServersClient(messagesCreator);
    client.findRegistrationServersAsync(new RegistrationRequestListener() {
      @Override
      public void openRegistrationServerFound(HostInfo serverInfo) {
        serverInfos.add(serverInfo);
        waitForResponseLatch.countDown();
      }
    });

    try { waitForResponseLatch.await(1, TimeUnit.SECONDS); } catch(Exception ex) { }

    Assert.assertEquals(1, serverInfos.size());

    registrationServer.closeRegistrationServer();
    client.stopSearchingForRegistrationServers();
  }

  @Test
  public void requestGetsReplied_HostInfoIsValid() {
    final List<HostInfo> serverInfos = new ArrayList<>();
    final CountDownLatch waitForResponseLatch = new CountDownLatch(1);

    RegistrationServer registrationServer = new RegistrationServer(messagesCreator);
    registrationServer.startRegistrationServerAsync();

    LookingForRegistrationServersClient client = new LookingForRegistrationServersClient(messagesCreator);
    client.findRegistrationServersAsync(new RegistrationRequestListener() {
      @Override
      public void openRegistrationServerFound(HostInfo serverInfo) {
        serverInfos.add(serverInfo);
        waitForResponseLatch.countDown();
      }
    });

    try { waitForResponseLatch.await(1, TimeUnit.SECONDS); } catch(Exception ex) { }

    HostInfo serverInfo = serverInfos.get(0);
    Assert.assertNotNull(serverInfo);
    Assert.assertEquals(NetworkHelper.getIPAddressString(true), serverInfo.getIpAddress());

    registrationServer.closeRegistrationServer();
    client.stopSearchingForRegistrationServers();
  }
}
