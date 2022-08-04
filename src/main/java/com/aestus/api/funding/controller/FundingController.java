package com.aestus.api.funding.controller;

import com.aestus.api.common.exception.EntityNotFoundException;
import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.funding.model.Funding;
import com.aestus.api.funding.model.swagger.ResponseMessageWithFunding;
import com.aestus.api.funding.model.swagger.ResponseMessageWithFundings;
import com.aestus.api.funding.service.FundingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Provides the funding controller functionality. */
@Slf4j
@RestController
@RequestMapping("api/v1/funding")
@Validated
public class FundingController {

  @Autowired private FundingService fundingService;

  /**
   * Pinging the controller.
   *
   * @param request the http request
   * @return the ping returned message in the {@code ResponseEntity} container object
   */
  @GetMapping("/ping")
  @Operation(
      summary = "Ping test",
      tags = {"Funding"},
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
                              "http://localhost:8080/swagger/funding/funding-ping-200.json",
                          value = ""))
            })
      })
  public ResponseEntity<ResponseMessage> ping(HttpServletRequest request) {

    String baseUrl = request.getRequestURI();

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), "ping pong", baseUrl);

    return new ResponseEntity<ResponseMessage>(msg, HttpStatus.OK);
  }

  /**
   * Creates a funding.
   *
   * @param funding the funding
   * @param result the validation result
   * @param httpRequest the http request
   * @return the {@code ResponseMessage} containing the contract created with the auto-generated
   *     {@code id}
   */
  @PostMapping("/")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Creates a new funding",
      tags = {"Funding"},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description =
                  "Requires a funding json object, the <code>id</code> field (if specified) will be ignored",
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = Funding.class),
                      examples =
                          @ExampleObject(
                              externalValue =
                                  "http://localhost:8080/swagger/funding/funding-create-req.json",
                              value =
                                  "{\n"
                                      + "  \"contractId\": 23,\n"
                                      + "  \"profileId\": 30,\n"
                                      + "  \"status\": \"F\",\n"
                                      + "  \"fundingAmount\": 9999,\n"
                                      + "  \"disbursedAmount\": 1000,\n"
                                      + "  \"createdTimestamp\": \"2022-07-07T11:37:19.3288777\"\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Returns funding (with the auto-generated <code>id</code>) in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithFunding.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/funding/funding-create-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-07-07T11:50:17.2996416\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"\",\n"
                                  + "  \"data\": {\n"
                                  + "    \"id\": 1,\n"
                                  + "    \"contractId\": 23,\n"
                                  + "    \"profileId\": \"30\",\n"
                                  + "    \"status\": \"F\",\n"
                                  + "    \"fundingAmount\": 9999,\n"
                                  + "    \"disbursedAmount\": 1000,\n"
                                  + "    \"createdTimestamp\": \"2022-07-07T11:37:19.3288777\",\n"
                                  + "    \"profile\": null\n"
                                  + "  },\n"
                                  + "  \"path\": \"/api/v1/funding/\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to create funding due to validation reasons.  Refer to schema of Funding for details.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/funding/funding-create-400-contractId.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-07-07T11:54:34.2377228\",\n"
                                  + "  \"status\": 400,\n"
                                  + "  \"message\": \"ConstraintViolationException: create.funding.contractId: contractId must not be Null, create.funding.contractId: contractId must not be Null\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/funding/\",\n"
                                  + "  \"ok\": false\n"
                                  + "}"))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> create(
      @Valid @RequestBody Funding funding, BindingResult result, HttpServletRequest httpRequest) {

    String uri = httpRequest.getRequestURI();

    Funding created = fundingService.createFunding(funding);

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), created, uri);

    return ResponseEntity.ok(msg);
  }

  /**
   * Updates a funding.
   *
   * @param funding the funding
   * @param result the validation result
   * @param httpRequest the http request
   * @return the {@code ResponseMessage} containing the funding updated
   */
  @PutMapping("/")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Updates a funding",
      tags = {"Funding"},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description =
                  "Requires a funding json object, fields will be updated based on the primary key <code>id</code> ",
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              externalValue =
                                  "http://localhost:8080/swagger/funding/funding-update-req.json",
                              value =
                                  "{\n"
                                      + "  \"id\": 1,\n"
                                      + "  \"contractId\": \"23\",\n"
                                      + "  \"profileId\": \"30\",\n"
                                      + "  \"status\": \"F\",\n"
                                      + "  \"fundingAmount\": 9999,\n"
                                      + "  \"disbursedAmount\": 8877,\n"
                                      + "  \"createdTimestamp\": \"2022-07-07T11:37:19.3288777\"\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns updated funding in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithFunding.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/funding/funding-update-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-07T12:01:24.3544105\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 1,\n"
                                    + "    \"contractId\": 23,\n"
                                    + "    \"profileId\": \"30\",\n"
                                    + "    \"status\": \"F\",\n"
                                    + "    \"fundingAmount\": 9999,\n"
                                    + "    \"disbursedAmount\": 8877,\n"
                                    + "    \"createdTimestamp\": \"2022-07-07T11:37:19.3288777\",\n"
                                    + "    \"profile\": null\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/funding/\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to update funding due to validation reasons. Refer to schema of Funding for details.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/funding/funding-update-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-07T12:03:33.8729801\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"ConstraintViolationException: update.funding.disbursedAmount: disbursedAmount must be at least 1, update.funding.disbursedAmount: disbursedAmount must be positive\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/funding/\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Funding with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/funding/funding-update-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-07T12:04:32.1067538\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Funding with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/funding/\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
      })
  public ResponseEntity<ResponseMessage> update(
      @Valid @RequestBody Funding funding, BindingResult result, HttpServletRequest httpRequest)
      throws EntityNotFoundException {

    String uri = httpRequest.getRequestURI();

    Funding updated = fundingService.updateFunding(funding);

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), updated, uri);

    return ResponseEntity.ok(msg);
  }

  /**
   * Updates a funding status.
   *
   * @param id the funding id
   * @param status the funding status
   * @param httpRequest the http request
   * @return the {@code ResponseMessage} with updated message
   */
  @PatchMapping("/id/{id}/status/{status}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Updates the status of a funding",
      tags = {"Funding"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns updated status confirmation",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithFunding.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/funding/funding-update-status-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-07T12:11:33.302206\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"Funding with id=1, status=D updated\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/funding/id/1/status/D\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to update funding due to validation reasons. Refer to schema of Funding for details.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/funding/funding-update-status-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-07T12:14:02.6097553\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"ConstraintViolationException: updateStatus.id: must be greater than 0\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/funding/id/0/status/D\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Funding with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/funding/funding-update-status-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-07T12:14:44.4971605\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Funding with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/funding/id/99/status/D\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
      })
  public ResponseEntity<ResponseMessage> updateStatus(
      @PathVariable("id") @Positive Integer id,
      @PathVariable("status") @NotBlank String status,
      HttpServletRequest httpRequest)
      throws EntityNotFoundException {

    String uri = httpRequest.getRequestURI();

    fundingService.updateFundingStatus(id, status);

    String reason = "Funding with id=%d, status=%s updated";
    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), String.format(reason, id, status), uri);

    return ResponseEntity.ok(msg);
  }

  /**
   * Disburse a list of fundings specified by {@code ids}
   *
   * @param ids the list of funding ids to disburse
   * @param httpRequest the http request
   * @return the {@code ResponseMessage} with disbursed message
   */
  @PostMapping("/disburse")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Disburse a list of fundings",
      tags = {"Funding"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns disbursed confirmation",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithFunding.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/funding/funding-disburseFundings-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-19T15:06:39.1370387\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"Fundings disbursed, ids=[1, 2]\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/funding/disburse/\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Funding with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/funding/funding-disburseFundings-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-19T15:24:06.5630407\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Funding with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/funding/disburse/\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
      })
  public ResponseEntity<ResponseMessage> disburse(
      @RequestParam List<Integer> ids, HttpServletRequest httpRequest)
      throws EntityNotFoundException {

    String uri = httpRequest.getRequestURI();

    fundingService.disburseFundings(ids);

    String reason = "Fundings disbursed, ids=%s";
    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), String.format(reason, ids), uri);

    return ResponseEntity.ok(msg);
  }

  /**
   * Removes all fundings.
   *
   * @param request the http request
   * @return the HTTP status OK is successful
   */
  @DeleteMapping("/")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Removes all fundings",
      tags = {"Funding"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "All fundings deleted",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/funding/funding-delete-all-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-07-07T12:24:13.5720265\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"All fundings deleted.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/funding/\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> removeAll(HttpServletRequest request) {

    String uri = request.getRequestURI();

    fundingService.deleteAllFundings();

    String reason = "All fundings deleted.";

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), reason, uri);

    return ResponseEntity.ok(msg);
  }

  /**
   * Removes a funding by {@code id}.
   *
   * @param id the funding id
   * @param httpRequest the http request
   * @return HTTP status NOT FOUND if the funding with {@code id} is not found. If the delete is
   *     successful, the HTTP status OK.
   */
  @DeleteMapping("/id/{id}")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Remove funding by id",
      tags = {"Funding"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns deleted funding in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithFunding.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/funding/funding-delete-id-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-07-07T12:21:23.32848\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"Funding with id=2 deleted.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/funding/id/2\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Funding with <code>id</code> not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/funding/funding-delete-id-404.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-07-07T12:22:15.594643\",\n"
                                  + "  \"status\": 404,\n"
                                  + "  \"message\": \"EntityNotFoundException: Funding with id=99 NOT found.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/funding/id/99\",\n"
                                  + "  \"ok\": false\n"
                                  + "}"))
            })
      })
  public ResponseEntity<ResponseMessage> removeById(
      @PathVariable int id, HttpServletRequest httpRequest) throws EntityNotFoundException {

    fundingService.deleteFundingById(id);

    String reason = "Funding with id=%d deleted.";
    ResponseMessage msg =
        new ResponseMessage(
            HttpStatus.OK.value(), String.format(reason, id), httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets all fundings.
   *
   * @param request the http request
   * @return list of all fundings
   */
  @GetMapping("/")
  @PreAuthorize("hasAuthority('U') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve all fundings",
      tags = {"Funding"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns all fundings in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithFundings.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/funding/funding-get-all-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-07T14:40:52.2393521\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"contractId\": 23,\n"
                                    + "      \"profileId\": \"30\",\n"
                                    + "      \"status\": \"F\",\n"
                                    + "      \"fundingAmount\": 9999,\n"
                                    + "      \"disbursedAmount\": 1000,\n"
                                    + "      \"createdTimestamp\": \"2022-07-07T11:37:19.328878\",\n"
                                    + "      \"profile\": null\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"id\": 2,\n"
                                    + "      \"contractId\": 23,\n"
                                    + "      \"profileId\": \"30\",\n"
                                    + "      \"status\": \"F\",\n"
                                    + "      \"fundingAmount\": 9999,\n"
                                    + "      \"disbursedAmount\": 1000,\n"
                                    + "      \"createdTimestamp\": \"2022-07-07T11:37:19.328878\",\n"
                                    + "      \"profile\": null\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"id\": 3,\n"
                                    + "      \"contractId\": 23,\n"
                                    + "      \"profileId\": \"30\",\n"
                                    + "      \"status\": \"F\",\n"
                                    + "      \"fundingAmount\": 9999,\n"
                                    + "      \"disbursedAmount\": 1000,\n"
                                    + "      \"createdTimestamp\": \"2022-07-07T11:37:19.328878\",\n"
                                    + "      \"profile\": null\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/funding/\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getAll(HttpServletRequest request) {

    Iterable<Funding> fundings = fundingService.getAllFundings();

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), fundings, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets fundings by {@code contractId}.
   *
   * @param contractId the contract id
   * @param request the http request
   * @return list of fundings belonging to contract indicated by {@code contractId}
   */
  @GetMapping("/contractId/{contractId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve fundings by contract id",
      tags = {"Funding"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns fundings in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithFundings.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/funding/funding-get-contractId-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-07T15:11:22.6693793\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 4,\n"
                                    + "      \"contractId\": 30,\n"
                                    + "      \"profileId\": \"30\",\n"
                                    + "      \"status\": \"F\",\n"
                                    + "      \"fundingAmount\": 9999,\n"
                                    + "      \"disbursedAmount\": 1000,\n"
                                    + "      \"createdTimestamp\": \"2022-07-07T11:37:19.328878\",\n"
                                    + "      \"profile\": null\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"id\": 5,\n"
                                    + "      \"contractId\": 30,\n"
                                    + "      \"profileId\": \"30\",\n"
                                    + "      \"status\": \"F\",\n"
                                    + "      \"fundingAmount\": 9999,\n"
                                    + "      \"disbursedAmount\": 1000,\n"
                                    + "      \"createdTimestamp\": \"2022-07-07T11:37:19.328878\",\n"
                                    + "      \"profile\": null\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/funding/contractId/30\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByContractId(
      @PathVariable Integer contractId, HttpServletRequest request) {

    Iterable<Funding> fundings = fundingService.getFundingsByContractId(contractId);

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), fundings, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets fundings by {@code profileId}.
   *
   * @param profileId the profile id of the investor
   * @param request the http request
   * @return list of fundings belonging to an investor indicated by {@code profileId}
   */
  @GetMapping("/profileId/{profileId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve fundings by profile id",
      tags = {"Funding"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns fundings in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithFundings.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/funding/funding-get-profileId-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-14T11:56:45.5289653\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"contractId\": 1,\n"
                                    + "      \"profileId\": 4,\n"
                                    + "      \"status\": \"FIC\",\n"
                                    + "      \"fundingAmount\": 800000,\n"
                                    + "      \"repaymentAmount\": 920000,\n"
                                    + "      \"disbursedAmount\": 0,\n"
                                    + "      \"createdTimestamp\": \"2022-07-14T11:56:39.462488\",\n"
                                    + "      \"profile\": null\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/funding/profileId/4\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByProfileId(
      @PathVariable Integer profileId, HttpServletRequest request) {

    Iterable<Funding> fundings = fundingService.getFundingsByProfileId(profileId);

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), fundings, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Get funding by id.
   *
   * @param id the funding id
   * @param httpRequest the http request
   * @return the funding with the id if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/id/{id}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve funding by id",
      tags = {"Contract"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a funding in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithFunding.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/funding/funding-get-id-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-07T15:12:41.620264\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 3,\n"
                                    + "    \"contractId\": 23,\n"
                                    + "    \"profileId\": \"30\",\n"
                                    + "    \"status\": \"F\",\n"
                                    + "    \"fundingAmount\": 9999,\n"
                                    + "    \"disbursedAmount\": 1000,\n"
                                    + "    \"createdTimestamp\": \"2022-07-07T11:37:19.328878\",\n"
                                    + "    \"profile\": null\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/funding/id/3\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Funding with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/funding/funding-get-id-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-07T15:14:09.349932\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Funding with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/funding/id/99\",\n"
                                    + "  \"ok\": false\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> getById(
      @PathVariable Integer id, HttpServletRequest httpRequest) throws EntityNotFoundException {

    Funding funding = fundingService.getFundingById(id);

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), funding, httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }
}
