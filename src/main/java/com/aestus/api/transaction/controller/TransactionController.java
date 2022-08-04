package com.aestus.api.transaction.controller;

import com.aestus.api.blockchain.model.swagger.ResponseMessageWithBalance;
import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.profile.model.UserProfile;
import com.aestus.api.profile.model.swagger.ResponseMessageWithUserProfile;
import com.aestus.api.transaction.model.Transaction;

import com.aestus.api.transaction.model.swagger.ResponseMessageWithTransaction;
import com.aestus.api.transaction.model.swagger.ResponseMessageWithTransactions;
import com.aestus.api.transaction.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.aestus.api.common.controller.CommonController.getBadRequestResponse;
import static com.aestus.api.common.controller.CommonController.getHttpHeaders;

/** Provides the transaction controller functionality. */
@Slf4j
@RestController
@RequestMapping("api/v1/transaction")
@Validated
public class TransactionController {
  @Autowired private TransactionService transactionService;
  @Autowired RestTemplate restTemplate;

  @Value("${com.aestus.base.url}")
  private final String urlBase = null;

  @Value("${com.aestus.profile.get.token.url}")
  private final String urlProfileGetByToken = null;

  @Value("${com.aestus.blockchain.get.wallet.balance.url}")
  private final String urlBlockchainGetWalletBalance = null;

  /**
   * Pinging the controller.
   *
   * @param request the request
   * @return the ping returned message in the {@code ResponseEntity} container object
   */
  @GetMapping("/ping")
  @Operation(
      summary = "Ping test",
      tags = {"Transaction"},
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
                              "http://localhost:8080/swagger/transaction/transaction-ping-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-05-23T11:34:15.1883792\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"ping pong\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/profile/ping\"\n"
                                  + "}"))
            })
      })
  public ResponseEntity<ResponseMessage> ping(HttpServletRequest request) {

    String baseUrl = request.getRequestURI();

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), "ping pong", baseUrl);

    return new ResponseEntity<ResponseMessage>(msg, HttpStatus.OK);
  }

  /**
   * Creates a transaction entry via a {@code Transaction} object in the request body.
   *
   * @param transaction the transaction entry
   * @param result the validation result
   * @param request the http request
   * @return the {@code ResponseMessage} containing the transaction entry created with the
   *     auto-generated {@code id}
   */
  @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Creates a new transaction entry via a <code>Transaction</code> json object",
      tags = {"Transaction"},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description =
                  "Requires a transaction json object, the <code>id</code> field (if specified) will be ignored",
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              externalValue =
                                  "http://localhost:8080/swagger/transaction/transaction-create-req.json",
                              value =
                                  "{\n"
                                      + "  \"type\": \"M\",\n"
                                      + "  \"receiverWalletId\": \"5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK\",\n"
                                      + "  \"senderWalletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\",\n"
                                      + "  \"receiverAmount\": 444000,\n"
                                      + "  \"senderAmount\": -444000,\n"
                                      + "  \"receiverBalance\": 4000000,\n"
                                      + "  \"senderBalance\": 400444,\n"
                                      + "  \"createdTimestamp\": \"2022-06-03T15:31:49.1426443\"\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Returns a transaction (with the auto-generated <code>id</code>) in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithTransaction.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/transaction/transaction-create-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-08T21:55:46.6925759\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 10,\n"
                                    + "    \"type\": \"M\",\n"
                                    + "    \"senderWalletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\",\n"
                                    + "    \"senderAmount\": -444000,\n"
                                    + "    \"senderBalance\": 400444,\n"
                                    + "    \"receiverWalletId\": \"5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK\",\n"
                                    + "    \"receiverAmount\": 444000,\n"
                                    + "    \"receiverBalance\": 4000000,\n"
                                    + "    \"createdTimestamp\": \"2022-06-03T15:31:49.1426443\",\n"
                                    + "    \"hash\": \"Vv9U2gRgqdjo62DE1Qjx\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/transaction/\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to create transaction due to validation reasons.  Refer to schema of Transaction for details.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/transaction/transaction-create-400.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-08T21:56:35.7986444\",\n"
                                  + "  \"status\": 400,\n"
                                  + "  \"message\": \"javax.validation.ConstraintViolationException: create.transaction.senderBalance: senderBalance must not be Null, create.transaction.senderBalance: senderBalance must not be Null\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/transaction/\"\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
      })
  public ResponseEntity<ResponseMessage> create(
      @Valid @RequestBody Transaction transaction,
      BindingResult result,
      HttpServletRequest request) {

    ResponseMessage msg;
    String uri = request.getRequestURI();

    try {

      // Check for constraint violations
      ResponseEntity<ResponseMessage> response = getBadRequestResponse(result, uri);
      if (response != null) return response;

      Transaction created = transactionService.createTransaction(transaction);

      msg = new ResponseMessage(HttpStatus.OK.value(), created, uri);

      return ResponseEntity.ok(msg);
    } catch (RuntimeException ex) {

      log.error(ex.getMessage());

      msg = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), uri);

      return ResponseEntity.internalServerError().body(msg);
    }
  }

  /**
   * Gets wallet balance by calling the blockchain api.
   *
   * @param walletId the wallet id
   * @param request the http request
   * @return the {@code ResponseMessage} from the blockchain api
   */
  protected ResponseMessageWithBalance getWalletBalance(
      String walletId, HttpServletRequest request) {

    String url = urlBase + String.format(urlBlockchainGetWalletBalance, walletId);

    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity(getHttpHeaders(request));

    // Retrieve user profile from token
    ResponseEntity<ResponseMessageWithBalance> response =
        restTemplate.exchange(url, HttpMethod.GET, entity, ResponseMessageWithBalance.class);

    return response.getBody();
  }

  /**
   * Updates a transaction.
   *
   * @param transaction the transaction
   * @param result the validation result
   * @param request the request
   * @return the {@code ResponseMessage} containing the transaction updated
   */
  @PutMapping("/")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Updates a transaction",
      tags = {"Transaction"},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description =
                  "Requires a transaction json object, fields will be updated based on the primary key <code>id</code> ",
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              externalValue =
                                  "http://localhost:8080/swagger/transaction/transaction-update-req.json",
                              value =
                                  "{\n"
                                      + "  \"id\": 9,\n"
                                      + "  \"type\": \"M\",\n"
                                      + "  \"receiverWalletId\": \"5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK\",\n"
                                      + "  \"senderWalletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\",\n"
                                      + "  \"receiverAmount\": 8000,\n"
                                      + "  \"senderAmount\": -6000,\n"
                                      + "  \"receiverBalance\": -700000,\n"
                                      + "  \"senderBalance\": 222000,\n"
                                      + "  \"createdTimestamp\": \"2022-06-03T15:31:49.1426443\"\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns updated transaction in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithTransaction.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/transaction/transaction-update-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-08T23:22:28.6355968\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 9,\n"
                                    + "    \"type\": \"M\",\n"
                                    + "    \"senderWalletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\",\n"
                                    + "    \"senderAmount\": -6000,\n"
                                    + "    \"senderBalance\": 222000,\n"
                                    + "    \"receiverWalletId\": \"5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK\",\n"
                                    + "    \"receiverAmount\": 8000,\n"
                                    + "    \"receiverBalance\": 700000,\n"
                                    + "    \"createdTimestamp\": \"2022-06-03T15:31:49.1426443\",\n"
                                    + "    \"hash\": \"Vv9U2gRgqdjo62DE1Qjx\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/transaction/\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to update transaction due to validation reasons. Refer to schema of Transaction for details.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/transaction/transaction-update-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-08T23:23:20.0614616\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"javax.validation.ConstraintViolationException: update.transaction.receiverBalance: receiverBalance must be positive\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/transaction/\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Transaction with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/transaction/transaction-update-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-05T17:18:45.3558951\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"Transaction with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/transaction/\"\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> update(
      @Valid @RequestBody Transaction transaction,
      BindingResult result,
      HttpServletRequest request) {

    String uri = request.getRequestURI();
    ResponseMessage msg;

    // Set transaction hash before persisting
    transaction.setHash();

    try {

      ResponseEntity<ResponseMessage> response = getBadRequestResponse(result, uri);

      if (response != null) return response;

      Optional<Transaction> optTransaction =
          transactionService.getTransactionById(transaction.getId());

      if (optTransaction.isPresent()) {

        Transaction updated = transactionService.updateTransaction(transaction);

        msg = new ResponseMessage(HttpStatus.OK.value(), updated, uri);

        return ResponseEntity.ok(msg);
      } else {

        String reason = "Transaction with id=%d NOT found.";
        msg =
            new ResponseMessage(
                HttpStatus.NOT_FOUND.value(), String.format(reason, transaction.getId()), uri);

        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
      }

    } catch (RuntimeException ex) {

      log.error(ex.getMessage());

      msg = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), uri);

      return ResponseEntity.internalServerError().body(msg);
    }
  }

  /**
   * Gets transaction by id.
   *
   * @param id the id
   * @param request the http request
   * @return the user transaction with the id if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/id/{id}")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve user profile by id",
      tags = {"Transaction"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a transaction in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithTransaction.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/transaction/transaction-get-id-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-05T17:54:55.3948183\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 9,\n"
                                    + "    \"receiverWalletId\": \"aabbccddee\",\n"
                                    + "    \"senderWalletId\": \"ffgghhiijj\",\n"
                                    + "    \"receiverAmount\": 44,\n"
                                    + "    \"senderAmount\": 100,\n"
                                    + "    \"receiverBalance\": 88,\n"
                                    + "    \"senderBalance\": 99,\n"
                                    + "    \"createdTimestamp\": \"2022-06-03T15:31:49.142644\",\n"
                                    + "    \"property\": 77,\n"
                                    + "    \"hash\": \"XxxYyy\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/transaction/id/9\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Transaction with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/transaction/transaction-get-id-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-05T17:55:55.7432463\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"Transaction with id=7 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/transaction/id/7\"\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> getById(
      @PathVariable Integer id, HttpServletRequest request) {

    Optional<Transaction> optTransaction = transactionService.getTransactionById(id);

    ResponseMessage msg;

    if (optTransaction.isPresent()) {

      msg =
          new ResponseMessage(HttpStatus.OK.value(), optTransaction.get(), request.getRequestURI());

      return new ResponseEntity<>(msg, HttpStatus.OK);
    } else {

      String reason = "Transaction with id=%d NOT found.";

      msg =
          new ResponseMessage(
              HttpStatus.NOT_FOUND.value(), String.format(reason, id), request.getRequestURI());

      return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Gets transactions by receiver wallet Id.
   *
   * @param walletId the receiver wallet id
   * @param request the http request
   * @return transactions by receiver wallet Id
   */
  @GetMapping("/receiver/{walletId}")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve transactions by receiver wallet id",
      tags = {"Transaction"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns all user profiles in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithTransactions.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/transaction/transaction-get-receiverWalletId-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-05T18:16:03.8964823\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"\",\n"
                                  + "  \"data\": [\n"
                                  + "    {\n"
                                  + "      \"id\": 9,\n"
                                  + "      \"receiverWalletId\": \"aabbccddee\",\n"
                                  + "      \"senderWalletId\": \"abcdabcdabcd\",\n"
                                  + "      \"receiverAmount\": 22,\n"
                                  + "      \"senderAmount\": 222,\n"
                                  + "      \"receiverBalance\": 2222,\n"
                                  + "      \"senderBalance\": 22222,\n"
                                  + "      \"createdTimestamp\": \"2022-06-03T15:31:49.142644\",\n"
                                  + "      \"property\": 2,\n"
                                  + "      \"hash\": \"222hhh\"\n"
                                  + "    },\n"
                                  + "    {\n"
                                  + "      \"id\": 10,\n"
                                  + "      \"receiverWalletId\": \"aabbccddee\",\n"
                                  + "      \"senderWalletId\": \"zzzxxxyyyzzz\",\n"
                                  + "      \"receiverAmount\": 33,\n"
                                  + "      \"senderAmount\": 333,\n"
                                  + "      \"receiverBalance\": 3333,\n"
                                  + "      \"senderBalance\": 33333,\n"
                                  + "      \"createdTimestamp\": \"2022-06-03T15:31:49.142644\",\n"
                                  + "      \"property\": 3,\n"
                                  + "      \"hash\": \"333333\"\n"
                                  + "    }\n"
                                  + "  ],\n"
                                  + "  \"path\": \"/api/v1/transaction/receiver/aabbccddee\"\n"
                                  + "}"))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByReceiverWalletId(
      @PathVariable String walletId, HttpServletRequest request) {
    ResponseMessage msg;

    try {

      Iterable<Transaction> transactions =
          transactionService.getTransactionsByReceiverWalletId(walletId);

      msg = new ResponseMessage(HttpStatus.OK.value(), transactions, request.getRequestURI());

      return ResponseEntity.ok(msg);
    } catch (RuntimeException ex) {

      log.error(ex.getMessage());

      msg = new ResponseMessage(HttpStatus.OK.value(), ex.getMessage(), request.getRequestURI());

      return ResponseEntity.internalServerError().body(msg);
    }
  }

  /**
   * Gets transactions by sender wallet Id.
   *
   * @param walletId the sender wallet id
   * @param request the http request
   * @return transactions by sender wallet Id
   */
  @GetMapping("/sender/{walletId}")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve transactions by sender wallet id",
      tags = {"Transaction"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns transactions in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithTransactions.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/transaction/transaction-get-senderWalletId-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-05T18:42:10.4509743\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"\",\n"
                                  + "  \"data\": [\n"
                                  + "    {\n"
                                  + "      \"id\": 9,\n"
                                  + "      \"receiverWalletId\": \"eeefffggghhh\",\n"
                                  + "      \"senderWalletId\": \"ironmanWallet\",\n"
                                  + "      \"receiverAmount\": 11,\n"
                                  + "      \"senderAmount\": 111,\n"
                                  + "      \"receiverBalance\": 1111,\n"
                                  + "      \"senderBalance\": 11111,\n"
                                  + "      \"createdTimestamp\": \"2022-06-03T15:31:49.142644\",\n"
                                  + "      \"property\": 1,\n"
                                  + "      \"hash\": \"111111\"\n"
                                  + "    },\n"
                                  + "    {\n"
                                  + "      \"id\": 10,\n"
                                  + "      \"receiverWalletId\": \"thorwallet\",\n"
                                  + "      \"senderWalletId\": \"ironmanWallet\",\n"
                                  + "      \"receiverAmount\": 22,\n"
                                  + "      \"senderAmount\": 222,\n"
                                  + "      \"receiverBalance\": 2222,\n"
                                  + "      \"senderBalance\": 22222,\n"
                                  + "      \"createdTimestamp\": \"2022-06-03T15:31:49.142644\",\n"
                                  + "      \"property\": 2,\n"
                                  + "      \"hash\": \"2222\"\n"
                                  + "    }\n"
                                  + "  ],\n"
                                  + "  \"path\": \"/api/v1/transaction/sender/ironmanwallet\"\n"
                                  + "}"))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getBySenderWalletId(
      @PathVariable String walletId, HttpServletRequest request) {
    ResponseMessage msg;

    try {

      Iterable<Transaction> transactions =
          transactionService.getTransactionsBySenderWalletId(walletId);

      msg = new ResponseMessage(HttpStatus.OK.value(), transactions, request.getRequestURI());

      return ResponseEntity.ok(msg);
    } catch (RuntimeException ex) {

      log.error(ex.getMessage());

      msg = new ResponseMessage(HttpStatus.OK.value(), ex.getMessage(), request.getRequestURI());

      return ResponseEntity.internalServerError().body(msg);
    }
  }

  /**
   * Gets sending and receiving transactions by wallet Id.
   *
   * @param walletId the wallet id
   * @param request the http request
   * @return sending and receiving transactions by wallet Id
   */
  @GetMapping("/walletId/{walletId}")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve transactions by wallet id",
      tags = {"Transaction"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns transactions in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithTransactions.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/transaction/transaction-get-walletId-200.json",
                          value = ""))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByWalletId(
      @PathVariable String walletId, HttpServletRequest request) {
    ResponseMessage msg;

    try {

      Iterable<Transaction> transactions = transactionService.getTransactionsByWalletId(walletId);

      msg =
          new ResponseMessage(
              HttpStatus.OK.value(), transactions.iterator(), request.getRequestURI());

      return ResponseEntity.ok(msg);
    } catch (RuntimeException ex) {

      log.error(ex.getMessage());

      msg = new ResponseMessage(HttpStatus.OK.value(), ex.getMessage(), request.getRequestURI());

      return ResponseEntity.internalServerError().body(msg);
    }
  }

  @GetMapping("/token")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve transactions by user profile using the jwt token",
      tags = {"Transaction"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns transactions in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithTransactions.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/transaction/transaction-get-token-200.json",
                          value = ""))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByToken(HttpServletRequest request) {

    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(getHttpHeaders(request));

    // Retrieve user profile from token
    ResponseEntity<ResponseMessageWithUserProfile> response =
        restTemplate.exchange(
            urlBase + urlProfileGetByToken,
            HttpMethod.GET,
            entity,
            ResponseMessageWithUserProfile.class);

    ResponseMessage msg = response.getBody();

    if (msg.isOk()) {
      UserProfile profile = (UserProfile) msg.getData();

      return getByWalletId(profile.getWalletId(), request);
    } else return ResponseEntity.internalServerError().body(msg);
  }

  /**
   * Gets all transactions.
   *
   * @param request the http request
   * @return all transactions
   */
  @GetMapping("/")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve all transactions",
      tags = {"Transaction"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns all transactions in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithTransactions.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/transaction/transaction-get-all-200.json",
                          value = ""))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getAll(HttpServletRequest request) {
    ResponseMessage msg;

    try {

      Iterable<Transaction> transactions = transactionService.getAllTransactions();

      msg = new ResponseMessage(HttpStatus.OK.value(), transactions, request.getRequestURI());

      return ResponseEntity.ok(msg);
    } catch (RuntimeException ex) {

      log.error(ex.getMessage());

      msg = new ResponseMessage(HttpStatus.OK.value(), ex.getMessage(), request.getRequestURI());

      return ResponseEntity.internalServerError().body(msg);
    }
  }

  /**
   * Removes a transaction by {@code id}.
   *
   * @param id the transaction id
   * @param request the http request
   * @return HTTP status NOT FOUND if the transaction with {@code id} is not found. If the delete is
   *     successful, the HTTP status OK.
   */
  @DeleteMapping("/id/{id}")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Remove transaction by id",
      tags = {"Transaction"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns deleted transaction in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithTransaction.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/transaction/transaction-delete-id-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-05T18:46:10.9470282\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"Transaction with id=10 deleted.\",\n"
                                  + "  \"data\": {\n"
                                  + "    \"id\": 10,\n"
                                  + "    \"receiverWalletId\": \"thorwallet\",\n"
                                  + "    \"senderWalletId\": \"ironmanWallet\",\n"
                                  + "    \"receiverAmount\": 22,\n"
                                  + "    \"senderAmount\": 222,\n"
                                  + "    \"receiverBalance\": 2222,\n"
                                  + "    \"senderBalance\": 22222,\n"
                                  + "    \"createdTimestamp\": \"2022-06-03T15:31:49.142644\",\n"
                                  + "    \"property\": 2,\n"
                                  + "    \"hash\": \"2222\"\n"
                                  + "  },\n"
                                  + "  \"path\": \"/api/v1/transaction/id/10\"\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Transaction with <code>id</code> not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/transaction/transaction-delete-id-404.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-05T18:47:13.8390887\",\n"
                                  + "  \"status\": 404,\n"
                                  + "  \"message\": \"Transaction with id=100 NOT found.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/transaction/id/100\"\n"
                                  + "}"))
            })
      })
  public ResponseEntity<ResponseMessage> removeById(
      @PathVariable int id, HttpServletRequest request) {

    String uri = request.getRequestURI();
    String reason;
    ResponseMessage msg;

    try {

      Optional<Transaction> optTransaction = transactionService.getTransactionById(id);

      if (optTransaction.isPresent()) {

        transactionService.deleteTransactionById(id);

        reason = "Transaction with id=%d deleted.";
        msg =
            new ResponseMessage(
                HttpStatus.OK.value(), String.format(reason, id), optTransaction.get(), uri);

        return ResponseEntity.ok(msg);
      } else {

        reason = "Transaction with id=%d NOT found.";
        msg = new ResponseMessage(HttpStatus.NOT_FOUND.value(), String.format(reason, id), uri);

        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
      }
    } catch (Exception ex) {

      log.error(ex.getMessage());

      msg = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), uri);

      return ResponseEntity.internalServerError().body(msg);
    }
  }

  /**
   * Removes all transactions.
   *
   * @param request the http request
   * @return the HTTP status OK is successful
   */
  @DeleteMapping("/")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Removes all transactions",
      tags = {"Transaction"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "All transactions deleted",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/transaction/transaction-delete-all-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-05T18:48:54.763686\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"All transactions deleted.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/transaction/\"\n"
                                  + "}"))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> removeAll(HttpServletRequest request) {
    String uri = request.getRequestURI();
    ResponseMessage msg;

    try {
      transactionService.deleteAllTransactions();

      String reason = "All transactions deleted.";
      msg = new ResponseMessage(HttpStatus.OK.value(), reason, uri);

      return ResponseEntity.ok(msg);
    } catch (Exception ex) {

      log.error(ex.getMessage());

      msg = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), uri);

      return ResponseEntity.internalServerError().body(msg);
    }
  }
}
