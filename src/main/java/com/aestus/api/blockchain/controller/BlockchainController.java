package com.aestus.api.blockchain.controller;

import com.aestus.api.blockchain.exception.InvalidHttpMethodException;
import com.aestus.api.blockchain.model.Balance;
import com.aestus.api.blockchain.model.Deposit;
import com.aestus.api.blockchain.model.Quote;
import com.aestus.api.blockchain.model.Transfer;
import com.aestus.api.blockchain.model.swagger.ResponseMessageWithBalance;
import com.aestus.api.blockchain.model.swagger.ResponseMessageWithDeposit;
import com.aestus.api.blockchain.model.swagger.ResponseMessageWithQuote;
import com.aestus.api.blockchain.model.swagger.ResponseMessageWithTransfer;
import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.common.util.JWTUtils;
import com.aestus.api.profile.model.UserProfile;
import com.aestus.api.profile.model.swagger.ResponseMessageWithUserProfile;
import com.aestus.api.transaction.model.Transaction;
import com.aestus.api.transaction.model.swagger.ResponseMessageWithTransaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static com.aestus.api.common.controller.CommonController.getHttpHeaders;

/** Provides the blockchain controller functionality. */
@Slf4j
@RestController
@RequestMapping("api/v1/blockchain")
@Validated
public class BlockchainController {
  /** The Rest template. */
  @Autowired RestTemplate restTemplate;

  /** The Object mapper. */
  @Autowired ObjectMapper objectMapper;

  /** The jwt utilities. */
  @Autowired JWTUtils jwtUtils;

  @Value("${com.aestus.solana.base.server}")
  private final String bcServer = null;

  @Value("${com.aestus.solana.base.port}")
  private final String bcPort = null;

  @Value("${com.aestus.solana.base.url}")
  private final String bcUrlBase = null;

  private String urlBlockchainBase;

  @Value("${com.aestus.solana.deposit.url}")
  private final String urlDeposit = null;

  @Value("${com.aestus.solana.transfer.url}")
  private final String urlTransfer = null;

  @Value("${com.aestus.solana.wallet.address.url}")
  private final String urlWalletAddress = null;

  @Value("${com.aestus.solana.wallet.balance.url}")
  private final String urlWalletBalance = null;

  @Value("${com.aestus.solana.nft.get.url}")
  private final String urlNFTGet = null;

  @Value("${com.aestus.solana.nft.get.all.url}")
  private final String urlNFTGetAll = null;

  @Value("${com.aestus.solana.nft.get.owner.url}")
  private final String urlNFTGetOwner = null;

  @Value("${com.aestus.solana.nft.create.url}")
  private final String urlNFTCreate = null;

  @Value("${com.aestus.coinapi.rate.url}")
  private final String urlRate = null;

  @Value("${com.aestus.base.url}")
  private final String urlBase = null;

  @Value("${com.aestus.profile.get.email.url}")
  private final String urlProfileGetByEmail = null;

  @Value("${com.aestus.profile.get.token.url}")
  private final String urlProfileGetByToken = null;

  @Value("${com.aestus.transaction.create.url}")
  private final String urlTransactionCreate = null;

  public BlockchainController() {}

  protected String getUrlBlockchainBase() {
    if (urlBlockchainBase == null) urlBlockchainBase = String.format(bcUrlBase, bcServer, bcPort);

    log.info("### " + urlBlockchainBase);

    return urlBlockchainBase;
  }

  /**
   * Pinging the controller.
   *
   * @param request the request
   * @return the ping returned message in the {@code ResponseEntity} container object
   */
  @GetMapping("/ping")
  @Operation(
      summary = "Ping test",
      tags = {"Blockchain"},
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
                              "http://localhost:8080/swagger/blockchain/blockchain-ping-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-05-23T11:34:15.1883792\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"ping pong\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/blockchain/ping\"\n"
                                  + "}"))
            })
      })
  public ResponseEntity<ResponseMessage> test(HttpServletRequest request) {

    String baseUrl = request.getRequestURI();

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), "ping pong", baseUrl);

    return new ResponseEntity<ResponseMessage>(msg, HttpStatus.OK);
  }

  /**
   * Gets the exchange rate between 2 assets from the blockchain api.
   *
   * @param baseAsset the base asset
   * @param quoteAsset the quote asset
   * @param request the http request
   * @return the exchange rate
   */
  @GetMapping("/rate/{baseAsset}/{quoteAsset}")
  @PreAuthorize(
      "hasAuthority('A') or hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I')")
  @Operation(
      summary = "Returns the exchange rate quote from a base asset to a quote asset",
      tags = {"Blockchain"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the exchange rate quote in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithQuote.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-rate-get-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-05-26T16:11:56.849452\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"time\": \"2022-05-26T08:11:56.4\",\n"
                                    + "    \"asset_id_base\": \"SOL\",\n"
                                    + "    \"asset_id_quote\": \"USD\",\n"
                                    + "    \"rate\": 45.990594735807484\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/blockchain/rate/sol/usd\"\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getRate(
      @PathVariable @NotBlank String baseAsset,
      @PathVariable @NotBlank String quoteAsset,
      HttpServletRequest request) {

    String uri = request.getRequestURI();
    String url = String.format(urlRate, baseAsset.toUpperCase(), quoteAsset.toUpperCase());

    HttpHeaders headers = new HttpHeaders();
    headers.set("X-CoinAPI-Key", "89A2E66F-C49F-4334-8F3F-09594BAFDE2D");

    HttpEntity<String> entity = new HttpEntity<String>(headers);

    Quote quote;
    ResponseMessage msg;

    try {
      // Retrieve exchange rate from blockchain api
      String strQuote = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();

      // Configure the object mapper date format to prevent timestamps being converted to arrays
      ObjectMapper mapper = new ObjectMapper();
      mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));

      quote = objectMapper.readValue(strQuote, Quote.class);

      msg = new ResponseMessage(HttpStatus.OK.value(), quote, uri);

      return ResponseEntity.ok(msg);
    }
    // Catch RestClientException from restTemplate and JsonProcessingException objectMapper
    catch (Exception ex) {
      log.error(ex.getMessage());

      msg = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), uri);

      return ResponseEntity.internalServerError().body(msg);
    }
  }

  /**
   * Generates a wallet address from the blockchain
   *
   * @return a wallet address on the blockchain
   */
  @GetMapping("/wallet/address")
  @Operation(
      summary = "Returns a wallet address",
      tags = {"Blockchain"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a generated wallet address in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-wallet-address-get-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-05-26T16:50:11.8362457\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": \"97WijCdpLsk214BCgB8WwASM8b8QXTojasB6nUHmSxgK\",\n"
                                    + "  \"path\": \"/api/v1/blockchain/wallet\"\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> getWalletAddress(HttpServletRequest request) {

    String uri = request.getRequestURI();
    ResponseMessage msg;

    Object address =
        restTemplate
            .getForObject(getUrlBlockchainBase() + urlWalletAddress, String.class)
            .replaceAll("[^a-zA-Z0-9]", "");

    msg = new ResponseMessage(HttpStatus.OK.value(), address, uri);
    return ResponseEntity.ok(msg);
  }

  /**
   * Checks and returns the balance of a wallet address
   *
   * @param walletId the wallet address
   * @param request the http request
   * @return the balance of the wallet address
   */
  @GetMapping("/wallet/balance/{walletId}")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Returns the balance of the wallet by wallet id",
      tags = {"Blockchain"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the wallet balance in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithBalance.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-wallet-balance-get-walletId-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-03T15:48:10.5393986\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"address\": \"5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK\",\n"
                                    + "    \"balance\": 1000000000,\n"
                                    + "    \"symbol\": \"SOL\",\n"
                                    + "    \"units\": \"lamport\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/blockchain/wallet/balance/5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description = "Unable to get wallet balance due to validation reasons.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-wallet_balance-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-07T14:15:38.2583567\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"getWalletBalance.walletId: size must be between 32 and 44\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/blockchain/wallet/balance/1234567890\"\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getWalletBalance(
      @PathVariable
          @Size(min = 32, max = 44, message = "walletId must contain between 32 to 44 characters")
          @Pattern(
              regexp = "[a-zA-Z0-9]+",
              message = "walletId must not contain special characters")
          String walletId,
      HttpServletRequest request) {

    String uri = request.getRequestURI();
    String url = getUrlBlockchainBase() + String.format(urlWalletBalance, walletId);
    ResponseMessage msg;

    Balance balance = restTemplate.getForObject(url, Balance.class);

    msg = new ResponseMessage(HttpStatus.OK.value(), balance, uri);

    return ResponseEntity.ok().body(msg);
  }

  /**
   * Retrieves the user profile associated with the jwt token
   *
   * @param request the http request
   * @return the response message with the user profile
   */
  protected ResponseMessage getUserProfile(HttpServletRequest request) {
    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(getHttpHeaders(request));

    // Retrieve user profile from the jwt token
    ResponseEntity<ResponseMessageWithUserProfile> response =
        restTemplate.exchange(
            urlBase + urlProfileGetByToken,
            HttpMethod.GET,
            entity,
            ResponseMessageWithUserProfile.class);

    return response.getBody();
  }

  /**
   * Retrieves the user profile associated with the email address
   *
   * @param email the email address
   * @param request the http request
   * @return the response message with the user profile
   */
  protected ResponseMessage getUserProfile(String email, HttpServletRequest request) {
    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(getHttpHeaders(request));

    String url = urlBase + String.format(urlProfileGetByEmail, email);

    // Retrieve user profile from the jwt token
    ResponseEntity<ResponseMessageWithUserProfile> response =
        restTemplate.exchange(url, HttpMethod.GET, entity, ResponseMessageWithUserProfile.class);

    return response.getBody();
  }

  /**
   * Checks and returns the balance of the wallet address of the user profile associated with the
   * jwt token
   *
   * @param request the http request
   * @return the balance of the wallet address
   */
  @GetMapping("/wallet/balance")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Returns the wallet balance of the user profile based on the jwt token",
      tags = {"Blockchain"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the wallet balance in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithBalance.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-wallet-balance-get-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-03T15:46:44.4557915\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"address\": \"5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK\",\n"
                                    + "    \"balance\": 1000000000,\n"
                                    + "    \"symbol\": \"SOL\",\n"
                                    + "    \"units\": \"lamport\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/blockchain/wallet/balance\"\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getWalletBalance(HttpServletRequest request) {
    ResponseMessage msg = getUserProfile(request);

    if (msg.isOk()) {
      UserProfile profile = (UserProfile) msg.getData();

      return getWalletBalance(profile.getWalletId(), request);
    } else return ResponseEntity.internalServerError().body(msg);
  }

  /**
   * Airdrops the {@code amount} of Lamports to the wallet address {@code walletId}
   *
   * @param walletId the wallet address
   * @param amount the quantity of Lamports to airdrop
   * @param request the http request
   * @return the balance of the wallet address
   */
  @PostMapping("/deposit")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Deposits a specified amount of Lamports to a wallet address",
      tags = {"Blockchain"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a deposit confirmation hash in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithDeposit.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-deposit-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-06T21:39:40.1454592\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"address\": \"5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK\",\n"
                                    + "    \"symbol\": \"SOL\",\n"
                                    + "    \"txHash\": \"2SPTFsUGRyorm4cD5q6EdpGnc9JFcgxQLEinT7NUEG93qpnfk1zsWzujbAvt8WqaFzv7SDtEL62UpAdu8S354tvU\",\n"
                                    + "    \"depositAmount\": 7000,\n"
                                    + "    \"units\": \"lamport\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/blockchain/deposit\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description = "Unable to execute deposit due to validation reasons.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-deposit-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-07T14:08:12.389515\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"deposit.amount: must be greater than or equal to 1\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/blockchain/deposit\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description =
                "Unable to execute deposit due to unexpected conditions e.g. invalid wallet address",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-deposit-500.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-07T12:32:29.4025581\",\n"
                                    + "  \"status\": 500,\n"
                                    + "  \"message\": \"Internal server error\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/blockchain/deposit\"\n"
                                    + "}"))),
      })
  public ResponseEntity<ResponseMessage> deposit(
      @RequestParam(required = false) @Size(min = 32, max = 44) String walletId,
      @RequestParam @Min(1) long amount,
      HttpServletRequest request) {

    String uri = request.getRequestURI();
    ResponseMessage msg;

    // If not provided, get wallet id of user profile from jwt token
    if (walletId == null) {
      msg = getUserProfile(request);

      if (msg.isOk()) {
        walletId = ((UserProfile) msg.getData()).getWalletId();
      } else return ResponseEntity.internalServerError().body(msg);
    }

    // Perform airdrop using blockchain api
    String url = getUrlBlockchainBase() + String.format(urlDeposit, walletId, amount);
    Deposit resDeposit = restTemplate.getForObject(url, Deposit.class);

    if (resDeposit.isOk()) {

      // Create transaction record for deposit
      ResponseMessage msgTransaction = createTransaction("D", walletId, walletId, amount, request);

      if (!msgTransaction.isOk()) {
        return ResponseEntity.internalServerError().body(msgTransaction);
      }

      msg = new ResponseMessage(HttpStatus.OK.value(), resDeposit, uri);
      return ResponseEntity.ok(msg);
    } else {
      msg = new ResponseMessage(resDeposit.getCode(), resDeposit.getMessage(), uri);
      return new ResponseEntity<>(msg, HttpStatus.resolve(resDeposit.getCode()));
    }
  }

  /**
   * Creates a record of the transfer transaction
   *
   * @param type the transaction type
   * @param fromWalletId the sender wallet address
   * @param toWalletId the receiver wallet address
   * @param amount the quantity of Lamports to transfer
   * @param request the http request
   * @return the response message with the embedded {@code Transaction} object
   */
  protected ResponseMessage createTransaction(
      String type,
      String fromWalletId,
      String toWalletId,
      Long amount,
      HttpServletRequest request) {

    Long fromBalance = null;
    Long toBalance = null;

    // Retrieve balance of sender wallet address
    ResponseMessage msgBalance = getWalletBalance(fromWalletId, request).getBody();
    if (msgBalance.isOk()) fromBalance = ((Balance) msgBalance.getData()).getBalance();

    msgBalance = getWalletBalance(toWalletId, request).getBody();
    if (msgBalance.isOk()) toBalance = ((Balance) msgBalance.getData()).getBalance();

    Transaction transaction =
        new Transaction(
            type, fromWalletId, amount * -1, fromBalance, toWalletId, amount, toBalance);

    try {
      String jsonTransaction = objectMapper.writeValueAsString(transaction);

      String url =
          urlBase + String.format(urlTransactionCreate, type, fromWalletId, toWalletId, amount);

      // Duplicate authorization headers from request
      HttpEntity entity = new HttpEntity<String>(jsonTransaction, getHttpHeaders(request));

      // Create transaction
      ResponseEntity<ResponseMessageWithTransaction> response =
          restTemplate.exchange(url, HttpMethod.POST, entity, ResponseMessageWithTransaction.class);

      return response.getBody();
    } catch (JsonProcessingException jpEx) {
      return new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, jpEx, request.getRequestURI());
    }
  }

  /**
   * Performs a transfer transaction via the blockchain api
   *
   * @param fromWalletId the sender wallet address
   * @param toWalletId the receiver wallet address
   * @param amount the quantity of Lamports to transfer
   * @param request the http request
   * @return the {@code Transfer} response embedded in a {@code ResponseMessage} instance
   */
  @PostMapping("/transfer")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary =
          "Transfer a specified amount of Lamports from a wallet address to another wallet address",
      tags = {"Blockchain"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a transfer confirmation hash in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithTransfer.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-transfer-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-07T14:47:24.9895932\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"symbol\": \"SOL\",\n"
                                    + "    \"txHash\": \"45ieFsAUs4N7rTnCPBnErHr92rpxANRVH8h4otZo4warhDfM7j3md1YpLW8X5BG3KBbCWpMDjVnnMEKTc9gKJJ9q\",\n"
                                    + "    \"address\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\",\n"
                                    + "    \"receiver\": \"5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK\",\n"
                                    + "    \"transferAmount\": 10,\n"
                                    + "    \"units\": \"lamport\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/blockchain/transfer\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description = "Unable to execute transfer due to validation reasons.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-transfer-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-07T14:08:12.389515\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"deposit.amount: must be greater than or equal to 1\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/blockchain/deposit\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description =
                "Unable to execute transfer due to unexpected conditions e.g. invalid wallet address",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-transfer-500.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-07T14:57:56.4042094\",\n"
                                    + "  \"status\": 500,\n"
                                    + "  \"message\": \"Internal server error\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/blockchain/transfer\"\n"
                                    + "}"))),
      })
  public ResponseEntity<ResponseMessage> transfer(
      @RequestParam(required = false)
          @Size(
              min = 32,
              max = 44,
              message = "fromWalletId must contain between 32 to 44 characters")
          @Pattern(
              regexp = "[a-zA-Z0-9]+",
              message = "fromWalletId must not contain special characters")
          String fromWalletId,
      @RequestParam
          @NotBlank
          @Size(min = 32, max = 44, message = "toWalletId must contain between 32 to 44 characters")
          @Pattern(
              regexp = "[a-zA-Z0-9]+",
              message = "toWalletId must not contain special characters")
          String toWalletId,
      @RequestParam @NotNull @Min(1) long amount,
      HttpServletRequest request) {

    String uri = request.getRequestURI();
    ResponseMessage msg;

    // If not provided, get fromWalletId of user profile from jwt token
    if (fromWalletId == null) {
      msg = getUserProfile(request);

      if (msg.isOk()) {
        fromWalletId = ((UserProfile) msg.getData()).getWalletId();
      } else return ResponseEntity.internalServerError().body(msg);
    }

    String url = getUrlBlockchainBase() + String.format(urlTransfer, fromWalletId, toWalletId, amount);

    Transfer resTransfer = restTemplate.getForObject(url, Transfer.class);

    if (resTransfer.isOk()) {

      // Creates transaction record for the transfer
      ResponseMessage msgTransaction =
          createTransaction("T", fromWalletId, toWalletId, amount, request);

      if (!msgTransaction.isOk()) {
        return ResponseEntity.internalServerError().body(msgTransaction);
      }

      msg = new ResponseMessage(HttpStatus.OK.value(), resTransfer, uri);
      return ResponseEntity.ok(msg);
    } else {
      msg = new ResponseMessage(resTransfer.getCode(), resTransfer.getMessage(), uri);
      return new ResponseEntity<>(msg, HttpStatus.resolve(resTransfer.getCode()));
    }
  }

  /**
   * Performs a transfer transaction from the wallet address associated with the jwt token to the
   * wallet address associated with the {@code email} address.
   *
   * @param email the receiver email address
   * @param amount the quantity of Lamports to transfer
   * @param request the http request
   * @return the {@code Transfer} response embedded in a {@code ResponseMessage} instance
   */
  @PostMapping("/transfer/email")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary =
          "Transfer a specified amount of Lamports from the sender wallet address (retrieved from the jwt token) to a receiver address (retrieved from user profile by email address)",
      tags = {"Blockchain"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a transfer confirmation hash in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithTransfer.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-transfer-email-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-08T09:52:40.6976671\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"code\": 0,\n"
                                    + "    \"message\": null,\n"
                                    + "    \"symbol\": \"SOL\",\n"
                                    + "    \"txHash\": \"2bFRfpemx2z8YzqP3cGrfBZtHomNdYXmccASAA8Ks4Ho9NLhTykCfrVDPDL95CSmUQ7GVxzJgPTpbzgcC3ssuCgg\",\n"
                                    + "    \"address\": \"5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK\",\n"
                                    + "    \"receiver\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\",\n"
                                    + "    \"transferAmount\": 1000,\n"
                                    + "    \"units\": \"lamport\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/blockchain/transfer/email\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description = "Unable to execute transfer due to validation reasons.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-transfer-email-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-08T09:54:00.8313232\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"Required request parameter 'email' for method parameter type String is not present\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/blockchain/transfer/email\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description =
                "Unable to execute transfer due to unexpected conditions e.g. invalid wallet address",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-transfer-email-500.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-08T10:08:14.7150792\",\n"
                                    + "  \"status\": 500,\n"
                                    + "  \"message\": \"org.springframework.web.client.ResourceAccessException: I/O error on GET request for \\\"http://172.28.176.166:8080/api/v1/sol/transfer\\\": No route to host: connect; nested exception is java.net.NoRouteToHostException: No route to host: connect\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/blockchain/transfer/email\"\n"
                                    + "}"))),
      })
  public ResponseEntity<ResponseMessage> transfer(
      @RequestParam
          @NotBlank(message = "email must not be blank")
          @Email(message = "email must be a valid format")
          String email,
      @RequestParam @NotNull @Min(1) int amount,
      HttpServletRequest request) {

    String fromWalletId;
    String toWalletId;

    // Retrieve fromWalletId from jwt token
    ResponseMessage msg = getUserProfile(request);

    if (msg.isOk()) {
      fromWalletId = ((UserProfile) msg.getData()).getWalletId();
    } else return ResponseEntity.internalServerError().body(msg);

    // Retrieve toWalletId from email
    msg = getUserProfile(email, request);

    if (msg.isOk()) {
      toWalletId = ((UserProfile) msg.getData()).getWalletId();
    } else return ResponseEntity.internalServerError().body(msg);

    return transfer(fromWalletId, toWalletId, amount, request);
  }

  /**
   * Retrieves the JSON metadata of a NFT at the specified {@code address}.
   *
   * @param address the mint address of the NFT
   * @param request the http request
   * @return the JSON metadata of the NFT embedded in a {@code ResponseMessage} instance
   */
  @GetMapping("/nft/address/{address}")
  @PreAuthorize("hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieves the JSON metadata of the NFT at the specified address",
      tags = {"Blockchain"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a JSON metadata of the NFT in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-nft-get-200.json",
                            value = ""))),
        @ApiResponse(
            responseCode = "400",
            description = "Unable to retrieve NFT due to validation reasons.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-nft-get-400.json",
                            value = ""))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description =
                "Unable to retrieve NFT due to unexpected conditions e.g. invalid address",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-nft-get-500.json",
                            value = ""))),
      })
  public ResponseEntity<ResponseMessage> getNFT(
      @PathVariable @NotBlank(message = "address must not be blank") String address,
      HttpServletRequest request)
      throws InvalidHttpMethodException {

    String url = getUrlBlockchainBase() + String.format(urlNFTGet, address);

    return callAPI(url, HttpMethod.GET, request);
  }

  public ResponseEntity<ResponseMessage> getNFTsOwnedBy(
      @PathVariable @NotBlank(message = "owner must not be blank") String owner,
      HttpServletRequest request)
      throws InvalidHttpMethodException {

    String url = getUrlBlockchainBase() + String.format(urlNFTGetOwner, owner);

    return callAPI(url, HttpMethod.GET, request);
  }

  /**
   * Retrieves the JSON metadata of all NFTs.
   *
   * @param request the http request
   * @return the JSON metadata of all NFTs embedded in a {@code ResponseMessage} instance
   */
  @GetMapping("/nft/")
  @PreAuthorize("hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieves the JSON metadata of all NFTs",
      tags = {"Blockchain"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the JSON metadata of all NFTs in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-nft-get-all-200.json",
                            value = ""))),
        @ApiResponse(
            responseCode = "400",
            description = "Unable to retrieve NFT metadata due to validation reasons.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-nft-get-all-400.json",
                            value = ""))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description =
                "Unable to retrieve NFT metadata due to unexpected conditions e.g. invalid address",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-nft-get-all-500.json",
                            value = ""))),
      })
  public ResponseEntity<ResponseMessage> getAllNFTs(HttpServletRequest request)
      throws InvalidHttpMethodException {

    return callAPI(getUrlBlockchainBase() + urlNFTGetAll, HttpMethod.GET, request);
  }

  /**
   * Creates a NFT on the blockchain
   *
   * @param name the name of the NFT
   * @param description the description of the NFT
   * @param request the http request
   * @return the response from the blockchain
   */
  @PostMapping("/nft/")
  @PreAuthorize("hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Create a NFT on the blockchain",
      tags = {"Blockchain"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the response from the blockchain",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-nft-create-200.json",
                            value = ""))),
        @ApiResponse(
            responseCode = "400",
            description = "Unable to create NFT due to validation reasons.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-nft-create-400.json",
                            value = ""))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Unable to create NFT due to unexpected conditions e.g. blank name",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/blockchain/blockchain-nft-create-500.json",
                            value = ""))),
      })
  public ResponseEntity<ResponseMessage> createNFT(
      @RequestParam @NotBlank(message = "name must not be blank") String name,
      @RequestParam @NotBlank(message = "description must not be blank") String description,
      HttpServletRequest request)
      throws InvalidHttpMethodException {

    String url = getUrlBlockchainBase() + String.format(urlNFTCreate, name, description);

    return callAPI(url, HttpMethod.POST, request);
  }

  /**
   * Make a REST API call to the blockchain
   *
   * @param url the blockchain API endpoint
   * @param method the HTTP method to use in the API call
   * @param request the http request
   * @return the response message with the embedded {@code Transaction} object
   */
  public ResponseEntity<ResponseMessage> callAPI(
      String url, HttpMethod method, HttpServletRequest request) throws InvalidHttpMethodException {
    String uri = request.getRequestURI();

    Object response;
    if (method.matches(HttpMethod.GET.name()))
      response = restTemplate.getForObject(url, String.class);
    else if (method.matches(HttpMethod.POST.name()))
      response = restTemplate.postForObject(url, null, String.class);
    else throw new InvalidHttpMethodException(method);

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), response, uri);
    return ResponseEntity.ok(msg);
  }
}
