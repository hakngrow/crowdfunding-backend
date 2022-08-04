package com.aestus.api.ledger.controller;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.ledger.exception.CreateTransactionException;
import com.aestus.api.ledger.exception.LedgerException;
import com.aestus.api.ledger.model.Ledger;
import com.aestus.api.profile.model.UserProfile;
import com.aestus.api.profile.model.swagger.ResponseMessageWithUserProfile;

import com.aestus.api.transaction.model.Transaction;
import com.aestus.api.transaction.model.swagger.ResponseMessageWithTransaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

import static com.aestus.api.common.controller.CommonController.getHttpHeaders;

/** Provides the ledger controller functionality. */
@Slf4j
@RestController
@RequestMapping("api/v1/ledger")
@Validated
public class LedgerController {

  @Autowired RestTemplate restTemplate;
  @Autowired ObjectMapper objectMapper;

  @Value("${com.aestus.base.url}")
  private final String urlBase = null;

  @Value("${com.aestus.profile.get.token.url}")
  private final String urlProfileGetByToken = null;

  @Value("${com.aestus.transaction.create.url}")
  private final String urlTransactionCreate = null;

  protected Ledger ledger = new Ledger();

  /**
   * Pinging the controller.
   *
   * @param request the http request
   * @return the ping returned message in the {@code ResponseEntity} container object
   */
  @GetMapping("/ping")
  @Operation(
      summary = "Ping test",
      tags = {"Ledger"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns standard ping test response",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/ledger/ledger-ping-200.json",
                          value = ""))
            })
      })
  public ResponseEntity<ResponseMessage> ping(HttpServletRequest request) {

    String baseUrl = request.getRequestURI();

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), "ping pong", baseUrl);

    return new ResponseEntity<ResponseMessage>(msg, HttpStatus.OK);
  }

  /**
   * Get balance of an account specified by the {@code walletId}.
   *
   * @param walletId the wallet id of the account
   * @param httpRequest the http request
   * @return the balance if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/balance/{walletId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve account balance",
      tags = {"Ledger"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the balance of an account in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/ledger/ledger-get-balance-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-08T17:50:38.2014849\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": 1000000000,\n"
                                    + "  \"path\": \"/api/v1/ledger/balance/4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getBalance(
      @PathVariable String walletId, HttpServletRequest httpRequest) {

    Long balance = this.ledger.getBalance(walletId);

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), balance, httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Retrieves the user profile associated with the jwt token
   *
   * @param request the http request
   * @return the response message with the user profile
   */
  protected UserProfile getUserProfile(HttpServletRequest request) throws LedgerException {
    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(getHttpHeaders(request));

    // Retrieve user profile from the jwt token
    ResponseEntity<ResponseMessageWithUserProfile> response =
        restTemplate.exchange(
            urlBase + urlProfileGetByToken,
            HttpMethod.GET,
            entity,
            ResponseMessageWithUserProfile.class);

    if (response.getBody().isOk()) return response.getBody().getData();
    else throw new LedgerException(response.getBody().getMessage());
  }

  /**
   * Get balance of an account using the jwt.
   *
   * @param httpRequest the http request
   * @return the balance if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/balance/token")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve account balance by the profileId",
      tags = {"Ledger"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the balance of an account in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/ledger/ledger-get-balance-token-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-12T21:15:01.9429381\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": 1000000000,\n"
                                    + "  \"path\": \"/api/v1/ledger/balance/token\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getBalance(HttpServletRequest httpRequest)
      throws LedgerException {

    UserProfile profile = getUserProfile(httpRequest);

    Long balance = this.ledger.getBalance(profile.getWalletId());

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), balance, httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Creates a transaction entry
   *
   * @param transaction the transaction entry
   * @param request the http request
   * @return the response message with the created transaction
   */
  protected void createTransaction(Transaction transaction, HttpServletRequest request)
      throws CreateTransactionException, JsonProcessingException {

    String jsonTransaction = objectMapper.writeValueAsString(transaction);

    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(jsonTransaction, getHttpHeaders(request));

    ResponseMessageWithTransaction msg =
        restTemplate
            .exchange(
                urlBase + urlTransactionCreate,
                HttpMethod.POST,
                entity,
                ResponseMessageWithTransaction.class)
            .getBody();

    if (!msg.isOk()) throw new CreateTransactionException(msg.getMessage());
  }

  /**
   * Transfer an {@code amount} of tokens between 2 accounts.
   *
   * @param fromWalletId the from wallet address
   * @param toWalletId the to wallet address
   * @param amount the amount of tokens to be transferred
   * @param httpRequest the http request
   * @return the {@code ResponseMessage} containing confirmation of the balances after the transfer
   */
  @PostMapping("/transfer")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Transfer tokens between 2 accounts",
      tags = {"Ledger"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Returns a confirmation message with balances of the accounts after the transfer",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/ledger/ledger-transfer-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-07-08T18:16:56.973177\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"After transfer, from wallet balance=999990000, to wallet balance=1000010000\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/ledger/transfer\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Unable to transfer tokens due to validation reasons.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/ledger/ledger-transfer-400.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-07-08T18:17:35.199669\",\n"
                                  + "  \"status\": 400,\n"
                                  + "  \"message\": \"InvalidBalanceException: Invalid balance @ wallet address qJJZvUgCRtJMNHqq91EcoStYw8NhWyszrtWRVLhVmw\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/ledger/transfer\",\n"
                                  + "  \"ok\": false\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
      })
  public ResponseEntity<ResponseMessage> transfer(
      @RequestParam(required = true) String fromWalletId,
      @RequestParam(required = true) String toWalletId,
      @RequestParam(required = true) Long amount,
      HttpServletRequest httpRequest)
      throws LedgerException, JsonProcessingException {

    String uri = httpRequest.getRequestURI();

    this.ledger.transfer(fromWalletId, toWalletId, amount);

    long balanceFrom = this.ledger.getBalance(fromWalletId);
    long balanceTo = this.ledger.getBalance(toWalletId);

    // Creates a transaction record
    Transaction transaction =
        new Transaction("T", fromWalletId, amount * -1, balanceFrom, toWalletId, amount, balanceTo);

    createTransaction(transaction, httpRequest);

    String reason = "After transfer, from wallet balance=%d, to wallet balance=%d";

    ResponseMessage msg =
        new ResponseMessage(
            HttpStatus.OK.value(), String.format(reason, balanceFrom, balanceTo), uri);

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets the ledger entries.
   *
   * @param httpRequest the http request
   * @return the ledger entries
   */
  @GetMapping("/entries")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Retrieve the ledger",
      tags = {"Ledger"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the ledger entries in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/ledger/ledger-entries-200.json",
                            value = ""))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getEntries(HttpServletRequest httpRequest) {

    ResponseMessage msg =
        new ResponseMessage(
            HttpStatus.OK.value(), this.ledger.getEntries(), httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }
}
