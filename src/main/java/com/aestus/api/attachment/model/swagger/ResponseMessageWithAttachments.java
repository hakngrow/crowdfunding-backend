package com.aestus.api.attachment.model.swagger;

import com.aestus.api.attachment.model.Attachment;
import com.aestus.api.common.model.ResponseMessage;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with an array of Attachments as the payload. Used for schema
 * documentation in Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithAttachments extends ResponseMessage {

  @Override
  public Attachment[] getData() {
    return (Attachment[]) super.getData();
  }

  /**
   * Sets data payload.
   *
   * @param attachments the array of attachments
   */
  public void setData(Attachment[] attachments) {
    super.setData(attachments);
  }
}
