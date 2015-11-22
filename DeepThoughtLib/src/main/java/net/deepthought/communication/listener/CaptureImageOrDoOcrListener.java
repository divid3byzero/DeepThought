package net.deepthought.communication.listener;

import net.deepthought.communication.messages.request.CaptureImageOrDoOcrRequest;
import net.deepthought.communication.messages.request.StopRequestWithAsynchronousResponse;

/**
 * Created by ganymed on 23/08/15.
 */
public interface CaptureImageOrDoOcrListener {

  void startCaptureImageOrDoOcr(CaptureImageOrDoOcrRequest request);

  void stopCaptureImageOrDoOcr(StopRequestWithAsynchronousResponse request);
}
