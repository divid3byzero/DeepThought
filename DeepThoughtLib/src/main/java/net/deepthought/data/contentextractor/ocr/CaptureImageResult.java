package net.deepthought.data.contentextractor.ocr;

/**
 * Created by ganymed on 18/08/15.
 */
public class CaptureImageResult {

  protected boolean isUserCancelled = false;

  protected String errorMessage = null;

  protected boolean successful = false;

  protected boolean isDone = false;

  protected byte[] imageData = null;


  public CaptureImageResult() {

  }

  public CaptureImageResult(byte[] imageData) {
    this.imageData = imageData;
    this.successful = true;
  }

  public boolean isUserCancelled() {
    return isUserCancelled;
  }

  public void setIsUserCancelled(boolean isUserCancelled) {
    this.isUserCancelled = isUserCancelled;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public boolean successful() {
    return successful;
  }

  public boolean isDone() {
    return isDone;
  }

  public void setIsDone(boolean isDone) {
    this.isDone = isDone;
  }

  public byte[] getImageData() {
    return imageData;
  }

  public void setImageData(byte[] imageData) {
    this.imageData = imageData;
  }


  @Override
  public String toString() {
    if(isUserCancelled())
      return "User cancelled";
    else if(isDone())
      return "Done";
    else
      return "Successfully captured Image";
  }


  public static CaptureImageResult createRecognitionProcessDoneResult() {
    CaptureImageResult result = new CaptureImageResult();
    result.setIsDone(true);

    return result;
  }

  public static CaptureImageResult createUserCancelledResult() {
    CaptureImageResult result = new CaptureImageResult();
    result.setIsUserCancelled(true);

    return result;
  }

  public static CaptureImageResult createCapturingSuccessfulResult(byte[] imageData) {
    CaptureImageResult result = new CaptureImageResult(imageData);

    return result;
  }

  public static CaptureImageResult createCapturingSuccessfulResult(byte[] imageData, boolean isDone) {
    CaptureImageResult result = new CaptureImageResult(imageData);
    result.setIsDone(isDone);

    return result;
  }

}
