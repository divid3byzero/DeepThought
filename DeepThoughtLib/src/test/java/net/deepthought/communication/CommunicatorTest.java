package net.deepthought.communication;

import net.deepthought.Application;
import net.deepthought.communication.listener.AskForDeviceRegistrationListener;
import net.deepthought.communication.listener.CaptureImageOrDoOcrListener;
import net.deepthought.communication.listener.CaptureImageOrDoOcrResponseListener;
import net.deepthought.communication.listener.MessagesReceiverListener;
import net.deepthought.communication.messages.AskForDeviceRegistrationRequest;
import net.deepthought.communication.messages.AskForDeviceRegistrationResponseMessage;
import net.deepthought.communication.messages.CaptureImageOrDoOcrRequest;
import net.deepthought.communication.messages.CaptureImageResultResponse;
import net.deepthought.communication.messages.OcrResultResponse;
import net.deepthought.communication.messages.StopCaptureImageOrDoOcrRequest;
import net.deepthought.communication.model.ConnectedDevice;
import net.deepthought.communication.model.HostInfo;
import net.deepthought.communication.registration.UserDeviceRegistrationRequestListener;
import net.deepthought.data.contentextractor.ocr.CaptureImageResult;
import net.deepthought.data.contentextractor.ocr.TextRecognitionResult;
import net.deepthought.util.ObjectHolder;
import net.deepthought.util.file.FileUtils;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ganymed on 20/08/15.
 */
public class CommunicatorTest extends CommunicationTestBase {

  private final static Logger log = LoggerFactory.getLogger(CommunicatorTest.class);


  @Override
  public void setup() {
    super.setup();

    try { Thread.sleep(200); } catch(Exception ex) { } // it is very critical that server is fully started therefore wait some time
  }

  @Test
  public void askForDeviceRegistration_ListenerMethodAllowDeviceToRegisterGetsCalled() {
    final AtomicBoolean methodCalled = new AtomicBoolean(false);
    final CountDownLatch waitLatch = new CountDownLatch(1);

    connector.openUserDeviceRegistrationServer(new UserDeviceRegistrationRequestListener() {
      @Override
      public void registerDeviceRequestRetrieved(AskForDeviceRegistrationRequest request) {
        methodCalled.set(true);
        waitLatch.countDown();
      }
    });

    communicator.askForDeviceRegistration(createLocalHostServerInfo(), null);

    try { waitLatch.await(2, TimeUnit.SECONDS); } catch(Exception ex) { }
    connector.closeUserDeviceRegistrationServer();

    Assert.assertTrue(methodCalled.get());
  }

  @Test
  public void askForDeviceRegistration_ServerResponseIsReceived() {
    final List<AskForDeviceRegistrationResponseMessage> responses = new ArrayList<>();
    final CountDownLatch waitLatch = new CountDownLatch(1);

    connector.openUserDeviceRegistrationServer(null);

    communicator.askForDeviceRegistration(createLocalHostServerInfo(), new AskForDeviceRegistrationListener() {
      @Override
      public void serverResponded(AskForDeviceRegistrationResponseMessage response) {
        responses.add(response);
        waitLatch.countDown();
      }
    });

    try { waitLatch.await(3, TimeUnit.SECONDS); } catch(Exception ex) { }
    connector.closeUserDeviceRegistrationServer();

    Assert.assertEquals(1, responses.size());
  }

  @Test
  public void askForDeviceRegistration_RegistrationIsProhibitedByServer_RegistrationDeniedResponseIsReceived() {
    final List<AskForDeviceRegistrationResponseMessage> responses = new ArrayList<>();
    final CountDownLatch waitLatch = new CountDownLatch(1);

    connector.openUserDeviceRegistrationServer(new UserDeviceRegistrationRequestListener() {
      @Override
      public void registerDeviceRequestRetrieved(AskForDeviceRegistrationRequest request) {
        communicator.sendAskForDeviceRegistrationResponse(request, AskForDeviceRegistrationResponseMessage.Deny, null);
      }
    });

    communicator.askForDeviceRegistration(createLocalHostServerInfo(), new net.deepthought.communication.listener.AskForDeviceRegistrationListener() {
      @Override
      public void serverResponded(AskForDeviceRegistrationResponseMessage response) {
        responses.add(response);
        waitLatch.countDown();
      }
    });

    try { waitLatch.await(3, TimeUnit.SECONDS); } catch(Exception ex) { }
    connector.closeUserDeviceRegistrationServer();

    Assert.assertFalse(responses.get(0).allowsRegistration());
  }

  @Test
  public void askForDeviceRegistration_ServerAllowsRegistration_RegistrationAllowedResponseIsReceived() {
    final List<AskForDeviceRegistrationResponseMessage> responses = new ArrayList<>();
    final CountDownLatch waitLatch = new CountDownLatch(1);

    connector.openUserDeviceRegistrationServer(new UserDeviceRegistrationRequestListener() {
      @Override
      public void registerDeviceRequestRetrieved(AskForDeviceRegistrationRequest request) {
        communicator.sendAskForDeviceRegistrationResponse(request, AskForDeviceRegistrationResponseMessage.createAllowRegistrationResponse(true,
            Application.getLoggedOnUser(), Application.getApplication().getLocalDevice()), null);
      }
    });

    communicator.askForDeviceRegistration(createLocalHostServerInfo(), new AskForDeviceRegistrationListener() {
      @Override
      public void serverResponded(AskForDeviceRegistrationResponseMessage response) {
        responses.add(response);
        waitLatch.countDown();
      }
    });

    try { waitLatch.await(3, TimeUnit.SECONDS); } catch(Exception ex) { }
    connector.closeUserDeviceRegistrationServer();

    Assert.assertTrue(responses.get(0).allowsRegistration());
  }

  @Test
  public void askForDeviceRegistrationDone_ListenerGetsRemoved() {
    final CountDownLatch waitLatch = new CountDownLatch(1);

    connector.openUserDeviceRegistrationServer(new UserDeviceRegistrationRequestListener() {
      @Override
      public void registerDeviceRequestRetrieved(AskForDeviceRegistrationRequest request) {
        communicator.sendAskForDeviceRegistrationResponse(request, AskForDeviceRegistrationResponseMessage.createAllowRegistrationResponse(true,
            Application.getLoggedOnUser(), Application.getApplication().getLocalDevice()), null);
      }
    });

    communicator.askForDeviceRegistration(createLocalHostServerInfo(), new AskForDeviceRegistrationListener() {
      @Override
      public void serverResponded(AskForDeviceRegistrationResponseMessage response) {
        waitLatch.countDown();
      }
    });

    Assert.assertEquals(1, communicator.askForDeviceRegistrationListeners.size());

    try { waitLatch.await(3, TimeUnit.SECONDS); } catch(Exception ex) { }
    connector.closeUserDeviceRegistrationServer();

    Assert.assertEquals(0, communicator.askForDeviceRegistrationListeners.size());
  }


  @Test
  public void notifyRemoteWeHaveConnected() {
    Assert.fail("Yet to implement");
  }


  @Test
  public void startCaptureImageAndDoOcr_RequestIsReceived() {
    final AtomicBoolean methodCalled = new AtomicBoolean(false);
    final CountDownLatch waitLatch = new CountDownLatch(1);

    connector.addCaptureImageOrDoOcrListener(new CaptureImageOrDoOcrListener() {
      @Override
      public void startCaptureImageOrDoOcr(CaptureImageOrDoOcrRequest request) {
        methodCalled.set(true);
        waitLatch.countDown();
      }

      @Override
      public void stopCaptureImageOrDoOcr(StopCaptureImageOrDoOcrRequest request) {

      }
    });

    communicator.startCaptureImageAndDoOcr(new ConnectedDevice("unique", NetworkHelper.getIPAddressString(true), connector.getMessageReceiverPort()), null);

    try { waitLatch.await(2, TimeUnit.SECONDS); } catch(Exception ex) { }

    Assert.assertTrue(methodCalled.get());
  }

  @Test
  public void startCaptureImageAndDoOcr_ServerSendsOcrResult_ServerResponseIsReceived() {
    final List<TextRecognitionResult> ocrResults = new ArrayList<>();
    final AtomicBoolean methodCalled = new AtomicBoolean(false);
    final String recognizedText = "Hyper, hyper";
    final CountDownLatch waitLatch = new CountDownLatch(1);

    connector.addCaptureImageOrDoOcrListener(new CaptureImageOrDoOcrListener() {

      @Override
      public void startCaptureImageOrDoOcr(CaptureImageOrDoOcrRequest request) {
        communicator.sendOcrResult(request, TextRecognitionResult.createRecognitionSuccessfulResult(recognizedText), null);
      }

      @Override
      public void stopCaptureImageOrDoOcr(StopCaptureImageOrDoOcrRequest request) {

      }
    });

    communicator.startCaptureImageAndDoOcr(new ConnectedDevice("unique", NetworkHelper.getIPAddressString(true), connector.getMessageReceiverPort()), new CaptureImageOrDoOcrResponseListener() {
      @Override
      public void captureImageResult(CaptureImageResult captureImageResult) {

      }

      @Override
      public void ocrResult(TextRecognitionResult ocrResult) {
        methodCalled.set(true);
        ocrResults.add(ocrResult);
        waitLatch.countDown();
      }
    });

    try { waitLatch.await(5, TimeUnit.SECONDS); } catch(Exception ex) { }

    Assert.assertTrue(methodCalled.get());
    Assert.assertEquals(recognizedText, ocrResults.get(0).getRecognizedText());
  }


  @Test
  public void startDoOcr_RequestIsReceived() throws IOException {
    final AtomicBoolean methodCalled = new AtomicBoolean(false);
    final CountDownLatch waitLatch = new CountDownLatch(1);
    final ObjectHolder<byte[]> receivedImageData = new ObjectHolder<>();

    connector.addCaptureImageOrDoOcrListener(new CaptureImageOrDoOcrListener() {
      @Override
      public void startCaptureImageOrDoOcr(CaptureImageOrDoOcrRequest request) {
        methodCalled.set(true);
        try {
          receivedImageData.set(request.readBytesFromImageUri());
        } catch (Exception ex) { log. error("Could not get image data from CaptureImageOrDoOcrRequest", ex); }
        waitLatch.countDown();
      }

      @Override
      public void stopCaptureImageOrDoOcr(StopCaptureImageOrDoOcrRequest request) {

      }
    });

    byte[] imageData = getTestImage();

    communicator.startDoOcr(new ConnectedDevice("unique", NetworkHelper.getIPAddressString(true), connector.getMessageReceiverPort()), imageData, false, false, null);

    try { waitLatch.await(2, TimeUnit.SECONDS); } catch(Exception ex) { }

    Assert.assertTrue(methodCalled.get());
    Assert.assertArrayEquals(imageData, receivedImageData.get());
  }

  @Test
  public void startDoOcr_ServerSendsOcrResult_ServerResponseIsReceived() throws IOException {
    final List<TextRecognitionResult> ocrResults = new ArrayList<>();
    final AtomicBoolean methodCalled = new AtomicBoolean(false);
    final String recognizedText = "Hyper, hyper";
    final CountDownLatch waitLatch = new CountDownLatch(1);

    connector.addCaptureImageOrDoOcrListener(new CaptureImageOrDoOcrListener() {

      @Override
      public void startCaptureImageOrDoOcr(CaptureImageOrDoOcrRequest request) {
        communicator.sendOcrResult(request, TextRecognitionResult.createRecognitionSuccessfulResult(recognizedText), null);
      }

      @Override
      public void stopCaptureImageOrDoOcr(StopCaptureImageOrDoOcrRequest request) {

      }
    });

    byte[] imageData = getTestImage();

    communicator.startDoOcr(new ConnectedDevice("unique", NetworkHelper.getIPAddressString(true), connector.getMessageReceiverPort()),
        imageData, false, false, new CaptureImageOrDoOcrResponseListener() {
          @Override
          public void captureImageResult(CaptureImageResult captureImageResult) {

          }

          @Override
          public void ocrResult(TextRecognitionResult ocrResult) {
            methodCalled.set(true);
            ocrResults.add(ocrResult);
            waitLatch.countDown();
          }
        });

    try { waitLatch.await(3, TimeUnit.SECONDS); } catch(Exception ex) { }

    Assert.assertTrue(methodCalled.get());
    Assert.assertEquals(recognizedText, ocrResults.get(0).getRecognizedText());
  }


  @Test
  public void sendCaptureImageResult_ResultIsReceived() throws IOException {
    final AtomicBoolean methodCalled = new AtomicBoolean(false);
    final CountDownLatch waitLatch = new CountDownLatch(1);

    ConnectedDevice self = ConnectedDevice.createSelfInstance();
    CaptureImageOrDoOcrRequest request = new CaptureImageOrDoOcrRequest(self.getAddress(), self.getMessagesPort(), true, false);
    connector.getCommunicator().captureImageOrDoOcrListeners.put(request, new CaptureImageOrDoOcrResponseListener() {
      @Override
      public void captureImageResult(CaptureImageResult captureImageResult) {
        methodCalled.set(true);
        waitLatch.countDown();
      }

      @Override
      public void ocrResult(TextRecognitionResult ocrResult) {

      }
    });

    byte[] imageData = getTestImage();

    communicator.sendCaptureImageResult(request, imageData, null);

    try { waitLatch.await(2, TimeUnit.SECONDS); } catch(Exception ex) { }

    Assert.assertTrue(methodCalled.get());
  }


  @Test
  public void sendCaptureImageResult_ReceivedImageDataIsCorrect() throws IOException {
    final List<byte[]> receivedImageData = new ArrayList<>();
    final CountDownLatch waitLatch = new CountDownLatch(1);

    ConnectedDevice self = ConnectedDevice.createSelfInstance();
    CaptureImageOrDoOcrRequest request = new CaptureImageOrDoOcrRequest(self.getAddress(), self.getMessagesPort(), true, false);
    connector.getCommunicator().captureImageOrDoOcrListeners.put(request, new CaptureImageOrDoOcrResponseListener() {
      @Override
      public void captureImageResult(CaptureImageResult captureImageResult) {
        try {
          if(captureImageResult.getImageData() != null)
            receivedImageData.add(captureImageResult.getImageData());
          else if(captureImageResult.getImageUri() != null)
            receivedImageData.add(FileUtils.readFile(new File(captureImageResult.getImageUri())));
        } catch(Exception ex) { log. error("Could not get image data from CaptureImageResult", ex); }
        waitLatch.countDown();
      }

      @Override
      public void ocrResult(TextRecognitionResult ocrResult) {

      }
    });

    byte[] sentImageData = getTestImage();

    communicator.sendCaptureImageResult(request, sentImageData, null);

    try { waitLatch.await(2, TimeUnit.SECONDS);
    } catch (Exception ex) { }

    Assert.assertEquals(1, receivedImageData.size());
    Assert.assertEquals(sentImageData.length, receivedImageData.get(0).length);
    Assert.assertArrayEquals(sentImageData, receivedImageData.get(0));
  }


  @Test
  public void stopCaptureImageAndDoOcr_RequestIsReceived() {
    final AtomicBoolean methodCalled = new AtomicBoolean(false);
    final CountDownLatch waitLatch = new CountDownLatch(1);

    connector.addMessagesReceiverListener(new MessagesReceiverListener() {
      @Override
      public void askForDeviceRegistrationResponseReceived(AskForDeviceRegistrationResponseMessage message) {

      }

      @Override
      public void notifyRegisteredDeviceConnected(ConnectedDevice connectedDevice) {

      }

      @Override
      public void deviceIsStillConnected(ConnectedDevice connectedDevice) {

      }

      @Override
      public void captureImageResult(CaptureImageResultResponse response) {

      }

      @Override
      public void ocrResult(OcrResultResponse response) {

      }

      @Override
      public void startCaptureImageOrDoOcr(CaptureImageOrDoOcrRequest request) {

      }

      @Override
      public void stopCaptureImageOrDoOcr(StopCaptureImageOrDoOcrRequest request) {
        methodCalled.set(true);
        waitLatch.countDown();
      }

      @Override
      public void registerDeviceRequestRetrieved(AskForDeviceRegistrationRequest request) {

      }
    });

    communicator.startCaptureImageAndDoOcr(new ConnectedDevice("unique", NetworkHelper.getIPAddressString(true), connector.getMessageReceiverPort()), ocrResponseListener);
    communicator.stopCaptureImageAndDoOcr(ocrResponseListener, null);

    try { waitLatch.await(3, TimeUnit.SECONDS);
    } catch(Exception ex) { }

    Assert.assertTrue(methodCalled.get());
  }

  @Test
  public void stopCaptureImageAndDoOcr_ListenerGetsCalledCorrectly() {
    final AtomicBoolean methodCalled = new AtomicBoolean(false);
    final CountDownLatch waitLatch = new CountDownLatch(1);

    connector.addCaptureImageOrDoOcrListener(new CaptureImageOrDoOcrListener() {
      @Override
      public void startCaptureImageOrDoOcr(CaptureImageOrDoOcrRequest request) {
        communicator.stopCaptureImageAndDoOcr(ocrResponseListener, null);
      }

      @Override
      public void stopCaptureImageOrDoOcr(StopCaptureImageOrDoOcrRequest request) {
        methodCalled.set(true);
        waitLatch.countDown();
      }
    });

    communicator.startCaptureImageAndDoOcr(new ConnectedDevice("unique", NetworkHelper.getIPAddressString(true), connector.getMessageReceiverPort()), ocrResponseListener);

    try { waitLatch.await(3, TimeUnit.SECONDS);
    } catch(Exception ex) { }

    Assert.assertTrue(methodCalled.get());
  }

  @Test
  public void stopCaptureImageAndDoOcrDone_ListenerGetsRemoved() {
    final CountDownLatch waitLatch = new CountDownLatch(1);

    connector.addCaptureImageOrDoOcrListener(new CaptureImageOrDoOcrListener() {
      @Override
      public void startCaptureImageOrDoOcr(CaptureImageOrDoOcrRequest request) {
        communicator.stopCaptureImageAndDoOcr(ocrResponseListener, null);
      }

      @Override
      public void stopCaptureImageOrDoOcr(StopCaptureImageOrDoOcrRequest request) {
        waitLatch.countDown();
      }
    });

    communicator.startCaptureImageAndDoOcr(new ConnectedDevice("unique", NetworkHelper.getIPAddressString(true), connector.getMessageReceiverPort()), ocrResponseListener);
    Assert.assertEquals(1, communicator.captureImageOrDoOcrListeners.size());

    try { waitLatch.await(3, TimeUnit.SECONDS);
    } catch (Exception ex) {
    }

    Assert.assertEquals(0, communicator.captureImageOrDoOcrListeners.size());
  }

  protected CaptureImageOrDoOcrResponseListener ocrResponseListener = new CaptureImageOrDoOcrResponseListener() {
    @Override
    public void captureImageResult(CaptureImageResult captureImageResult) {

    }

    @Override
    public void ocrResult(TextRecognitionResult ocrResult) {

    }
  };


  @Test
  public void sendOcrResult() {
    Assert.fail("Yet to implement");
  }

  @Test
  public void sendCapturedImage() {
    Assert.fail("Yet to implement");
  }


  protected HostInfo createLocalHostServerInfo() {
    HostInfo hostInfo = HostInfo.fromUserAndDevice(Application.getLoggedOnUser(), Application.getApplication().getLocalDevice());
    hostInfo.setIpAddress(NetworkHelper.getIPAddressString(true));
    hostInfo.setPort(Application.getDeepThoughtsConnector().getMessageReceiverPort());

    return hostInfo;
  }


  protected byte[] getTestImage() throws IOException {
//    return FileHelper.loadTestImage();
    return new byte[] { 47, 11 };
  }


  protected void sendMessagesToCommunicator(int port, String... messages) throws IOException, InterruptedException {
    Runtime rt = Runtime.getRuntime();
    Process pr = rt.exec("telnet localhost " + port);
    Thread.sleep(50); // wait till connection has been established

    PrintStream streamWriter = new PrintStream(pr.getOutputStream());
    for(String message : messages) {
      streamWriter.print(message);
      streamWriter.flush();
    }

    streamWriter.close();
    pr.destroy();
  }
}
