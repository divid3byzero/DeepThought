package net.deepthought.communication;

import net.deepthought.communication.listener.AskForDeviceRegistrationResultListener;
import net.deepthought.communication.listener.CaptureImageAndDoOcrResultListener;
import net.deepthought.communication.listener.CaptureImageResultListener;
import net.deepthought.communication.listener.CommunicatorListener;
import net.deepthought.communication.listener.DoOcrOnImageResultListener;
import net.deepthought.communication.listener.ResponseListener;
import net.deepthought.communication.messages.AsynchronousResponseListenerManager;
import net.deepthought.communication.messages.IMessagesDispatcher;
import net.deepthought.communication.messages.request.AskForDeviceRegistrationRequest;
import net.deepthought.communication.messages.request.DoOcrOnImageRequest;
import net.deepthought.communication.messages.request.GenericRequest;
import net.deepthought.communication.messages.request.Request;
import net.deepthought.communication.messages.request.RequestWithAsynchronousResponse;
import net.deepthought.communication.messages.response.AskForDeviceRegistrationResponse;
import net.deepthought.communication.messages.response.CaptureImageResultResponse;
import net.deepthought.communication.messages.response.OcrResultResponse;
import net.deepthought.communication.messages.response.Response;
import net.deepthought.communication.messages.response.ResponseCode;
import net.deepthought.communication.model.ConnectedDevice;
import net.deepthought.communication.model.DoOcrConfiguration;
import net.deepthought.communication.model.HostInfo;
import net.deepthought.data.contentextractor.ocr.CaptureImageResult;
import net.deepthought.data.contentextractor.ocr.TextRecognitionResult;
import net.deepthought.data.model.Device;
import net.deepthought.data.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ganymed on 20/08/15.
 */
public class Communicator {

  private final static Logger log = LoggerFactory.getLogger(Communicator.class);


  protected IMessagesDispatcher dispatcher = null;

  protected AsynchronousResponseListenerManager listenerManager = null;

  protected int messageReceiverPort;

  protected CommunicatorListener communicatorListener;

  protected ConnectorMessagesCreator connectorMessagesCreator;


  public Communicator(CommunicatorConfig config, CommunicatorListener communicatorListener) {
    this.dispatcher = config.getDispatcher();
    this.listenerManager = config.getListenerManager();
    this.messageReceiverPort = config.getMessageReceiverPort();
    this.connectorMessagesCreator = config.getConnectorMessagesCreator();

    // TODO: try to remove
    this.communicatorListener = communicatorListener;
  }


  public AskForDeviceRegistrationRequest askForDeviceRegistration(HostInfo serverInfo, User loggedOnUser, Device localDevice, final AskForDeviceRegistrationResultListener listener) {
    String address = Addresses.getAskForDeviceRegistrationAddress(serverInfo.getIpAddress(), serverInfo.getPort());

    final AskForDeviceRegistrationRequest request = createAskForDeviceRegistrationRequest(loggedOnUser, localDevice);
    listenerManager.addListenerForResponse(request, listener);

    dispatcher.sendMessageAsync(address, request, new CommunicatorResponseListener() {
      @Override
      public void responseReceived(Response communicatorResponse) {
        // user choice isn't send directly as response anymore as a) asking user is an asynchronous operation which cannot be await synchronously and b) connection may timeout when waiting
        // TODO: check if Response contains an error and notify user accordingly
        dispatchResponse(request, communicatorResponse);
      }
    });

    return request;
  }

  protected AskForDeviceRegistrationRequest createAskForDeviceRegistrationRequest(User loggedOnUser, Device localDevice) {
    return new AskForDeviceRegistrationRequest(loggedOnUser, localDevice, getIpAddressToSendResponseTo(), getMessageReceiverPort());
  }

  public void respondToAskForDeviceRegistrationRequest(final AskForDeviceRegistrationRequest request, final AskForDeviceRegistrationResponse response, final ResponseListener listener) {
    String address = Addresses.getAskForDeviceRegistrationResponseAddress(request.getAddress(), request.getPort());
    response.setRequestMessageId(request.getMessageId());

    dispatcher.sendMessageAsync(address, response, new CommunicatorResponseListener() {
      @Override
      public void responseReceived(Response communicatorResponse) {
        dispatchResponse(response, communicatorResponse, listener);

        if (response.allowsRegistration() && communicatorResponse.getResponseCode() == ResponseCode.Ok)
          communicatorListener.serverAllowedDeviceRegistration(request, response);
      }
    });
  }

  public void notifyRemoteWeHaveConnected(ConnectedDevice connectedDevice) {
    notifyRemoteWeHaveConnected(connectedDevice, getLocalHostInfo());
  }

  public void notifyRemoteWeHaveConnected(ConnectedDevice connectedDevice, ConnectedDevice localHost) {
    String address = Addresses.getNotifyRemoteWeHaveConnectedAddress(connectedDevice.getAddress(), connectedDevice.getMessagesPort());
    final Request request = new GenericRequest<ConnectedDevice>(localHost);

    dispatcher.sendMessageAsync(address, request, new CommunicatorResponseListener() {
      @Override
      public void responseReceived(Response communicatorResponse) {
        dispatchResponse(request, communicatorResponse);
      }
    });
  }

  public void sendHeartbeat(ConnectedDevice connectedDevice, final ResponseListener listener) {
    sendHeartbeat(connectedDevice, getLocalHostInfo(), listener);
  }

  public void sendHeartbeat(ConnectedDevice connectedDevice, ConnectedDevice localHost, final ResponseListener listener) {
    String address = Addresses.getHeartbeatAddress(connectedDevice.getAddress(), connectedDevice.getMessagesPort());
    final Request request = new GenericRequest<ConnectedDevice>(localHost);

    dispatcher.sendMessageAsync(address, request, new CommunicatorResponseListener() {
      @Override
      public void responseReceived(Response communicatorResponse) {
        dispatchResponse(request, communicatorResponse, listener);
      }
    });
  }


  public RequestWithAsynchronousResponse startCaptureImage(ConnectedDevice deviceToDoTheJob, CaptureImageResultListener listener) {
    String address = Addresses.getStartCaptureImageAddress(deviceToDoTheJob.getAddress(), deviceToDoTheJob.getMessagesPort());
    final RequestWithAsynchronousResponse request = new RequestWithAsynchronousResponse(getIpAddressToSendResponseTo(), getMessageReceiverPort());

    listenerManager.addListenerForResponse(request, listener);

    dispatcher.sendMessageAsync(address, request, new CommunicatorResponseListener() {
      @Override
      public void responseReceived(Response communicatorResponse) {
        dispatchResponse(request, communicatorResponse); // TODO: if an error occurred inform caller
      }
    });

    return request;
  }

//  public void stopCaptureImage(int messageId, ConnectedDevice deviceToDoTheJob, final ResponseListener listener) {
//    if(listenerManager.removeListenerForMessageId(messageId) == false) {
//      log.error("stopCaptureImage() has been called but there was no Listener registered for MessageId " + messageId);
//    }
//
//    String address = Addresses.getStopCaptureImageAddress(deviceToDoTheJob.getAddress(), deviceToDoTheJob.getMessagesPort());
//    final StopRequestWithAsynchronousResponse stopRequest = new StopRequestWithAsynchronousResponse(messageId);
//
//    dispatcher.sendMessageAsync(address, stopRequest, new CommunicatorResponseListener() {
//      @Override
//      public void responseReceived(Response communicatorResponse) {
//        dispatchResponse(stopRequest, communicatorResponse, listener);
//      }
//    });
//  }

  public void respondToCaptureImageRequest(RequestWithAsynchronousResponse request, CaptureImageResult result, final ResponseListener listener) {
    String address = Addresses.getCaptureImageResultAddress(request.getAddress(), request.getPort());
    final CaptureImageResultResponse response = new CaptureImageResultResponse(result, request.getMessageId());

    dispatcher.sendMultipartMessageAsync(address, response, new CommunicatorResponseListener() {
      @Override
      public void responseReceived(Response communicatorResponse) {
        dispatchResponse(response, communicatorResponse, listener);
      }
    });
  }


  public RequestWithAsynchronousResponse startCaptureImageAndDoOcr(ConnectedDevice deviceToDoTheJob, CaptureImageAndDoOcrResultListener listener) {
    String address = Addresses.getStartCaptureImageAndDoOcrAddress(deviceToDoTheJob.getAddress(), deviceToDoTheJob.getMessagesPort());
    final RequestWithAsynchronousResponse request = new RequestWithAsynchronousResponse(getIpAddressToSendResponseTo(), getMessageReceiverPort());

    listenerManager.addListenerForResponse(request, listener);

    dispatcher.sendMessageAsync(address, request, new CommunicatorResponseListener() {
      @Override
      public void responseReceived(Response communicatorResponse) {
        dispatchResponse(request, communicatorResponse); // TODO: if an error occurred inform caller
      }
    });

    return request;
  }

//  public void stopCaptureImageAndDoOcr(int messageId, ConnectedDevice deviceToDoTheJob, final ResponseListener listener) {
//    if(listenerManager.removeListenerForMessageId(messageId) == false) {
//      log.error("stopCaptureImageOrDoOcr() has been called but there was no Listener registered for MessageId " + messageId);
//    }
//
//    String address = Addresses.getStopCaptureImageAndDoOcrAddress(deviceToDoTheJob.getAddress(), deviceToDoTheJob.getMessagesPort());
//    final StopRequestWithAsynchronousResponse stopRequest = new StopRequestWithAsynchronousResponse(messageId);
//
//    dispatcher.sendMessageAsync(address, stopRequest, new CommunicatorResponseListener() {
//      @Override
//      public void responseReceived(Response communicatorResponse) {
//        dispatchResponse(stopRequest, communicatorResponse, listener);
//      }
//    });
//  }

  public void respondToCaptureImageAndDoOcrRequest(RequestWithAsynchronousResponse request, final TextRecognitionResult ocrResult, final ResponseListener listener) {
    String address = Addresses.getOcrResultAddress(request.getAddress(), request.getPort());
    final OcrResultResponse response = new OcrResultResponse(ocrResult, request.getMessageId());

    dispatcher.sendMessageAsync(address, response, new CommunicatorResponseListener() {
      @Override
      public void responseReceived(Response communicatorResponse) {
        dispatchResponse(response, communicatorResponse, listener);
      }
    });
  }


  public DoOcrOnImageRequest startDoOcrOnImage(ConnectedDevice deviceToDoTheJob, DoOcrConfiguration configuration, final DoOcrOnImageResultListener listener) {
    String address = Addresses.getDoOcrOnImageAddress(deviceToDoTheJob.getAddress(), deviceToDoTheJob.getMessagesPort());
    final DoOcrOnImageRequest request = new DoOcrOnImageRequest(getIpAddressToSendResponseTo(), getMessageReceiverPort(), configuration);

    listenerManager.addListenerForResponse(request, listener);

    dispatcher.sendMultipartMessageAsync(address, request, new CommunicatorResponseListener() {
      @Override
      public void responseReceived(Response communicatorResponse) {
        dispatchResponse(request, communicatorResponse); // TODO: if an error occurred inform caller
      }
    });

    return request;
  }


  public void respondToDoOcrOnImageRequest(final DoOcrOnImageRequest request, final TextRecognitionResult ocrResult, final ResponseListener listener) {
    String address = Addresses.getOcrResultAddress(request.getAddress(), request.getPort());
    final OcrResultResponse response = new OcrResultResponse(ocrResult, request.getMessageId());

    dispatcher.sendMessageAsync(address, response, new CommunicatorResponseListener() {
      @Override
      public void responseReceived(Response communicatorResponse) {
        dispatchResponse(response, communicatorResponse, listener);
      }
    });
  }


  protected void dispatchResponse(Request request, Response response) {
    dispatchResponse(request, response, null);
  }

  protected void dispatchResponse(Request request, Response response, ResponseListener listener) {
    try {
      communicatorListener.responseReceived(request, response);
    } catch(Exception ex) { log.error("An error occurred calling Communicator's communicatorListener (so the error certainly is in the listener method)", ex); }

    if(listener != null) {
      try {
        listener.responseReceived(request, response);
      } catch (Exception ex2) { log.error("An error occurred calling method's ResponseListener (so the error certainly is in the listener method)", ex2); }
    }
  }


  protected String getIpAddressToSendResponseTo() {
    return NetworkHelper.getIPAddressString(true);
  }

  protected int getMessageReceiverPort() {
    return messageReceiverPort;
  }

  protected void setMessageReceiverPort(int messageReceiverPort) {
    this.messageReceiverPort = messageReceiverPort;
  }

  protected ConnectedDevice getLocalHostInfo() {
    return connectorMessagesCreator.getLocalHostDevice();
  }

}
