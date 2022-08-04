package com.aestus.api.blockchain.model.swagger;

import com.aestus.api.blockchain.model.Quote;
import com.aestus.api.common.model.ResponseMessage;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with a Quote instance as the payload. Used for schema documentation
 * in Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithQuote extends ResponseMessage {
  @Override
  public Quote getData() {
    return (Quote) super.getData();
  }

  /**
   * Sets data payload.
   *
   * @param quote the quote
   */
  public void setData(Quote quote) {
    super.setData(quote);
  }
}
