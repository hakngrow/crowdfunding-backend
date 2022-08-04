package com.aestus.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * The ResponseMessage instance is the returned object from a REST API call of a controller class
 * (i.e. from {@code controller} package). The instance provides important information and returned
 * payload as the outcome of an API call.
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessage {

  /** The timestamp of the response. */
  private LocalDateTime timestamp;

  /** The HTTP status response. */
  private int status;

  /** Any error or status message. */
  private String message;

  /** Returned payload data if any */
  private Object data;

  /** Uri path invoked */
  private String path;

  /**
   * Instantiates a new Response message.
   *
   * @param status the HTTP status
   * @param message the message
   * @param data the payload data
   * @param path the uri path invoked
   */
  public ResponseMessage(int status, String message, Object data, String path) {
    this.timestamp = LocalDateTime.now();
    this.status = status;
    this.message = message;
    this.data = data;
    this.path = path;
  }

  /**
   * Instantiates a new Response message with payload only.
   *
   * @param status the HTTP status
   * @param data the payload data
   * @param path the uri path invoked
   */
  public ResponseMessage(int status, Object data, String path) {
    this(status, "", data, path);
  }

  /**
   * Instantiates a new Response message with an exception, used in error handling.
   *
   * @param status the HTTP status
   * @param ex the exception handled
   * @param path the uri path invoked
   */
  public ResponseMessage(HttpStatus status, Exception ex, String path) {
    this(status.value(), ex.getClass().getSimpleName()+ ": " + ex.getMessage(), path);
  }

  /**
   * Instantiates a new Response message without payload data.
   *
   * @param status the HTTP status
   * @param message the message
   * @param path the uri path invoked
   */
  public ResponseMessage(int status, String message, String path) {
    this(status, message, null, path);
  }

  /**
   * Check if response status is OK
   *
   * @return {@code true} if {@code status} equals 200 (HttpStatus OK)
   */
  public boolean isOk() {
    return this.status == HttpStatus.OK.value();
  }
}
