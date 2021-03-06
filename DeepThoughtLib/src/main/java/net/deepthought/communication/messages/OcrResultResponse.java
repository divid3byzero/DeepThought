package net.deepthought.communication.messages;

import net.deepthought.data.contentextractor.ocr.TextRecognitionResult;

/**
 * Created by ganymed on 23/08/15.
 */
public class OcrResultResponse extends Request {

  protected TextRecognitionResult textRecognitionResult;

  protected int messageId;


  public OcrResultResponse(TextRecognitionResult textRecognitionResult, int messageId) {
    this.textRecognitionResult = textRecognitionResult;
    this.messageId = messageId;
  }


  public TextRecognitionResult getTextRecognitionResult() {
    return textRecognitionResult;
  }

  public int getMessageId() {
    return messageId;
  }


  @Override
  public String toString() {
    return "" + textRecognitionResult;
  }

}
