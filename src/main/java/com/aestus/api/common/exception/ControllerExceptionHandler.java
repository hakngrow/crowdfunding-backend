package com.aestus.api.common.exception;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.contract.exception.ContractException;
import com.aestus.api.ledger.exception.LedgerException;
import com.aestus.api.request.exception.RequestException;

import javax.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** The type Controller exception handler centralize exception handling for controllers. */
@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Handles contract exceptions.
   *
   * @param ex the exception
   * @param request the http request
   * @return the response entity
   */
  @ExceptionHandler(value = {ContractException.class})
  protected ResponseEntity<Object> handleContractException(
      ContractException ex, WebRequest request) {

    String uri = request.getDescription(false).split("=")[1];

    ResponseMessage msg = new ResponseMessage(HttpStatus.BAD_REQUEST, ex, uri);

    return ResponseEntity.badRequest().body(msg);
  }

  /**
   * Handles ledger exceptions.
   *
   * @param ex the exception
   * @param request the http request
   * @return the response entity
   */
  @ExceptionHandler(value = {LedgerException.class})
  protected ResponseEntity<Object> handleLedgerException(LedgerException ex, WebRequest request) {

    String uri = request.getDescription(false).split("=")[1];

    ResponseMessage msg = new ResponseMessage(HttpStatus.BAD_REQUEST, ex, uri);

    return ResponseEntity.badRequest().body(msg);
  }

  /**
   * Handles request exceptions.
   *
   * @param ex the exception
   * @param request the http request
   * @return the response entity
   */
  @ExceptionHandler(value = {RequestException.class})
  protected ResponseEntity<Object> handleRequestException(RequestException ex, WebRequest request) {

    String uri = request.getDescription(false).split("=")[1];

    ResponseMessage msg = new ResponseMessage(HttpStatus.BAD_REQUEST, ex, uri);

    return ResponseEntity.badRequest().body(msg);
  }

  /**
   * Handles entity not found exceptions.
   *
   * @param ex the exception
   * @param request the http request
   * @return the response entity
   */
  @ExceptionHandler(value = {EntityNotFoundException.class})
  protected ResponseEntity<Object> handleEntityNotFound(
      EntityNotFoundException ex, WebRequest request) {

    String uri = request.getDescription(false).split("=")[1];

    ResponseMessage msg = new ResponseMessage(HttpStatus.NOT_FOUND, ex, uri);

    return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
  }

  /**
   * Handles constraint violation exceptions.
   *
   * @param ex the exception
   * @param request the http request
   * @return the response entity
   */
  @ExceptionHandler(value = {ConstraintViolationException.class})
  protected ResponseEntity<Object> handleConstraintViolation(
      ConstraintViolationException ex, WebRequest request) {

    String uri = request.getDescription(false).split("=")[1];

    ResponseMessage msg = new ResponseMessage(HttpStatus.BAD_REQUEST, ex, uri);

    return ResponseEntity.badRequest().body(msg);
  }

  /**
   * Handles missing request parameter exceptions.
   *
   * @param ex the exception
   * @param headers the request headers
   * @param status the http status
   * @param request the http request
   * @return the response entity
   */
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {

    String uri = request.getDescription(false).split("=")[1];

    ResponseMessage msg = new ResponseMessage(status, ex, uri);

    return ResponseEntity.badRequest().body(msg);
  }

  /**
   * Handles transaction system exceptions.
   *
   * @param ex the ex
   * @param request the request
   * @return the response entity
   */
  @ExceptionHandler(value = {TransactionSystemException.class})
  protected ResponseEntity<Object> handleTransactionSystemException(
      TransactionSystemException ex, WebRequest request) {

    log.info("### caught by handleTransactionSystemException");

    String uri = request.getDescription(false).split("=")[1];

    ResponseMessage msg;

    // Check if the root cause is a constraint violation.
    Throwable cause = ((TransactionSystemException) ex).getRootCause();
    if (cause instanceof ConstraintViolationException) {

      // When cause is a constraint violation, return a bad request status instead of an internal
      // server error
      ConstraintViolationException cvEx = (ConstraintViolationException) cause;

      msg = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), cvEx.getMessage(), uri);

      return ResponseEntity.badRequest().body(msg);
    } else {
      msg = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex, uri);

      return ResponseEntity.internalServerError().body(msg);
    }
  }

  /**
   * Handles all other exceptions.
   *
   * @param ex the exception
   * @param request the http request
   * @return the response entity
   */
  @ExceptionHandler({Exception.class})
  public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {

    log.error("### Caught by handleAllExceptions " + ex.getClass().getName());

    ex.printStackTrace();

    String uri = request.getDescription(false).split("=")[1];

    ResponseMessage msg = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex, uri);

    return ResponseEntity.internalServerError().body(msg);
  }
}
