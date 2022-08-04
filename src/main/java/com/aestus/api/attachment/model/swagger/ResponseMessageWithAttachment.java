package com.aestus.api.attachment.model.swagger;

import com.aestus.api.attachment.model.Attachment;
import com.aestus.api.common.model.ResponseMessage;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with a Attachment as the payload. Used for schema documentation in
 * Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithAttachment extends ResponseMessage {

  @Override
  public Attachment getData() {
    return (Attachment) super.getData();
  }

  /**
   * Sets data payload.
   *
   * @param attachment the attachment
   */
  public void setData(Attachment attachment) {
    super.setData(attachment);
  }
}
