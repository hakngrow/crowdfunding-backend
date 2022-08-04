package com.aestus.api.contract.controller;

import com.aestus.api.common.exception.EntityNotFoundException;
import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.contract.exception.*;
import com.aestus.api.contract.model.Contract;
import com.aestus.api.contract.model.swagger.ResponseMessageWithContract;
import com.aestus.api.contract.model.swagger.ResponseMessageWithContracts;
import com.aestus.api.contract.service.ContractService;
import com.aestus.api.funding.model.Funding;
import com.aestus.api.funding.model.swagger.ResponseMessageWithFunding;
import com.aestus.api.funding.model.swagger.ResponseMessageWithFundings;

import com.aestus.api.profile.model.swagger.ResponseMessageWithUserProfile;
import com.aestus.api.request.exception.GetUserProfileException;
import com.aestus.api.request.model.RequestForFunding;
import com.aestus.api.request.model.swagger.ResponseMessageWithRequestForFunding;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

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

import static com.aestus.api.common.controller.CommonController.getHttpHeaders;

/** Provides the contract controller functionality. */
@Slf4j
@RestController
@RequestMapping("api/v1/contract")
@Validated
public class ContractController {
  @Autowired private ContractService contractService;
  @Autowired RestTemplate restTemplate;
  @Autowired ObjectMapper objectMapper;

  @Value("${com.aestus.base.url}")
  private final String urlBase = null;

  @Value("${com.aestus.funding.get.contractId.url}")
  private final String urlFundingGetByContractId = null;

  @Value("${com.aestus.funding.get.profileId.url}")
  private final String urlFundingGetByProfileId = null;

  @Value("${com.aestus.funding.create.url}")
  private final String urlFundingCreate = null;

  @Value("${com.aestus.funding.disburse.url}")
  private final String urlFundingDisburse = null;

  @Value("${com.aestus.request.update.status.url}")
  private final String urlRequestUpdateStatus = null;

  @Value("${com.aestus.request.get.id.url}")
  private final String urlRequestGetId = null;

  @Value("${com.aestus.ledger.transfer.url}")
  private final String urlLedgerTransfer = null;

  @Value("${com.aestus.profile.get.id.url}")
  private final String urlProfileGetById = null;

  /**
   * Pinging the controller.
   *
   * @param request the http request
   * @return the ping returned message in the {@code ResponseEntity} container object
   */
  @GetMapping("/ping")
  @Operation(
      summary = "Ping test",
      tags = {"Contract"},
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
                              "http://localhost:8080/swagger/contract/contract-ping-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-07-06T16:06:22.9304063\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"ping pong\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/contract/ping\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            })
      })
  public ResponseEntity<ResponseMessage> ping(HttpServletRequest request) {

    String baseUrl = request.getRequestURI();

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), "ping pong", baseUrl);

    return new ResponseEntity<ResponseMessage>(msg, HttpStatus.OK);
  }

  /**
   * Creates a contract.
   *
   * @param contract the contract
   * @param result the validation result
   * @param httpRequest the http request
   * @return the {@code ResponseMessage} containing the contract created with the auto-generated
   *     {@code id}
   */
  @PostMapping("/")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Creates a new contract",
      tags = {"Contract"},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description =
                  "Requires a contract json object, the <code>id</code> field (if specified) will be ignored",
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = Contract.class),
                      examples =
                          @ExampleObject(
                              externalValue =
                                  "http://localhost:8080/swagger/contract/contract-create-req.json",
                              value =
                                  "{\n"
                                      + "    \"requestId\": 3,\n"
                                      + "    \"walletId\": \"GAyCywe7wYQ49XA92BrDBVvj2CMKeEGMmGjseQR3yFua\",\n"
                                      + "    \"targetAmount\": 1300000,\n"
                                      + "    \"repaymentAmount\": 1500000,\n"
                                      + "    \"status\": \"NF\",\n"
                                      + "    \"createdTimestamp\": \"2022-07-06T20:48:28.4621798\"\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Returns contract (with the auto-generated <code>id</code>) in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithContract.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/contract/contract-create-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-07-10T13:04:07.0153558\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"\",\n"
                                  + "  \"data\": {\n"
                                  + "    \"id\": 1,\n"
                                  + "    \"requestId\": 1,\n"
                                  + "    \"walletId\": \"GAyCywe7wYQ49XA92BrDBVvj2CMKeEGMmGjseQR3yFua\",\n"
                                  + "    \"targetAmount\": 1000000,\n"
                                  + "    \"repaymentAmount\": 1200000,\n"
                                  + "    \"status\": \"O\",\n"
                                  + "    \"createdTimestamp\": \"2022-07-06T20:48:28.4621798\",\n"
                                  + "    \"fundings\": null\n"
                                  + "  },\n"
                                  + "  \"path\": \"/api/v1/contract/\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to create contract due to validation reasons.  Refer to schema of Contract for details.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/contract/contract-create-400-requestId.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-07-06T20:56:48.2076296\",\n"
                                  + "  \"status\": 400,\n"
                                  + "  \"message\": \"ConstraintViolationException: create.contract.requestId: requestId must not be Null, create.contract.requestId: requestId must not be Null\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/contract/\",\n"
                                  + "  \"ok\": false\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Unable to create a contract with a duplicate requestId.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-create-500.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-07T14:15:48.1395338\",\n"
                                    + "  \"status\": 500,\n"
                                    + "  \"message\": \"DataIntegrityViolationException: could not execute statement; SQL [n/a]; constraint [contracts.UK_434pfjd6i1d3aq4f2lyl0nlc8]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/contract/\",\n"
                                    + "  \"ok\": false\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> create(
      @Valid @RequestBody Contract contract, BindingResult result, HttpServletRequest httpRequest)
      throws ContractException {

    String uri = httpRequest.getRequestURI();

    Contract created = contractService.createContract(contract);

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), created, uri);

    return ResponseEntity.ok(msg);
  }

  /**
   * Updates a contract.
   *
   * @param contract the contract
   * @param result the validation result
   * @param httpRequest the http request
   * @return the {@code ResponseMessage} containing the contract updated
   */
  @PutMapping("/")
  @PreAuthorize("hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Updates a contract",
      tags = {"Contract"},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description =
                  "Requires a contract json object, fields will be updated based on the primary key <code>id</code> ",
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              externalValue =
                                  "http://localhost:8080/swagger/contract/contract-update-req.json",
                              value =
                                  "{\n"
                                      + "  \"id\": 1,\n"
                                      + "  \"requestId\": 1,\n"
                                      + "  \"walletId\": \"GAyCywe7wYQ49XA92BrDBVvj2CMKeEGMmGjseQR3yFua\",\n"
                                      + "  \"targetAmount\": 1000000,\n"
                                      + "  \"repaymentAmount\": 1500000,\n"
                                      + "  \"status\": \"FF\",\n"
                                      + "  \"createdTimestamp\": \"2022-07-06T20:48:28.4621798\"\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns updated contract in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithContract.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-update-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-10T13:07:25.513882\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 1,\n"
                                    + "    \"requestId\": 1,\n"
                                    + "    \"walletId\": \"GAyCywe7wYQ49XA92BrDBVvj2CMKeEGMmGjseQR3yFua\",\n"
                                    + "    \"targetAmount\": 1000000,\n"
                                    + "    \"repaymentAmount\": 1500000,\n"
                                    + "    \"status\": \"FF\",\n"
                                    + "    \"createdTimestamp\": \"2022-07-06T20:48:28.4621798\",\n"
                                    + "    \"fundings\": null\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/contract/\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to update contract due to validation reasons. Refer to schema of Contract for details.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-update-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-06T21:37:42.5905848\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"ConstraintViolationException: update.contract.targetAmount: targetAmount must be positive, update.contract.targetAmount: targetAmount must be at least 1\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/contract/\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Contract with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-update-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-06T21:42:51.4882915\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Contract with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/contract/\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
      })
  public ResponseEntity<ResponseMessage> update(
      @Valid @RequestBody Contract contract, BindingResult result, HttpServletRequest httpRequest)
      throws EntityNotFoundException {

    String uri = httpRequest.getRequestURI();

    Contract updated = contractService.updateContract(contract);

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), updated, uri);

    return ResponseEntity.ok(msg);
  }

  /**
   * Updates a contract status.
   *
   * @param id the contract id
   * @param status the contract status
   * @param httpRequest the http request
   * @return the {@code ResponseMessage} with updated message
   */
  @PatchMapping("/id/{id}/status/{status}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Updates the status of a contract",
      tags = {"Contract"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns updated status confirmation",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithContract.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-update-status-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-06T21:44:42.1360758\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"Contract with id=1, status=FF updated\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/contract/id/1/status/FF\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to update contract due to validation reasons. Refer to schema of Contract for details.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-update-status-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-06T21:46:48.1368921\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"ConstraintViolationException: updateStatus.id: must be greater than 0\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/contract/id/0/status/FF\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Contract with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-update-status-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-06T21:47:43.4430105\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Contract with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/contract/id/99/status/FF\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
      })
  public ResponseEntity<ResponseMessage> updateStatus(
      @PathVariable("id") @Positive Integer id,
      @PathVariable("status") @NotBlank String status,
      HttpServletRequest httpRequest)
      throws EntityNotFoundException {

    String uri = httpRequest.getRequestURI();

    contractService.updateContractStatus(id, status);

    String reason = "Contract with id=%d, status=%s updated";
    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), String.format(reason, id, status), uri);

    return ResponseEntity.ok(msg);
  }

  /**
   * Removes all contracts.
   *
   * @param request the http request
   * @return the HTTP status OK is successful
   */
  @DeleteMapping("/")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Removes all contracts",
      tags = {"Contract"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "All contracts deleted",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/contract/contract-delete-all-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-07-06T22:42:05.7366319\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"All contracts deleted.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/contract/\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> removeAll(HttpServletRequest request) {

    String uri = request.getRequestURI();

    contractService.deleteAllContracts();

    String reason = "All contracts deleted.";

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), reason, uri);

    return ResponseEntity.ok(msg);
  }

  /**
   * Removes a contract by {@code id}.
   *
   * @param id the contract id
   * @param httpRequest the http request
   * @return HTTP status NOT FOUND if the contract with {@code id} is not found. If the delete is
   *     successful, the HTTP status OK.
   */
  @DeleteMapping("/id/{id}")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Remove contract by id",
      tags = {"Contract"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns deleted contract in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithContract.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/contract/contract-delete-id-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-07-06T22:44:25.286177\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"Contract with id=2 deleted.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/contract/id/2\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Contract with <code>id</code> not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/contract/contract-delete-id-404.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-07-06T22:45:07.3151395\",\n"
                                  + "  \"status\": 404,\n"
                                  + "  \"message\": \"EntityNotFoundException: Contract with id=99 NOT found.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/contract/id/99\",\n"
                                  + "  \"ok\": false\n"
                                  + "}"))
            })
      })
  public ResponseEntity<ResponseMessage> removeById(
      @PathVariable int id, HttpServletRequest httpRequest) throws EntityNotFoundException {

    contractService.deleteContractById(id);

    String reason = "Contract with id=%d deleted.";
    ResponseMessage msg =
        new ResponseMessage(
            HttpStatus.OK.value(), String.format(reason, id), httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets all contracts.
   *
   * @param includeFundings when {@code true}, includes the fundings in the returned contracts
   * @param request the http request
   * @return list of all contracts
   */
  @GetMapping("/")
  @PreAuthorize("hasAuthority('S') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve all contracts",
      tags = {"Contract"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns all contracts in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithContracts.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-get-all-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-07T15:54:46.4142207\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"requestId\": 4,\n"
                                    + "      \"targetAmount\": 1000000,\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"createdTimestamp\": \"2022-07-06T20:48:28.46218\",\n"
                                    + "      \"fundings\": [\n"
                                    + "        {\n"
                                    + "          \"id\": 1,\n"
                                    + "          \"contractId\": 1,\n"
                                    + "          \"profileId\": \"30\",\n"
                                    + "          \"status\": \"F\",\n"
                                    + "          \"fundingAmount\": 9999,\n"
                                    + "          \"disbursedAmount\": 1000,\n"
                                    + "          \"createdTimestamp\": \"2022-07-07T11:37:19.328878\",\n"
                                    + "          \"profile\": null\n"
                                    + "        },\n"
                                    + "        {\n"
                                    + "          \"id\": 2,\n"
                                    + "          \"contractId\": 1,\n"
                                    + "          \"profileId\": \"32\",\n"
                                    + "          \"status\": \"F\",\n"
                                    + "          \"fundingAmount\": 9999,\n"
                                    + "          \"disbursedAmount\": 1000,\n"
                                    + "          \"createdTimestamp\": \"2022-07-07T11:37:19.328878\",\n"
                                    + "          \"profile\": null\n"
                                    + "        }\n"
                                    + "      ]\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"id\": 3,\n"
                                    + "      \"requestId\": 5,\n"
                                    + "      \"targetAmount\": 1000000,\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"createdTimestamp\": \"2022-07-06T20:48:28.46218\",\n"
                                    + "      \"fundings\": [\n"
                                    + "        {\n"
                                    + "          \"id\": 3,\n"
                                    + "          \"contractId\": 3,\n"
                                    + "          \"profileId\": \"56\",\n"
                                    + "          \"status\": \"F\",\n"
                                    + "          \"fundingAmount\": 9999,\n"
                                    + "          \"disbursedAmount\": 1000,\n"
                                    + "          \"createdTimestamp\": \"2022-07-07T11:37:19.328878\",\n"
                                    + "          \"profile\": null\n"
                                    + "        },\n"
                                    + "        {\n"
                                    + "          \"id\": 4,\n"
                                    + "          \"contractId\": 3,\n"
                                    + "          \"profileId\": \"59\",\n"
                                    + "          \"status\": \"F\",\n"
                                    + "          \"fundingAmount\": 9999,\n"
                                    + "          \"disbursedAmount\": 1000,\n"
                                    + "          \"createdTimestamp\": \"2022-07-07T11:37:19.328878\",\n"
                                    + "          \"profile\": null\n"
                                    + "        }\n"
                                    + "      ]\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/contract/\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getAll(
      @RequestParam(required = false, defaultValue = "false") Boolean includeFundings,
      HttpServletRequest request)
      throws GetFundingsException {

    Iterable<Contract> contracts = contractService.getAllContracts();

    if (includeFundings)
      for (Contract contract : contracts)
        contract.setFundings(getFundingsByContractId(contract.getId(), request));

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), contracts, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets fundings by {@code profileId}.
   *
   * @param profileId the profile id
   * @param request the http request
   * @return the list of fundings
   */
  protected List<Funding> getFundingsByProfileId(int profileId, HttpServletRequest request)
      throws GetFundingsException {
    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(getHttpHeaders(request));

    String url = urlBase + String.format(urlFundingGetByProfileId, profileId);

    ResponseMessageWithFundings msg =
        restTemplate
            .exchange(url, HttpMethod.GET, entity, ResponseMessageWithFundings.class)
            .getBody();

    if (msg.isOk()) return Arrays.asList(msg.getData());
    else throw new GetFundingsException(msg.getMessage());
  }

  /**
   * Gets request for funding using {@code requestId}.
   *
   * @param requestId the request id
   * @param request the http request
   * @return the request for funding
   */
  protected RequestForFunding getRequestForFunding(int requestId, HttpServletRequest request)
      throws GetRequestException {
    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(getHttpHeaders(request));

    String url = urlBase + String.format(urlRequestGetId, requestId, true, false);

    ResponseMessageWithRequestForFunding msg =
        restTemplate
            .exchange(url, HttpMethod.GET, entity, ResponseMessageWithRequestForFunding.class)
            .getBody();

    if (msg.isOk()) return msg.getData();
    else throw new GetRequestException(msg.getMessage());
  }

  /**
   * Get contracts that contain funding from investor with {@code profileId}
   *
   * @param profileId the profile id
   * @param includeFundings when {@code true}, includes fundings in the returned contracts
   * @param request the http request
   * @return the list of contracts
   */
  @GetMapping("/profileId/{profileId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve contracts with funding from profileId",
      tags = {"Contract"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a contract in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithContract.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-get-profileId-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-18T16:56:19.2875473\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"requestId\": 3,\n"
                                    + "      \"walletId\": \"GAyCywe7wYQ49XA92BrDBVvj2CMKeEGMmGjseQR3yFua\",\n"
                                    + "      \"targetAmount\": 1200000,\n"
                                    + "      \"repaymentAmount\": 1300000,\n"
                                    + "      \"status\": \"PF\",\n"
                                    + "      \"createdTimestamp\": \"2022-07-18T16:49:17.305777\",\n"
                                    + "      \"fundings\": [\n"
                                    + "        {\n"
                                    + "          \"id\": 1,\n"
                                    + "          \"contractId\": 1,\n"
                                    + "          \"profileId\": 5,\n"
                                    + "          \"status\": \"FIC\",\n"
                                    + "          \"fundingAmount\": 800000,\n"
                                    + "          \"repaymentAmount\": 864000,\n"
                                    + "          \"disbursedAmount\": 0,\n"
                                    + "          \"createdTimestamp\": \"2022-07-18T16:49:32.908019\",\n"
                                    + "          \"profile\": null\n"
                                    + "        }\n"
                                    + "      ],\n"
                                    + "      \"request\": {\n"
                                    + "        \"id\": 3,\n"
                                    + "        \"fromProfileId\": 1,\n"
                                    + "        \"toProfileId\": 0,\n"
                                    + "        \"requestId\": 2,\n"
                                    + "        \"title\": \"Single Patient Dialysis Machine TR-8000\",\n"
                                    + "        \"type\": \"RFF\",\n"
                                    + "        \"status\": \"PF\",\n"
                                    + "        \"description\": \"The single patients dialysis machine can perform prescribed dialysis which adjust the dialysate conductivity in accordance with each patient. It mainly consists of Monitor/alarm part, Dialysate supply/UF control part, Extracorporeal blood circuit part, and Electrical control part.\",\n"
                                    + "        \"cost\": 1200000,\n"
                                    + "        \"repayment\": 1300000,\n"
                                    + "        \"specifications\": \"Supported by the advanced technologies of TORAY, TR-8000 offers comfortable dialysis treatment to patients, easy operation to medical staff and contributes to medical development.\\n\\n- Easy operability by changing the position of external parts\\n- The position of rinse ports, couplers and bicarbonate cartridge was changed and you can operate them without squat.\\n- Casters became larger and you can move it by less power.\\nImprovement of standard functions\\n- The chamber level adjustment has been newly equipped as standard\\n- Self-test time became shorter\\nVarious new options and accessories\\n- BVM, Kt/V indicator, Bicarbonate cartridge, etc.\",\n"
                                    + "        \"createdTimestamp\": \"2022-07-18T08:49:17.282\",\n"
                                    + "        \"fromProfile\": {\n"
                                    + "          \"id\": 1,\n"
                                    + "          \"username\": \"sbipcc\",\n"
                                    + "          \"password\": \"$2a$10$IENpCwlTjYEb4UMBuJL1/elpUP2SOGivoDdPhSoX2rb1JiH0NZE1G\",\n"
                                    + "          \"firstName\": \"SBIP\",\n"
                                    + "          \"lastName\": \"Community Clinic\",\n"
                                    + "          \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "          \"phone\": \"98760001\",\n"
                                    + "          \"userType\": \"U\",\n"
                                    + "          \"registrationDate\": \"2022-07-18T16:45:09.636589\",\n"
                                    + "          \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "        },\n"
                                    + "        \"toProfile\": null,\n"
                                    + "        \"proposal\": null,\n"
                                    + "        \"contract\": null,\n"
                                    + "        \"fromUser\": {\n"
                                    + "          \"id\": 1,\n"
                                    + "          \"username\": \"sbipcc\",\n"
                                    + "          \"password\": \"$2a$10$IENpCwlTjYEb4UMBuJL1/elpUP2SOGivoDdPhSoX2rb1JiH0NZE1G\",\n"
                                    + "          \"firstName\": \"SBIP\",\n"
                                    + "          \"lastName\": \"Community Clinic\",\n"
                                    + "          \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "          \"phone\": \"98760001\",\n"
                                    + "          \"userType\": \"U\",\n"
                                    + "          \"registrationDate\": \"2022-07-18T16:45:09.636589\",\n"
                                    + "          \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "        },\n"
                                    + "        \"open\": false,\n"
                                    + "        \"accepted\": false,\n"
                                    + "        \"closed\": false,\n"
                                    + "        \"rfp\": false,\n"
                                    + "        \"pro\": false,\n"
                                    + "        \"rff\": true,\n"
                                    + "        \"rejected\": false\n"
                                    + "      },\n"
                                    + "      \"outstandingAmount\": 400000,\n"
                                    + "      \"raisedAmount\": 800000,\n"
                                    + "      \"yield\": 8\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/contract/profileId/5\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByProfileId(
      @PathVariable Integer profileId,
      @RequestParam(required = false, defaultValue = "false") Boolean includeFundings,
      @RequestParam(required = false, defaultValue = "false") Boolean includeRFF,
      HttpServletRequest request)
      throws ContractException, EntityNotFoundException {

    List<Integer> contractIds =
        getFundingsByProfileId(profileId, request).stream()
            .map(Funding::getContractId)
            .collect(Collectors.toList())
            .stream()
            .distinct() // Remove duplicate contract ids
            .collect(Collectors.toList());

    ArrayList<Contract> contracts = new ArrayList<>();
    ResponseMessage msg;

    for (Integer id : contractIds) {
      msg = getById(id, includeFundings, request).getBody();

      if (msg.isOk()) {

        Contract contract = (Contract) msg.getData();

        if (includeRFF) contract.setRequest(getRequestForFunding(contract.getRequestId(), request));

        contracts.add(contract);
      } else throw new GetContractException(msg.getMessage());
    }

    msg = new ResponseMessage(HttpStatus.OK.value(), contracts, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets fundings by {@code contractId}.
   *
   * @param contractId the contract id
   * @param request the http request
   * @return the list of fundings
   */
  protected List<Funding> getFundingsByContractId(int contractId, HttpServletRequest request)
      throws GetFundingsException {
    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(getHttpHeaders(request));

    String url = urlBase + String.format(urlFundingGetByContractId, contractId);

    ResponseMessageWithFundings msg =
        restTemplate
            .exchange(url, HttpMethod.GET, entity, ResponseMessageWithFundings.class)
            .getBody();

    if (msg.isOk()) return Arrays.asList(msg.getData());
    else throw new GetFundingsException(msg.getMessage());
  }

  /**
   * Get contract by id.
   *
   * @param id the contract id
   * @param httpRequest the http request
   * @return the contract with the id if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/id/{id}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve contract by id",
      tags = {"Contract"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a contract in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithContract.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-get-id-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-07T15:52:25.8354309\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 3,\n"
                                    + "    \"requestId\": 5,\n"
                                    + "    \"targetAmount\": 1000000,\n"
                                    + "    \"status\": \"O\",\n"
                                    + "    \"createdTimestamp\": \"2022-07-06T20:48:28.46218\",\n"
                                    + "    \"fundings\": [\n"
                                    + "      {\n"
                                    + "        \"id\": 3,\n"
                                    + "        \"contractId\": 3,\n"
                                    + "        \"profileId\": \"56\",\n"
                                    + "        \"status\": \"F\",\n"
                                    + "        \"fundingAmount\": 9999,\n"
                                    + "        \"disbursedAmount\": 1000,\n"
                                    + "        \"createdTimestamp\": \"2022-07-07T11:37:19.328878\",\n"
                                    + "        \"profile\": null\n"
                                    + "      },\n"
                                    + "      {\n"
                                    + "        \"id\": 4,\n"
                                    + "        \"contractId\": 3,\n"
                                    + "        \"profileId\": \"59\",\n"
                                    + "        \"status\": \"F\",\n"
                                    + "        \"fundingAmount\": 9999,\n"
                                    + "        \"disbursedAmount\": 1000,\n"
                                    + "        \"createdTimestamp\": \"2022-07-07T11:37:19.328878\",\n"
                                    + "        \"profile\": null\n"
                                    + "      }\n"
                                    + "    ]\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/contract/id/3\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Contract with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-get-id-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-07T15:21:46.7150464\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Contract with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/contract/id/99\",\n"
                                    + "  \"ok\": false\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> getById(
      @PathVariable Integer id,
      @RequestParam(required = false, defaultValue = "false") Boolean includeFundings,
      HttpServletRequest httpRequest)
      throws GetFundingsException, EntityNotFoundException {

    Contract contract = contractService.getContractById(id);

    if (includeFundings)
      contract.setFundings(getFundingsByContractId(contract.getId(), httpRequest));

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), contract, httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Get contract by {@code requestId}.
   *
   * @param requestId the request for funding id
   * @param httpRequest the http request
   * @return the contract with the request id if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/requestId/{requestId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve contract by id",
      tags = {"Contract"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a contract in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithContract.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-get-requestId-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-13T11:30:23.2326168\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 1,\n"
                                    + "    \"requestId\": 3,\n"
                                    + "    \"walletId\": \"GAyCywe7wYQ49XA92BrDBVvj2CMKeEGMmGjseQR3yFua\",\n"
                                    + "    \"targetAmount\": 1000000,\n"
                                    + "    \"repaymentAmount\": 1300000,\n"
                                    + "    \"status\": \"O\",\n"
                                    + "    \"createdTimestamp\": \"2022-07-06T20:48:28.4621798\",\n"
                                    + "    \"fundings\": [],\n"
                                    + "    \"outstandingAmount\": 1000000,\n"
                                    + "    \"raisedAmount\": 0,\n"
                                    + "    \"yield\": 30\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/contract/\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Contract with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-get-requestId-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-13T11:38:12.2857055\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Contract with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/contract/requestId/99\",\n"
                                    + "  \"ok\": false\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> getByRequestId(
      @PathVariable Integer requestId,
      @RequestParam(required = false, defaultValue = "false") Boolean includeFundings,
      HttpServletRequest httpRequest)
      throws ContractException {

    Contract contract = contractService.getContractByRequestId(requestId);

    if (includeFundings)
      contract.setFundings(getFundingsByContractId(contract.getId(), httpRequest));

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), contract, httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets contract yield by id.
   *
   * @param id the contract id
   * @param httpRequest the http request
   * @return the contract yield if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/yield/id/{id}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve contract yield by id",
      tags = {"Contract"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the contract yield in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-get-yield-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-10T14:27:44.3214999\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": 20,\n"
                                    + "  \"path\": \"/api/v1/contract/yield/id/1\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Contract with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-get-yield-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-10T14:26:14.8435359\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Contract with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/contract/yield/id/99\",\n"
                                    + "  \"ok\": false\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> getYield(
      @PathVariable Integer id, HttpServletRequest httpRequest) throws EntityNotFoundException {

    Contract contract = contractService.getContractById(id);

    ResponseMessage msg =
        new ResponseMessage(
            HttpStatus.OK.value(), contract.getYield(), httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets contract raised amount by id.
   *
   * @param id the contract id
   * @param httpRequest the http request
   * @return the contract raised amount if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/raised/id/{id}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve contract raised amount by id",
      tags = {"Contract"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the contract raised amount in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-get-raisedAmount-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-10T14:48:26.8539402\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": 0,\n"
                                    + "  \"path\": \"/api/v1/contract/raised/id/1\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Contract with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-get-raisedAmount-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-10T14:47:37.6876198\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Contract with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/contract/raised/id/99\",\n"
                                    + "  \"ok\": false\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> getRaisedAmount(
      @PathVariable Integer id, HttpServletRequest httpRequest)
      throws GetFundingsException, EntityNotFoundException {

    Contract contract = contractService.getContractById(id);

    contract.setFundings(getFundingsByContractId(id, httpRequest));

    ResponseMessage msg =
        new ResponseMessage(
            HttpStatus.OK.value(), contract.getRaisedAmount(), httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets contract outstanding amount by id.
   *
   * @param id the contract id
   * @param httpRequest the http request
   * @return the contract outstanding amount if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/outstanding/id/{id}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve contract outstanding amount by id",
      tags = {"Contract"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the contract outstanding amount in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-get-outstandingAmount-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-10T14:29:15.4535241\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": 1000000,\n"
                                    + "  \"path\": \"/api/v1/contract/outstanding/id/1\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Contract with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-get-outstandingAmount-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-10T14:30:34.4149559\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Contract with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/contract/outstanding/id/99\",\n"
                                    + "  \"ok\": false\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> getOutstandingAmount(
      @PathVariable Integer id, HttpServletRequest httpRequest)
      throws GetFundingsException, EntityNotFoundException {

    Contract contract = contractService.getContractById(id);

    contract.setFundings(getFundingsByContractId(id, httpRequest));

    ResponseMessage msg =
        new ResponseMessage(
            HttpStatus.OK.value(), contract.getOutstandingAmount(), httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets funding percentage of a contract by id.
   *
   * @param id the contract id
   * @param amount the funding amount
   * @param httpRequest the http request
   * @return the funding percentage if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/fundingPercentage/id/{id}/amount/{amount}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve funding percentage of a contract by id",
      tags = {"Contract"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the funding percentage in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-get-fundingPercentage-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-10T15:02:55.4218576\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": 50,\n"
                                    + "  \"path\": \"/api/v1/contract/fundingPercentage/id/1/amount/500000\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Contract with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-get-fundingPercentage-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-10T15:04:10.349685\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Contract with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/contract/fundingPercentage/id/99/amount/500000\",\n"
                                    + "  \"ok\": false\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> getFundingPercentage(
      @PathVariable Integer id, @PathVariable @Positive Long amount, HttpServletRequest httpRequest)
      throws EntityNotFoundException {

    Contract contract = contractService.getContractById(id);

    ResponseMessage msg =
        new ResponseMessage(
            HttpStatus.OK.value(),
            Contract.getFundingPercentage(contract, amount),
            httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Get funding returns of a contract by id.
   *
   * @param id the contract id
   * @param amount the funding amount
   * @param httpRequest the http request
   * @return the funding returns if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/fundingReturns/id/{id}/amount/{amount}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve funding returns of a contract by id",
      tags = {"Contract"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the funding returns in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-get-fundingReturns-200.json",
                            value = ""))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Contract with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-get-fundingReturns-404.json",
                            value = "")))
      })
  public ResponseEntity<ResponseMessage> getFundingReturns(
      @PathVariable Integer id, @PathVariable @Positive Long amount, HttpServletRequest httpRequest)
      throws EntityNotFoundException {

    Contract contract = contractService.getContractById(id);

    ResponseMessage msg =
        new ResponseMessage(
            HttpStatus.OK.value(), contract.getFundingReturns(amount), httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Creates a funding.
   *
   * @param funding the funding to be created
   * @param httpRequest the http request
   * @throws CreateFundingException any exception thrown when creating the funding
   * @throws JsonProcessingException any exception thrown when writing the funding as a json
   */
  protected Funding createFunding(Funding funding, HttpServletRequest httpRequest)
      throws CreateFundingException, JsonProcessingException {
    String jsonFunding = objectMapper.writeValueAsString(funding);

    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(jsonFunding, getHttpHeaders(httpRequest));

    ResponseMessageWithFunding msg =
        restTemplate
            .exchange(
                urlBase + urlFundingCreate,
                HttpMethod.POST,
                entity,
                ResponseMessageWithFunding.class)
            .getBody();

    if (msg.isOk()) return msg.getData();
    else throw new CreateFundingException(msg.getMessage());
  }

  /**
   * Updates the status of a request for funding.
   *
   * @param requestId the id of the request for funding
   * @param status the status to be updated
   * @param httpRequest the http request
   * @throws UpdateRequestException any exception thrown when updating the status
   */
  protected void updateRequestForFundingStatus(
      int requestId, String status, HttpServletRequest httpRequest) throws UpdateRequestException {

    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(getHttpHeaders(httpRequest));

    String url = urlBase + String.format(urlRequestUpdateStatus, requestId, status);

    ResponseMessage msg = restTemplate.patchForObject(url, entity, ResponseMessage.class);

    if (!msg.isOk()) throw new UpdateRequestException(msg.getMessage());
  }

  /**
   * Funds a contract.
   *
   * @param contractId the contract id
   * @param profileId the profile id of the investor funding the contract
   * @param fundingAmount the amount of funding from investor
   * @param httpRequest the http request
   * @return the {@code ResponseMessage} containing the funded contract
   */
  @PostMapping("/fund/{contractId}")
  @PreAuthorize("hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Funds a contract",
      tags = {"Contract"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns funded contract in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithContract.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-fund-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-13T11:31:16.8508065\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 1,\n"
                                    + "    \"contractId\": 1,\n"
                                    + "    \"profileId\": 4,\n"
                                    + "    \"status\": \"FIC\",\n"
                                    + "    \"fundingAmount\": 8000,\n"
                                    + "    \"repaymentAmount\": 10400,\n"
                                    + "    \"disbursedAmount\": 0,\n"
                                    + "    \"createdTimestamp\": \"2022-07-13T11:31:16.7396355\",\n"
                                    + "    \"profile\": null\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/contract/fund/1\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description = "Unable to fund contract due to validation reasons.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-fund-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-13T11:32:34.4282158\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"FundingAmountException: Funding amount of 800000000000000, exceeds outstanding amount of 1000000\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/contract/fund/1\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Contract with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-fund-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-12T23:56:40.4659262\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Contract with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/contract/fund/99\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
      })
  public ResponseEntity<ResponseMessage> fund(
      @PathVariable Integer contractId,
      @RequestParam Integer profileId,
      @RequestParam Long fundingAmount,
      HttpServletRequest httpRequest)
      throws ContractException, EntityNotFoundException, JsonProcessingException {

    ResponseMessage msg = getById(contractId, true, httpRequest).getBody();

    if (msg.isOk()) {
      Contract contract = (Contract) msg.getData();

      Funding funding = contractService.fundContract(contract, profileId, fundingAmount);

      funding = createFunding(funding, httpRequest);

      updateRequestForFundingStatus(contract.getRequestId(), contract.getStatus(), httpRequest);

      return ResponseEntity.ok(
          new ResponseMessage(HttpStatus.OK.value(), funding, httpRequest.getRequestURI()));
    } else throw new ContractException(msg.getMessage());
  }

  /**
   * Transfer funds between 2 wallet addresses.
   *
   * @param fromWalletId the from wallet address
   * @param toWalletId the to wallet address
   * @param amount the amount to transfer
   * @throws TransferFundsException any exception thrown when transferring funds
   */
  protected void transferFunds(
      String fromWalletId, String toWalletId, Long amount, HttpServletRequest httpRequest)
      throws TransferFundsException {

    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(getHttpHeaders(httpRequest));

    ResponseMessage msg =
        restTemplate
            .exchange(
                urlBase + String.format(urlLedgerTransfer, fromWalletId, toWalletId, amount),
                HttpMethod.POST,
                entity,
                ResponseMessage.class)
            .getBody();

    if (!msg.isOk()) throw new TransferFundsException(msg.getMessage());
  }

  /**
   * Gets the wallet id of a user profile by {@code profileId}.
   *
   * @param profileId the profile id
   * @param request the http request
   * @return the wallet id of the user profile
   * @throws GetUserProfileException any exception thrown when retrieving the user profile
   */
  protected String getWalletId(int profileId, HttpServletRequest request)
      throws GetWalletIdException {
    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(getHttpHeaders(request));

    String url = urlBase + String.format(urlProfileGetById, profileId);

    ResponseMessageWithUserProfile msg =
        restTemplate
            .exchange(url, HttpMethod.GET, entity, ResponseMessageWithUserProfile.class)
            .getBody();

    if (msg.isOk()) return msg.getData().getWalletId();
    else throw new GetWalletIdException(msg.getMessage());
  }

  /**
   * Disburse fundings by a list of {@code fundingIds}.
   *
   * @param fundingIds the list of funding ids
   * @param request the http request
   * @throws GetUserProfileException any exception thrown when retrieving the user profile
   */
  protected void disburseFundings(List<Integer> fundingIds, HttpServletRequest request)
      throws DisburseContractException {
    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(getHttpHeaders(request));

    // Format list of funding ids
    String strIds = fundingIds.toString();
    strIds = strIds.substring(1, strIds.length() - 1);
    strIds.replace(" ", "");
    log.info("### " + strIds);

    String url = urlBase + String.format(urlFundingDisburse, strIds);

    ResponseMessage msg =
        restTemplate.exchange(url, HttpMethod.POST, entity, ResponseMessage.class).getBody();

    if (!msg.isOk()) throw new DisburseContractException(msg.getMessage());
  }

  /**
   * Disburse funds in a contract.
   *
   * @param contractId the contract id
   * @param httpRequest the http request
   * @return the {@code ResponseMessage} containing the funded contract
   */
  @PostMapping("/disburse/{contractId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Disburse funds in a contract",
      tags = {"Contract"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns disbursed contract in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithContract.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-disburse-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-19T15:37:37.1660591\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 1,\n"
                                    + "    \"requestId\": 3,\n"
                                    + "    \"walletId\": \"GAyCywe7wYQ49XA92BrDBVvj2CMKeEGMmGjseQR3yFua\",\n"
                                    + "    \"targetAmount\": 1000,\n"
                                    + "    \"repaymentAmount\": 1300,\n"
                                    + "    \"status\": \"FD\",\n"
                                    + "    \"createdTimestamp\": \"2022-07-19T15:31:35.812973\",\n"
                                    + "    \"fundings\": [\n"
                                    + "      {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"contractId\": 1,\n"
                                    + "        \"profileId\": 4,\n"
                                    + "        \"status\": \"FD\",\n"
                                    + "        \"fundingAmount\": 400,\n"
                                    + "        \"repaymentAmount\": 520,\n"
                                    + "        \"disbursedAmount\": 520,\n"
                                    + "        \"createdTimestamp\": \"2022-07-19T15:34:41.25571\",\n"
                                    + "        \"profile\": null\n"
                                    + "      },\n"
                                    + "      {\n"
                                    + "        \"id\": 2,\n"
                                    + "        \"contractId\": 1,\n"
                                    + "        \"profileId\": 5,\n"
                                    + "        \"status\": \"FD\",\n"
                                    + "        \"fundingAmount\": 300,\n"
                                    + "        \"repaymentAmount\": 390,\n"
                                    + "        \"disbursedAmount\": 390,\n"
                                    + "        \"createdTimestamp\": \"2022-07-19T15:35:24.14161\",\n"
                                    + "        \"profile\": null\n"
                                    + "      },\n"
                                    + "      {\n"
                                    + "        \"id\": 3,\n"
                                    + "        \"contractId\": 1,\n"
                                    + "        \"profileId\": 4,\n"
                                    + "        \"status\": \"FD\",\n"
                                    + "        \"fundingAmount\": 300,\n"
                                    + "        \"repaymentAmount\": 390,\n"
                                    + "        \"disbursedAmount\": 390,\n"
                                    + "        \"createdTimestamp\": \"2022-07-19T15:35:38.390228\",\n"
                                    + "        \"profile\": null\n"
                                    + "      }\n"
                                    + "    ],\n"
                                    + "    \"request\": null,\n"
                                    + "    \"outstandingAmount\": 0,\n"
                                    + "    \"yield\": 30,\n"
                                    + "    \"raisedAmount\": 1000\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/contract/disburse/1\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Contract with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/contract/contract-disburse-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-19T15:39:11.2754146\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Contract with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/contract/disburse/99\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
      })
  public ResponseEntity<ResponseMessage> disburse(
      @PathVariable Integer contractId, HttpServletRequest httpRequest)
      throws ContractException, EntityNotFoundException {

    String uri = httpRequest.getRequestURI();

    ResponseMessage msg = getById(contractId, true, httpRequest).getBody();

    if (msg.isOk()) {
      Contract contract = (Contract) msg.getData();

      if (contract.getStatus().equals(Contract.STATUS_FUNDS_REPAID)) {

        List<Funding> fundings = contract.getFundings();

        // Transfer funds to individual investors
        for (Funding funding : fundings)
          transferFunds(
              contract.getWalletId(),
              getWalletId(funding.getProfileId(), httpRequest),
              funding.getRepaymentAmount(),
              httpRequest);

        // Disburse all fundings and update statuses
        List<Integer> ids = fundings.stream().map(Funding::getId).collect(Collectors.toList());
        disburseFundings(ids, httpRequest);

        // Update contract status
        msg = updateStatus(contractId, Contract.STATUS_FUNDS_DISBURSED, httpRequest).getBody();
        if (!msg.isOk()) throw new UpdateContractException(msg.getMessage());

        contract.disburse();
        msg = new ResponseMessage(HttpStatus.OK.value(), contract, uri);

        return ResponseEntity.ok(msg);

      } else throw new DisburseContractException(contract.getStatus());
    } else throw new GetContractException(msg.getMessage());
  }
}
