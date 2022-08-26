package com.aestus.api.request.controller;

import com.aestus.api.common.exception.EntityNotFoundException;
import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.contract.exception.GetProposalException;
import com.aestus.api.contract.model.Contract;
import com.aestus.api.contract.model.swagger.ResponseMessageWithContract;
import com.aestus.api.product.model.Product;
import com.aestus.api.product.model.swagger.ResponseMessageWithProduct;
import com.aestus.api.profile.model.UserProfile;
import com.aestus.api.profile.model.swagger.ResponseMessageWithUserProfile;
import com.aestus.api.request.exception.CreateContractException;
import com.aestus.api.request.exception.GetProductException;
import com.aestus.api.request.exception.GetUserProfileException;
import com.aestus.api.request.exception.RequestException;
import com.aestus.api.request.model.*;
import com.aestus.api.request.model.swagger.*;
import com.aestus.api.request.service.RequestService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.collect.Iterables;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

/** Provides the request controller functionality. */
@Slf4j
@RestController
@RequestMapping("api/v1/request")
@Validated
public class RequestController {

  @Autowired private RequestService requestService;
  @Autowired ObjectMapper objectMapper;
  @Autowired RestTemplate restTemplate;

  @Value("${com.aestus.base.url}")
  private final String urlBase = null;

  @Value("${com.aestus.product.get.id.url}")
  private final String urlProductGetById = null;

  @Value("${com.aestus.profile.get.id.url}")
  private final String urlProfileGetById = null;

  @Value("${com.aestus.contract.create.url}")
  private final String urlContractCreate = null;

  @Value("${com.aestus.contract.get.requestId.url}")
  private final String urlContractGetByRequestId = null;

  /**
   * Pinging the controller.
   *
   * @param request the request
   * @return the ping returned message in the {@code ResponseEntity} container object
   */
  @GetMapping("/ping")
  @Operation(
      summary = "Ping test",
      tags = {"Request"},
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
                              "http://localhost:8080/swagger/request/request-ping-200.json",
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
   * Gets all requests.
   *
   * @param request the http request
   * @return list of all requests
   */
  @GetMapping("/")
  @PreAuthorize("hasAuthority('S') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve all requests",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns all requests in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequests.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-all-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-14T14:00:11.1671169\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"fromProfileId\": 8,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": 999,\n"
                                    + "      \"title\": \"TX2touch-90 POWER cobot\",\n"
                                    + "      \"type\": \"RFP\",\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "      \"cost\": 2000000,\n"
                                    + "      \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "      \"createdTimestamp\": \"2022-05-30T16:51:43.929622\"\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"id\": 2,\n"
                                    + "      \"fromProfileId\": 1,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": null,\n"
                                    + "      \"title\": \"Epson VT6L All-in-One 6-Axis Robot\",\n"
                                    + "      \"type\": \"RFA\",\n"
                                    + "      \"status\": \"C\",\n"
                                    + "      \"description\": \"Features Slimline design perfect for factories with limited floor space and compact wrist pitch that enables robot easy access to hard-to-reach areas. Ideal for load/ unload, packaging or parts assembly applications. Cleanroom (ISO4) and Protected (IP67) models available.\",\n"
                                    + "      \"cost\": 2000000,\n"
                                    + "      \"specifications\": \"VT6L offers a reach up to 900 mm and a payload up to 6 kg. A feature-packed performer, it includes a built-in controller, plus simplified cabling with a hollow end-of-arm design – all at a remarkably low cost, in a compact, SlimLine structure. The VT6L offers 110 V and 220 V power and installs in minutes.\",\n"
                                    + "      \"createdTimestamp\": \"2022-06-14T11:51:14.458968\"\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"id\": 3,\n"
                                    + "      \"fromProfileId\": 8,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": 1,\n"
                                    + "      \"title\": \"TX2touch-90 POWER cobot\",\n"
                                    + "      \"type\": \"RFA\",\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "      \"cost\": 3000000,\n"
                                    + "      \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "      \"createdTimestamp\": \"2022-06-14T11:51:14.458968\"\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"id\": 4,\n"
                                    + "      \"fromProfileId\": 8,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": null,\n"
                                    + "      \"title\": \"TX2touch-90 POWER cobot\",\n"
                                    + "      \"type\": \"RFP\",\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "      \"cost\": 1000000,\n"
                                    + "      \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "      \"createdTimestamp\": \"2022-05-30T16:51:43.929622\"\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/request/\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getAll(HttpServletRequest request) {

    Iterable<Request> requests = requestService.getAllRequests();

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), requests, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets requests by {@code fromProfileId} and {@code type}.
   *
   * @param profileId the profile id the request originated form
   * @param type the request type
   * @param request the http request
   * @return list of requests
   */
  @GetMapping("/fromProfileId/{profileId}/type/{type}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve requests by from profile id and type",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns requests in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequests.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-fromProfileId-type-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-05T15:14:09.0859423\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"fromProfileId\": 1,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": null,\n"
                                    + "      \"title\": \"Epson T3-B All-in-One SCARA Robot\",\n"
                                    + "      \"type\": \"RFP\",\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"description\": \"The ideal alternative to slide-based solutions, All-in-One design includes power for end-of-arm tooling, 4 built-in axes in one compact design. Perfect for pick and place, simple assembly, material handing and dispensing.\",\n"
                                    + "      \"cost\": 1000000,\n"
                                    + "      \"repayment\": null,\n"
                                    + "      \"specifications\": \"Designed to seamlessly fit in a variety of workspaces, this all-in-one solution features a built-in controller, power for end-of-arm tooling and 110 V or 220 V power—virtually eliminating any space-constraint issues. Plus, it offers a 400 mm reach and a payload of up to 3 kg to easily handle a variety of tasks.\",\n"
                                    + "      \"createdTimestamp\": \"2022-07-05T15:11:28.377618\",\n"
                                    + "      \"fromProfile\": null,\n"
                                    + "      \"toProfile\": null,\n"
                                    + "      \"open\": true,\n"
                                    + "      \"accepted\": false,\n"
                                    + "      \"closed\": false,\n"
                                    + "      \"rejected\": false,\n"
                                    + "      \"rfp\": true,\n"
                                    + "      \"pro\": false,\n"
                                    + "      \"approved\": false,\n"
                                    + "      \"rff\": false\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"id\": 2,\n"
                                    + "      \"fromProfileId\": 1,\n"
                                    + "      \"toProfileId\": 3,\n"
                                    + "      \"requestId\": null,\n"
                                    + "      \"title\": \"Epson VT6L All-in-One 6-Axis Robot\",\n"
                                    + "      \"type\": \"RFP\",\n"
                                    + "      \"status\": \"C\",\n"
                                    + "      \"description\": \"Features Slimline design perfect for factories with limited floor space and compact wrist pitch that enables robot easy access to hard-to-reach areas. Ideal for load/ unload, packaging or parts assembly applications. Cleanroom (ISO4) and Protected (IP67) models available.\",\n"
                                    + "      \"cost\": 2000000,\n"
                                    + "      \"repayment\": 25000000,\n"
                                    + "      \"specifications\": \"VT6L offers a reach up to 900 mm and a payload up to 6 kg. A feature-packed performer, it includes a built-in controller, plus simplified cabling with a hollow end-of-arm design – all at a remarkably low cost, in a compact, SlimLine structure. The VT6L offers 110 V and 220 V power and installs in minutes.\",\n"
                                    + "      \"createdTimestamp\": \"2022-07-05T15:11:28.377618\",\n"
                                    + "      \"fromProfile\": null,\n"
                                    + "      \"toProfile\": null,\n"
                                    + "      \"open\": false,\n"
                                    + "      \"accepted\": false,\n"
                                    + "      \"closed\": true,\n"
                                    + "      \"rejected\": false,\n"
                                    + "      \"rfp\": true,\n"
                                    + "      \"pro\": false,\n"
                                    + "      \"approved\": false,\n"
                                    + "      \"rff\": false\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/request/fromProfileId/1/type/RFP\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByFromProfileIdAndType(
      @PathVariable Integer profileId, @PathVariable String type, HttpServletRequest request) {

    Iterable<Request> requests = requestService.getRequestsByFromProfileIdAndType(profileId, type);

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), requests, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets requests by from profile id.
   *
   * @param profileId the profile id the request originated form
   * @param request the http request
   * @return list of requests
   */
  @GetMapping("/fromProfileId/{profileId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve requests by from profile id",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns requests in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequests.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-fromProfileId-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-05T15:12:33.5621274\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"fromProfileId\": 1,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": null,\n"
                                    + "      \"title\": \"Epson T3-B All-in-One SCARA Robot\",\n"
                                    + "      \"type\": \"RFP\",\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"description\": \"The ideal alternative to slide-based solutions, All-in-One design includes power for end-of-arm tooling, 4 built-in axes in one compact design. Perfect for pick and place, simple assembly, material handing and dispensing.\",\n"
                                    + "      \"cost\": 1000000,\n"
                                    + "      \"repayment\": null,\n"
                                    + "      \"specifications\": \"Designed to seamlessly fit in a variety of workspaces, this all-in-one solution features a built-in controller, power for end-of-arm tooling and 110 V or 220 V power—virtually eliminating any space-constraint issues. Plus, it offers a 400 mm reach and a payload of up to 3 kg to easily handle a variety of tasks.\",\n"
                                    + "      \"createdTimestamp\": \"2022-07-05T15:11:28.377618\",\n"
                                    + "      \"fromProfile\": null,\n"
                                    + "      \"toProfile\": null,\n"
                                    + "      \"open\": true,\n"
                                    + "      \"accepted\": false,\n"
                                    + "      \"closed\": false,\n"
                                    + "      \"rejected\": false,\n"
                                    + "      \"rfp\": true,\n"
                                    + "      \"pro\": false,\n"
                                    + "      \"approved\": false,\n"
                                    + "      \"rff\": false\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"id\": 2,\n"
                                    + "      \"fromProfileId\": 1,\n"
                                    + "      \"toProfileId\": 3,\n"
                                    + "      \"requestId\": null,\n"
                                    + "      \"title\": \"Epson VT6L All-in-One 6-Axis Robot\",\n"
                                    + "      \"type\": \"RFP\",\n"
                                    + "      \"status\": \"C\",\n"
                                    + "      \"description\": \"Features Slimline design perfect for factories with limited floor space and compact wrist pitch that enables robot easy access to hard-to-reach areas. Ideal for load/ unload, packaging or parts assembly applications. Cleanroom (ISO4) and Protected (IP67) models available.\",\n"
                                    + "      \"cost\": 2000000,\n"
                                    + "      \"repayment\": 25000000,\n"
                                    + "      \"specifications\": \"VT6L offers a reach up to 900 mm and a payload up to 6 kg. A feature-packed performer, it includes a built-in controller, plus simplified cabling with a hollow end-of-arm design – all at a remarkably low cost, in a compact, SlimLine structure. The VT6L offers 110 V and 220 V power and installs in minutes.\",\n"
                                    + "      \"createdTimestamp\": \"2022-07-05T15:11:28.377618\",\n"
                                    + "      \"fromProfile\": null,\n"
                                    + "      \"toProfile\": null,\n"
                                    + "      \"open\": false,\n"
                                    + "      \"accepted\": false,\n"
                                    + "      \"closed\": true,\n"
                                    + "      \"rejected\": false,\n"
                                    + "      \"rfp\": true,\n"
                                    + "      \"pro\": false,\n"
                                    + "      \"approved\": false,\n"
                                    + "      \"rff\": false\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/request/fromProfileId/1\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByFromProfileId(
      @PathVariable Integer profileId, HttpServletRequest request) {

    Iterable<Request> requests = requestService.getRequestsByFromProfileId(profileId);

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), requests, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets requests by to profile id.
   *
   * @param profileId the profile id the request is intended for
   * @param request the http request
   * @return list of requests
   */
  @GetMapping("/toProfileId/{profileId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve requests by to profile id",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns requests in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequests.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-toProfileId-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-14T14:24:53.6811596\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 2,\n"
                                    + "      \"fromProfileId\": 1,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": null,\n"
                                    + "      \"title\": \"Epson VT6L All-in-One 6-Axis Robot\",\n"
                                    + "      \"type\": \"RFA\",\n"
                                    + "      \"status\": \"C\",\n"
                                    + "      \"description\": \"Features Slimline design perfect for factories with limited floor space and compact wrist pitch that enables robot easy access to hard-to-reach areas. Ideal for load/ unload, packaging or parts assembly applications. Cleanroom (ISO4) and Protected (IP67) models available.\",\n"
                                    + "      \"cost\": 2000000,\n"
                                    + "      \"specifications\": \"VT6L offers a reach up to 900 mm and a payload up to 6 kg. A feature-packed performer, it includes a built-in controller, plus simplified cabling with a hollow end-of-arm design – all at a remarkably low cost, in a compact, SlimLine structure. The VT6L offers 110 V and 220 V power and installs in minutes.\",\n"
                                    + "      \"createdTimestamp\": \"2022-06-14T11:51:14.458968\"\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"fromProfileId\": 8,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": 999,\n"
                                    + "      \"title\": \"TX2touch-90 POWER cobot\",\n"
                                    + "      \"type\": \"RFP\",\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "      \"cost\": 2000000,\n"
                                    + "      \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "      \"createdTimestamp\": \"2022-05-30T16:51:43.929622\"\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"id\": 3,\n"
                                    + "      \"fromProfileId\": 8,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": 1,\n"
                                    + "      \"title\": \"TX2touch-90 POWER cobot\",\n"
                                    + "      \"type\": \"RFA\",\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "      \"cost\": 3000000,\n"
                                    + "      \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "      \"createdTimestamp\": \"2022-06-14T11:51:14.458968\"\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"id\": 4,\n"
                                    + "      \"fromProfileId\": 8,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": null,\n"
                                    + "      \"title\": \"TX2touch-90 POWER cobot\",\n"
                                    + "      \"type\": \"RFP\",\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "      \"cost\": 1000000,\n"
                                    + "      \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "      \"createdTimestamp\": \"2022-05-30T16:51:43.929622\"\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/request/toProfileId/2\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByToProfileId(
      @PathVariable Integer profileId, HttpServletRequest request) {

    Iterable<Request> requests = requestService.getRequestsByToProfileId(profileId);

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), requests, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets requests by to profile id and type.
   *
   * @param profileId the profile id the request is intended for
   * @param type the request type
   * @param request the http request
   * @return list of requests
   */
  @GetMapping("/toProfileId/{profileId}/type/{type}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve requests by to profile id and type",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns requests in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequests.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-toProfileId-type-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-15T15:13:53.5366634\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 2,\n"
                                    + "      \"fromProfileId\": 1,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": null,\n"
                                    + "      \"title\": \"Epson VT6L All-in-One 6-Axis Robot\",\n"
                                    + "      \"type\": \"RFA\",\n"
                                    + "      \"status\": \"C\",\n"
                                    + "      \"description\": \"Features Slimline design perfect for factories with limited floor space and compact wrist pitch that enables robot easy access to hard-to-reach areas. Ideal for load/ unload, packaging or parts assembly applications. Cleanroom (ISO4) and Protected (IP67) models available.\",\n"
                                    + "      \"cost\": 2000000,\n"
                                    + "      \"repayment\": 25000000,\n"
                                    + "      \"specifications\": \"VT6L offers a reach up to 900 mm and a payload up to 6 kg. A feature-packed performer, it includes a built-in controller, plus simplified cabling with a hollow end-of-arm design – all at a remarkably low cost, in a compact, SlimLine structure. The VT6L offers 110 V and 220 V power and installs in minutes.\",\n"
                                    + "      \"createdTimestamp\": \"2022-06-15T15:12:27.377656\"\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"id\": 3,\n"
                                    + "      \"fromProfileId\": 8,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": 1,\n"
                                    + "      \"title\": \"TX2touch-90 POWER cobot\",\n"
                                    + "      \"type\": \"RFA\",\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "      \"cost\": 3000000,\n"
                                    + "      \"repayment\": 31000000,\n"
                                    + "      \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "      \"createdTimestamp\": \"2022-06-15T15:12:27.377656\"\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/request/toProfileId/2/type/RFA\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByToProfileIdAndType(
      @PathVariable Integer profileId, @PathVariable String type, HttpServletRequest request) {

    Iterable<Request> requests = requestService.getRequestsByToProfileIdAndType(profileId, type);

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), requests, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets requests by profile id.
   *
   * @param profileId the profile id
   * @param request the http request
   * @return list of requests
   */
  @GetMapping("/profileId/{profileId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve requests by to profile id",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns requests in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequests.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-profileId-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-14T14:12:59.1098063\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 2,\n"
                                    + "      \"fromProfileId\": 1,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": null,\n"
                                    + "      \"title\": \"Epson VT6L All-in-One 6-Axis Robot\",\n"
                                    + "      \"type\": \"RFA\",\n"
                                    + "      \"status\": \"C\",\n"
                                    + "      \"description\": \"Features Slimline design perfect for factories with limited floor space and compact wrist pitch that enables robot easy access to hard-to-reach areas. Ideal for load/ unload, packaging or parts assembly applications. Cleanroom (ISO4) and Protected (IP67) models available.\",\n"
                                    + "      \"cost\": 2000000,\n"
                                    + "      \"specifications\": \"VT6L offers a reach up to 900 mm and a payload up to 6 kg. A feature-packed performer, it includes a built-in controller, plus simplified cabling with a hollow end-of-arm design – all at a remarkably low cost, in a compact, SlimLine structure. The VT6L offers 110 V and 220 V power and installs in minutes.\",\n"
                                    + "      \"createdTimestamp\": \"2022-06-14T11:51:14.458968\"\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/request/profileId/1\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByProfileId(
      @PathVariable Integer profileId, HttpServletRequest request) {

    Iterable<Request> requests = requestService.getRequestsByProfileId(profileId);

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), requests, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets requests by request id.
   *
   * @param requestId the request id
   * @param request the http request
   * @return list of requests
   */
  @GetMapping("/requestId/{requestId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve requests by request id",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns requests in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequests.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-requestId-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-21T16:12:21.0396995\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"fromProfileId\": 1,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": 5,\n"
                                    + "      \"title\": \"Epson T3-B All-in-One SCARA Robot\",\n"
                                    + "      \"type\": \"RFS\",\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"description\": \"The ideal alternative to slide-based solutions, All-in-One design includes power for end-of-arm tooling, 4 built-in axes in one compact design. Perfect for pick and place, simple assembly, material handing and dispensing.\",\n"
                                    + "      \"cost\": 1000000,\n"
                                    + "      \"repayment\": null,\n"
                                    + "      \"specifications\": \"Designed to seamlessly fit in a variety of workspaces, this all-in-one solution features a built-in controller, power for end-of-arm tooling and 110 V or 220 V power—virtually eliminating any space-constraint issues. Plus, it offers a 400 mm reach and a payload of up to 3 kg to easily handle a variety of tasks.\",\n"
                                    + "      \"createdTimestamp\": \"2022-06-21T16:11:50.939175\",\n"
                                    + "      \"open\": true,\n"
                                    + "      \"accepted\": false,\n"
                                    + "      \"rfa\": false,\n"
                                    + "      \"rfp\": false,\n"
                                    + "      \"approved\": false,\n"
                                    + "      \"proposal\": false,\n"
                                    + "      \"rejected\": false,\n"
                                    + "      \"rff\": false\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"id\": 2,\n"
                                    + "      \"fromProfileId\": 1,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": 5,\n"
                                    + "      \"title\": \"Epson VT6L All-in-One 6-Axis Robot\",\n"
                                    + "      \"type\": \"RFA\",\n"
                                    + "      \"status\": \"C\",\n"
                                    + "      \"description\": \"Features Slimline design perfect for factories with limited floor space and compact wrist pitch that enables robot easy access to hard-to-reach areas. Ideal for load/ unload, packaging or parts assembly applications. Cleanroom (ISO4) and Protected (IP67) models available.\",\n"
                                    + "      \"cost\": 2000000,\n"
                                    + "      \"repayment\": 25000000,\n"
                                    + "      \"specifications\": \"VT6L offers a reach up to 900 mm and a payload up to 6 kg. A feature-packed performer, it includes a built-in controller, plus simplified cabling with a hollow end-of-arm design – all at a remarkably low cost, in a compact, SlimLine structure. The VT6L offers 110 V and 220 V power and installs in minutes.\",\n"
                                    + "      \"createdTimestamp\": \"2022-06-21T16:11:50.939175\",\n"
                                    + "      \"open\": false,\n"
                                    + "      \"accepted\": false,\n"
                                    + "      \"rfa\": true,\n"
                                    + "      \"rfp\": false,\n"
                                    + "      \"approved\": false,\n"
                                    + "      \"proposal\": true,\n"
                                    + "      \"rejected\": false,\n"
                                    + "      \"rff\": false\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/request/requestId/5\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByReceiptId(
      @PathVariable Integer requestId, HttpServletRequest request) {

    Iterable<Request> requests = requestService.getRequestsByRequestId(requestId);

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), requests, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets requests by type.
   *
   * @param type the request type
   * @param request the http request
   * @return a list of requests
   */
  @GetMapping("/type/{type}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve requests by to profile id",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns requests in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequests.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-type-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-14T14:22:11.3787844\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 2,\n"
                                    + "      \"fromProfileId\": 1,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": null,\n"
                                    + "      \"title\": \"Epson VT6L All-in-One 6-Axis Robot\",\n"
                                    + "      \"type\": \"RFA\",\n"
                                    + "      \"status\": \"C\",\n"
                                    + "      \"description\": \"Features Slimline design perfect for factories with limited floor space and compact wrist pitch that enables robot easy access to hard-to-reach areas. Ideal for load/ unload, packaging or parts assembly applications. Cleanroom (ISO4) and Protected (IP67) models available.\",\n"
                                    + "      \"cost\": 2000000,\n"
                                    + "      \"specifications\": \"VT6L offers a reach up to 900 mm and a payload up to 6 kg. A feature-packed performer, it includes a built-in controller, plus simplified cabling with a hollow end-of-arm design – all at a remarkably low cost, in a compact, SlimLine structure. The VT6L offers 110 V and 220 V power and installs in minutes.\",\n"
                                    + "      \"createdTimestamp\": \"2022-06-14T11:51:14.458968\"\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"id\": 3,\n"
                                    + "      \"fromProfileId\": 8,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": 1,\n"
                                    + "      \"title\": \"TX2touch-90 POWER cobot\",\n"
                                    + "      \"type\": \"RFA\",\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "      \"cost\": 3000000,\n"
                                    + "      \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "      \"createdTimestamp\": \"2022-06-14T11:51:14.458968\"\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/request/type/RFA\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByType(
      @PathVariable String type, HttpServletRequest request) {

    Iterable<Request> requests = requestService.getRequestsByType(type);

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), requests, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets request for proposals by {@code fromProfileId}.
   *
   * @param fromProfileId the id of the profile the requests are from
   * @param includeProposals when {@code true}, includes the proposals from the solution provider in
   *     the returned request for proposals
   * @param includeProvider when {@code true}, includes the profiles of the solution providers in
   *     the returned proposals
   * @param includeProduct when {@code true}, includes the product which the RFP is based on
   * @param request the http request
   * @return a list of request for proposals
   */
  @GetMapping("/rfp/from/{fromProfileId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve request for proposals by from profile id",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Returns the list of request for proposals in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequestForProposals.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-requestForProposalsFrom-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-08-02T11:48:48.1913106\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"fromProfileId\": 1,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": 1,\n"
                                    + "      \"title\": \"TX2touch-90 POWER cobot\",\n"
                                    + "      \"type\": \"RFP\",\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "      \"cost\": 1000000,\n"
                                    + "      \"repayment\": null,\n"
                                    + "      \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "      \"createdTimestamp\": \"2022-05-30T16:51:43.929622\",\n"
                                    + "      \"fromProfile\": null,\n"
                                    + "      \"toProfile\": null,\n"
                                    + "      \"product\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"profileId\": 2,\n"
                                    + "        \"name\": \"Single Patient Dialysis Machine TR-8000\",\n"
                                    + "        \"type\": \"Dialysis\",\n"
                                    + "        \"description\": \"The single patients dialysis machine can perform prescribed dialysis which adjust the dialysate conductivity in accordance with each patient. It mainly consists of Monitor/alarm part, Dialysate supply/UF control part, Extracorporeal blood circuit part, and Electrical control part.\",\n"
                                    + "        \"imageUrl\": \"http://localhost:8080/images/dialysis.jpg\",\n"
                                    + "        \"price\": 1200000,\n"
                                    + "        \"specifications\": \"Supported by the advanced technologies of TORAY, TR-8000 offers comfortable dialysis treatment to patients, easy operation to medical staff and contributes to medical development.\\n\\n- Easy operability by changing the position of external parts\\n- The position of rinse ports, couplers and bicarbonate cartridge was changed and you can operate them without squat.\\n- Casters became larger and you can move it by less power.\\nImprovement of standard functions\\n- The chamber level adjustment has been newly equipped as standard\\n- Self-test time became shorter\\nVarious new options and accessories\\n- BVM, Kt/V indicator, Bicarbonate cartridge, etc.\",\n"
                                    + "        \"createdTimestamp\": \"2022-08-02T11:26:35.243258\"\n"
                                    + "      },\n"
                                    + "      \"proposals\": [\n"
                                    + "        {\n"
                                    + "          \"id\": 1,\n"
                                    + "          \"fromProfileId\": 1,\n"
                                    + "          \"toProfileId\": 2,\n"
                                    + "          \"requestId\": 1,\n"
                                    + "          \"title\": \"TX2touch-90 POWER cobot\",\n"
                                    + "          \"type\": \"PRO\",\n"
                                    + "          \"status\": \"O\",\n"
                                    + "          \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "          \"cost\": 1000000,\n"
                                    + "          \"repayment\": null,\n"
                                    + "          \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "          \"createdTimestamp\": \"2022-05-30T16:51:43.929622\",\n"
                                    + "          \"fromProfile\": {\n"
                                    + "            \"id\": 1,\n"
                                    + "            \"username\": \"sbipcc\",\n"
                                    + "            \"password\": \"$2a$10$dZrpM5CqU.LWHOlgNnzcc.zBPGSoTOmZN5IkDyr4zLjTN66WH5uTm\",\n"
                                    + "            \"firstName\": \"SBIP\",\n"
                                    + "            \"lastName\": \"Community Clinic\",\n"
                                    + "            \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "            \"phone\": \"98760001\",\n"
                                    + "            \"userType\": \"U\",\n"
                                    + "            \"registrationDate\": \"2022-08-02T11:26:34.670542\",\n"
                                    + "            \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "          },\n"
                                    + "          \"toProfile\": null,\n"
                                    + "          \"provider\": {\n"
                                    + "            \"id\": 1,\n"
                                    + "            \"username\": \"sbipcc\",\n"
                                    + "            \"password\": \"$2a$10$dZrpM5CqU.LWHOlgNnzcc.zBPGSoTOmZN5IkDyr4zLjTN66WH5uTm\",\n"
                                    + "            \"firstName\": \"SBIP\",\n"
                                    + "            \"lastName\": \"Community Clinic\",\n"
                                    + "            \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "            \"phone\": \"98760001\",\n"
                                    + "            \"userType\": \"U\",\n"
                                    + "            \"registrationDate\": \"2022-08-02T11:26:34.670542\",\n"
                                    + "            \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "          },\n"
                                    + "          \"user\": null,\n"
                                    + "          \"open\": true,\n"
                                    + "          \"accepted\": false,\n"
                                    + "          \"rfp\": false,\n"
                                    + "          \"rff\": false,\n"
                                    + "          \"closed\": false,\n"
                                    + "          \"rejected\": false,\n"
                                    + "          \"pro\": true\n"
                                    + "        }\n"
                                    + "      ],\n"
                                    + "      \"productId\": 1,\n"
                                    + "      \"fromUser\": null,\n"
                                    + "      \"toProvider\": null,\n"
                                    + "      \"open\": true,\n"
                                    + "      \"accepted\": false,\n"
                                    + "      \"rfp\": true,\n"
                                    + "      \"rff\": false,\n"
                                    + "      \"closed\": false,\n"
                                    + "      \"rejected\": false,\n"
                                    + "      \"pro\": false\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/request/rfp/from/1\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getRequestForProposalsFrom(
      @PathVariable Integer fromProfileId,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProposals,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProvider,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProduct,
      HttpServletRequest request)
      throws GetUserProfileException, GetProductException {

    Iterable<RequestForProposal> rfps = requestService.getRequestForProposalsFrom(fromProfileId);

    for (RequestForProposal rfp : rfps) {
      // Include proposals to the request for proposals
      if (includeProposals) {
        List<Request> proposals =
            (List<Request>)
                getProposalsFor(rfp.getId(), includeProvider, Boolean.FALSE, request)
                    .getBody()
                    .getData();
        rfp.setProposals(proposals);
      }

      // Include product which the request for proposal is based on
      if (includeProduct) rfp.setProduct(getProduct(rfp.getProductId(), request));
    }

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), rfps, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets request for proposals by {@code toProfileId}.
   *
   * @param toProfileId the id of the profile the requests are meant for
   * @param includeProposals when {@code true}, includes the proposals from the solution provider in
   *     the returned request for proposals
   * @param providerId the profile id of the solution provider whose proposal to include
   * @param includeUser when {@code true}, includes the user profile that created the request for
   *     proposal
   * @param includeProvider when {@code true}, includes the profiles of the solution providers in
   *     the returned proposals
   * @param includeProduct when {@code true}, includes the product which the RFP is based on
   * @param request the http request
   * @return a list of request for proposals
   */
  @GetMapping("/rfp/to/{toProfileId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve request for proposals by to profile id",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Returns the list of request for proposals in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequestForProposals.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-requestForProposalsTo-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-08-02T11:51:36.4984699\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"fromProfileId\": 1,\n"
                                    + "      \"toProfileId\": 2,\n"
                                    + "      \"requestId\": 1,\n"
                                    + "      \"title\": \"TX2touch-90 POWER cobot\",\n"
                                    + "      \"type\": \"RFP\",\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "      \"cost\": 1000000,\n"
                                    + "      \"repayment\": null,\n"
                                    + "      \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "      \"createdTimestamp\": \"2022-05-30T16:51:43.929622\",\n"
                                    + "      \"fromProfile\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"username\": \"sbipcc\",\n"
                                    + "        \"password\": \"$2a$10$dZrpM5CqU.LWHOlgNnzcc.zBPGSoTOmZN5IkDyr4zLjTN66WH5uTm\",\n"
                                    + "        \"firstName\": \"SBIP\",\n"
                                    + "        \"lastName\": \"Community Clinic\",\n"
                                    + "        \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "        \"phone\": \"98760001\",\n"
                                    + "        \"userType\": \"U\",\n"
                                    + "        \"registrationDate\": \"2022-08-02T11:26:34.670542\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"toProfile\": null,\n"
                                    + "      \"product\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"profileId\": 2,\n"
                                    + "        \"name\": \"Single Patient Dialysis Machine TR-8000\",\n"
                                    + "        \"type\": \"Dialysis\",\n"
                                    + "        \"description\": \"The single patients dialysis machine can perform prescribed dialysis which adjust the dialysate conductivity in accordance with each patient. It mainly consists of Monitor/alarm part, Dialysate supply/UF control part, Extracorporeal blood circuit part, and Electrical control part.\",\n"
                                    + "        \"imageUrl\": \"http://localhost:8080/images/dialysis.jpg\",\n"
                                    + "        \"price\": 1200000,\n"
                                    + "        \"specifications\": \"Supported by the advanced technologies of TORAY, TR-8000 offers comfortable dialysis treatment to patients, easy operation to medical staff and contributes to medical development.\\n\\n- Easy operability by changing the position of external parts\\n- The position of rinse ports, couplers and bicarbonate cartridge was changed and you can operate them without squat.\\n- Casters became larger and you can move it by less power.\\nImprovement of standard functions\\n- The chamber level adjustment has been newly equipped as standard\\n- Self-test time became shorter\\nVarious new options and accessories\\n- BVM, Kt/V indicator, Bicarbonate cartridge, etc.\",\n"
                                    + "        \"createdTimestamp\": \"2022-08-02T11:26:35.243258\"\n"
                                    + "      },\n"
                                    + "      \"proposals\": [\n"
                                    + "        {\n"
                                    + "          \"id\": 1,\n"
                                    + "          \"fromProfileId\": 1,\n"
                                    + "          \"toProfileId\": 2,\n"
                                    + "          \"requestId\": 1,\n"
                                    + "          \"title\": \"TX2touch-90 POWER cobot\",\n"
                                    + "          \"type\": \"PRO\",\n"
                                    + "          \"status\": \"O\",\n"
                                    + "          \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "          \"cost\": 1000000,\n"
                                    + "          \"repayment\": null,\n"
                                    + "          \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "          \"createdTimestamp\": \"2022-05-30T16:51:43.929622\",\n"
                                    + "          \"fromProfile\": {\n"
                                    + "            \"id\": 1,\n"
                                    + "            \"username\": \"sbipcc\",\n"
                                    + "            \"password\": \"$2a$10$dZrpM5CqU.LWHOlgNnzcc.zBPGSoTOmZN5IkDyr4zLjTN66WH5uTm\",\n"
                                    + "            \"firstName\": \"SBIP\",\n"
                                    + "            \"lastName\": \"Community Clinic\",\n"
                                    + "            \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "            \"phone\": \"98760001\",\n"
                                    + "            \"userType\": \"U\",\n"
                                    + "            \"registrationDate\": \"2022-08-02T11:26:34.670542\",\n"
                                    + "            \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "          },\n"
                                    + "          \"toProfile\": null,\n"
                                    + "          \"provider\": {\n"
                                    + "            \"id\": 1,\n"
                                    + "            \"username\": \"sbipcc\",\n"
                                    + "            \"password\": \"$2a$10$dZrpM5CqU.LWHOlgNnzcc.zBPGSoTOmZN5IkDyr4zLjTN66WH5uTm\",\n"
                                    + "            \"firstName\": \"SBIP\",\n"
                                    + "            \"lastName\": \"Community Clinic\",\n"
                                    + "            \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "            \"phone\": \"98760001\",\n"
                                    + "            \"userType\": \"U\",\n"
                                    + "            \"registrationDate\": \"2022-08-02T11:26:34.670542\",\n"
                                    + "            \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "          },\n"
                                    + "          \"user\": null,\n"
                                    + "          \"open\": true,\n"
                                    + "          \"accepted\": false,\n"
                                    + "          \"rfp\": false,\n"
                                    + "          \"rff\": false,\n"
                                    + "          \"closed\": false,\n"
                                    + "          \"rejected\": false,\n"
                                    + "          \"pro\": true\n"
                                    + "        }\n"
                                    + "      ],\n"
                                    + "      \"productId\": 1,\n"
                                    + "      \"fromUser\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"username\": \"sbipcc\",\n"
                                    + "        \"password\": \"$2a$10$dZrpM5CqU.LWHOlgNnzcc.zBPGSoTOmZN5IkDyr4zLjTN66WH5uTm\",\n"
                                    + "        \"firstName\": \"SBIP\",\n"
                                    + "        \"lastName\": \"Community Clinic\",\n"
                                    + "        \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "        \"phone\": \"98760001\",\n"
                                    + "        \"userType\": \"U\",\n"
                                    + "        \"registrationDate\": \"2022-08-02T11:26:34.670542\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"toProvider\": null,\n"
                                    + "      \"open\": true,\n"
                                    + "      \"accepted\": false,\n"
                                    + "      \"rfp\": true,\n"
                                    + "      \"rff\": false,\n"
                                    + "      \"closed\": false,\n"
                                    + "      \"rejected\": false,\n"
                                    + "      \"pro\": false\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/request/rfp/to/2\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getRequestForProposalsTo(
      @PathVariable Integer toProfileId,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProposals,
      @RequestParam(required = false, defaultValue = "0") Integer providerId,
      @RequestParam(required = false, defaultValue = "false") Boolean includeUser,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProvider,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProduct,
      HttpServletRequest request)
      throws GetUserProfileException, GetProductException {

    Iterable<RequestForProposal> rfps = requestService.getRequestForProposalsTo(toProfileId);

    for (RequestForProposal rfp : rfps) {
      // Include proposals for each request for proposal
      if (includeProposals) {
        List<Request> proposals =
            (List<Request>)
                getProposalsFor(rfp.getId(), includeProvider, Boolean.FALSE, request)
                    .getBody()
                    .getData();

        if (providerId > 0) {
          Request reqProposal =
              proposals.stream()
                  .filter(proposal -> providerId.equals(proposal.getFromProfileId()))
                  .findAny()
                  .orElse(null);

          if (reqProposal != null) proposals = Arrays.asList(reqProposal);
        }

        rfp.setProposals(proposals);
      }

      // Include the from user profile for each request for proposal
      if (includeUser) rfp.setFromUser(getUserProfile(rfp.getFromProfileId(), request));

      // Include the product which the request for proposal is based on
      if (includeProduct) rfp.setProduct(getProduct(rfp.getProductId(), request));
    }
    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), rfps, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets proposals by {@code requestId}.
   *
   * @param rfpId the id of the request for proposal
   * @param includeProvider when {@code true}, includes the profiles of the solution provider in the
   *     returned proposals
   * @param includeUser when {@code true}, includes the profile of the user in the returned
   *     proposals
   * @param httpRequest the http request
   * @return a list of proposals
   */
  @GetMapping("/pro/for/{rfpId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve proposals of a request for proposal",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the list of proposals in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithProposals.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-proposalsFrom-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-05T14:41:53.0180752\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 3,\n"
                                    + "      \"fromProfileId\": 2,\n"
                                    + "      \"toProfileId\": 1,\n"
                                    + "      \"requestId\": 1,\n"
                                    + "      \"title\": \"TX2touch-90 POWER Cobot\",\n"
                                    + "      \"type\": \"PRO\",\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "      \"cost\": 3000000,\n"
                                    + "      \"repayment\": null,\n"
                                    + "      \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "      \"createdTimestamp\": \"2022-07-05T14:39:54.487971\",\n"
                                    + "      \"fromProfile\": {\n"
                                    + "        \"id\": 2,\n"
                                    + "        \"username\": \"batman\",\n"
                                    + "        \"password\": \"$2a$10$5bclPpdulMo0YncfEbMJjOlHru9F8CfKQxRpD.Hs.HZBarnRw8dsi\",\n"
                                    + "        \"firstName\": \"bruce\",\n"
                                    + "        \"lastName\": \"wane\",\n"
                                    + "        \"email\": \"batman@gmail.com\",\n"
                                    + "        \"phone\": \"98760002\",\n"
                                    + "        \"userType\": \"S\",\n"
                                    + "        \"registrationDate\": \"2022-07-05T14:39:53.966007\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"toProfile\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"username\": \"superman\",\n"
                                    + "        \"password\": \"$2a$10$OibzI4RpJ7xsGAiVmhm2M.1xWZ0ew9BdnFRI/jwykZ58UVCuRMXsy\",\n"
                                    + "        \"firstName\": \"clark\",\n"
                                    + "        \"lastName\": \"kent\",\n"
                                    + "        \"email\": \"superman@gmail.com\",\n"
                                    + "        \"phone\": \"98760001\",\n"
                                    + "        \"userType\": \"U\",\n"
                                    + "        \"registrationDate\": \"2022-07-05T14:39:53.966007\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"provider\": {\n"
                                    + "        \"id\": 2,\n"
                                    + "        \"username\": \"batman\",\n"
                                    + "        \"password\": \"$2a$10$5bclPpdulMo0YncfEbMJjOlHru9F8CfKQxRpD.Hs.HZBarnRw8dsi\",\n"
                                    + "        \"firstName\": \"bruce\",\n"
                                    + "        \"lastName\": \"wane\",\n"
                                    + "        \"email\": \"batman@gmail.com\",\n"
                                    + "        \"phone\": \"98760002\",\n"
                                    + "        \"userType\": \"S\",\n"
                                    + "        \"registrationDate\": \"2022-07-05T14:39:53.966007\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"user\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"username\": \"superman\",\n"
                                    + "        \"password\": \"$2a$10$OibzI4RpJ7xsGAiVmhm2M.1xWZ0ew9BdnFRI/jwykZ58UVCuRMXsy\",\n"
                                    + "        \"firstName\": \"clark\",\n"
                                    + "        \"lastName\": \"kent\",\n"
                                    + "        \"email\": \"superman@gmail.com\",\n"
                                    + "        \"phone\": \"98760001\",\n"
                                    + "        \"userType\": \"U\",\n"
                                    + "        \"registrationDate\": \"2022-07-05T14:39:53.966007\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"open\": true,\n"
                                    + "      \"accepted\": false,\n"
                                    + "      \"closed\": false,\n"
                                    + "      \"rfp\": false,\n"
                                    + "      \"rff\": false,\n"
                                    + "      \"approved\": false,\n"
                                    + "      \"pro\": true,\n"
                                    + "      \"rejected\": false\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"id\": 4,\n"
                                    + "      \"fromProfileId\": 4,\n"
                                    + "      \"toProfileId\": 1,\n"
                                    + "      \"requestId\": 1,\n"
                                    + "      \"title\": \"DOBOT MG400 Lightweight Desktop Robotic Arm\",\n"
                                    + "      \"type\": \"PRO\",\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"description\": \"DOBOT MG400 is a lightweight space-saving desktop robotic arm suitable for diversified manufacturing needs. It is flexible to deploy and easy to use, perfect for small space applications. MG400 is a good fit for automated workbench scenarios in tight workspaces that require fast deployment and changeover.\",\n"
                                    + "      \"cost\": 2888000,\n"
                                    + "      \"repayment\": null,\n"
                                    + "      \"specifications\": \"With the footprint dimension of 190mm, MG400 can fit in any production environment smaller than one piece of A4 paper and free up more space in the plant for production. It is the perfect fit for repeating lightweight tasks and automated workbench scenarios in tight workspaces. The compact desktop collaborative robot weighs only 8kg but has a payload up to 750g.\",\n"
                                    + "      \"createdTimestamp\": \"2022-07-05T14:39:54.487971\",\n"
                                    + "      \"fromProfile\": {\n"
                                    + "        \"id\": 4,\n"
                                    + "        \"username\": \"spiderman\",\n"
                                    + "        \"password\": \"$2a$10$SsV7lN8aua4ktPNQ29VKsOJFgLBJVhDL1yJEF2nRltiAB9Lq7yIou\",\n"
                                    + "        \"firstName\": \"peter\",\n"
                                    + "        \"lastName\": \"parker\",\n"
                                    + "        \"email\": \"spiderman@gmail.com\",\n"
                                    + "        \"phone\": \"98760004\",\n"
                                    + "        \"userType\": \"D\",\n"
                                    + "        \"registrationDate\": \"2022-07-05T14:39:53.966007\",\n"
                                    + "        \"walletId\": \"5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK\"\n"
                                    + "      },\n"
                                    + "      \"toProfile\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"username\": \"superman\",\n"
                                    + "        \"password\": \"$2a$10$OibzI4RpJ7xsGAiVmhm2M.1xWZ0ew9BdnFRI/jwykZ58UVCuRMXsy\",\n"
                                    + "        \"firstName\": \"clark\",\n"
                                    + "        \"lastName\": \"kent\",\n"
                                    + "        \"email\": \"superman@gmail.com\",\n"
                                    + "        \"phone\": \"98760001\",\n"
                                    + "        \"userType\": \"U\",\n"
                                    + "        \"registrationDate\": \"2022-07-05T14:39:53.966007\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"provider\": {\n"
                                    + "        \"id\": 4,\n"
                                    + "        \"username\": \"spiderman\",\n"
                                    + "        \"password\": \"$2a$10$SsV7lN8aua4ktPNQ29VKsOJFgLBJVhDL1yJEF2nRltiAB9Lq7yIou\",\n"
                                    + "        \"firstName\": \"peter\",\n"
                                    + "        \"lastName\": \"parker\",\n"
                                    + "        \"email\": \"spiderman@gmail.com\",\n"
                                    + "        \"phone\": \"98760004\",\n"
                                    + "        \"userType\": \"D\",\n"
                                    + "        \"registrationDate\": \"2022-07-05T14:39:53.966007\",\n"
                                    + "        \"walletId\": \"5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK\"\n"
                                    + "      },\n"
                                    + "      \"user\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"username\": \"superman\",\n"
                                    + "        \"password\": \"$2a$10$OibzI4RpJ7xsGAiVmhm2M.1xWZ0ew9BdnFRI/jwykZ58UVCuRMXsy\",\n"
                                    + "        \"firstName\": \"clark\",\n"
                                    + "        \"lastName\": \"kent\",\n"
                                    + "        \"email\": \"superman@gmail.com\",\n"
                                    + "        \"phone\": \"98760001\",\n"
                                    + "        \"userType\": \"U\",\n"
                                    + "        \"registrationDate\": \"2022-07-05T14:39:53.966007\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"open\": true,\n"
                                    + "      \"accepted\": false,\n"
                                    + "      \"closed\": false,\n"
                                    + "      \"rfp\": false,\n"
                                    + "      \"rff\": false,\n"
                                    + "      \"approved\": false,\n"
                                    + "      \"pro\": true,\n"
                                    + "      \"rejected\": false\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/request/pro/for/1\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getProposalsFor(
      @PathVariable Integer rfpId,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProvider,
      @RequestParam(required = false, defaultValue = "false") Boolean includeUser,
      HttpServletRequest httpRequest)
      throws GetUserProfileException {

    Iterable<Request> requests = requestService.getRequestsByRequestId(rfpId);

    ArrayList<Proposal> proposals = new ArrayList<Proposal>();

    for (Request request : requests) {
      Proposal proposal = new Proposal(request);

      if (includeProvider)
        proposal.setProvider(getUserProfile(proposal.getFromProfileId(), httpRequest));

      if (includeUser) proposal.setUser(getUserProfile(proposal.getToProfileId(), httpRequest));

      proposals.add(proposal);
    }

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), proposals, httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets proposals by {@code fromProfileId}.
   *
   * @param fromProfileId the profile id of the solution provider
   * @param status the status of the proposal
   * @param includeProvider when {@code true}, includes the profile of the solution provider in the
   *     returned proposals
   * @param includeUser when {@code true}, includes the profile of the user in the returned
   *     proposals
   * @param httpRequest the http request
   * @return a list of proposals
   */
  @GetMapping("/pro/from/{fromProfileId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve proposals by from profile Id",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the list of proposals in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithProposals.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-proposalsFrom-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-05T15:23:54.8216974\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 3,\n"
                                    + "      \"fromProfileId\": 2,\n"
                                    + "      \"toProfileId\": 1,\n"
                                    + "      \"requestId\": 1,\n"
                                    + "      \"title\": \"TX2touch-90 POWER Cobot\",\n"
                                    + "      \"type\": \"PRO\",\n"
                                    + "      \"status\": \"O\",\n"
                                    + "      \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "      \"cost\": 3000000,\n"
                                    + "      \"repayment\": null,\n"
                                    + "      \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "      \"createdTimestamp\": \"2022-07-05T15:21:23.255662\",\n"
                                    + "      \"fromProfile\": {\n"
                                    + "        \"id\": 2,\n"
                                    + "        \"username\": \"batman\",\n"
                                    + "        \"password\": \"$2a$10$6BHgYUJzzcwkWppj17kEi.ZR1dbks.w81h2jHOE/jdRwpRuDmpU6G\",\n"
                                    + "        \"firstName\": \"bruce\",\n"
                                    + "        \"lastName\": \"wane\",\n"
                                    + "        \"email\": \"batman@gmail.com\",\n"
                                    + "        \"phone\": \"98760002\",\n"
                                    + "        \"userType\": \"S\",\n"
                                    + "        \"registrationDate\": \"2022-07-05T15:21:22.733913\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"toProfile\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"username\": \"superman\",\n"
                                    + "        \"password\": \"$2a$10$whpUZ9K4HfxDeeLLKVY0NurFoL/oEFpb8IR99wQjA7LQC1W9sZkiK\",\n"
                                    + "        \"firstName\": \"clark\",\n"
                                    + "        \"lastName\": \"kent\",\n"
                                    + "        \"email\": \"superman@gmail.com\",\n"
                                    + "        \"phone\": \"98760001\",\n"
                                    + "        \"userType\": \"U\",\n"
                                    + "        \"registrationDate\": \"2022-07-05T15:21:22.733913\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"provider\": {\n"
                                    + "        \"id\": 2,\n"
                                    + "        \"username\": \"batman\",\n"
                                    + "        \"password\": \"$2a$10$6BHgYUJzzcwkWppj17kEi.ZR1dbks.w81h2jHOE/jdRwpRuDmpU6G\",\n"
                                    + "        \"firstName\": \"bruce\",\n"
                                    + "        \"lastName\": \"wane\",\n"
                                    + "        \"email\": \"batman@gmail.com\",\n"
                                    + "        \"phone\": \"98760002\",\n"
                                    + "        \"userType\": \"S\",\n"
                                    + "        \"registrationDate\": \"2022-07-05T15:21:22.733913\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"user\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"username\": \"superman\",\n"
                                    + "        \"password\": \"$2a$10$whpUZ9K4HfxDeeLLKVY0NurFoL/oEFpb8IR99wQjA7LQC1W9sZkiK\",\n"
                                    + "        \"firstName\": \"clark\",\n"
                                    + "        \"lastName\": \"kent\",\n"
                                    + "        \"email\": \"superman@gmail.com\",\n"
                                    + "        \"phone\": \"98760001\",\n"
                                    + "        \"userType\": \"U\",\n"
                                    + "        \"registrationDate\": \"2022-07-05T15:21:22.733913\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"open\": true,\n"
                                    + "      \"accepted\": false,\n"
                                    + "      \"closed\": false,\n"
                                    + "      \"rff\": false,\n"
                                    + "      \"approved\": false,\n"
                                    + "      \"pro\": true,\n"
                                    + "      \"rfp\": false,\n"
                                    + "      \"rejected\": false\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/request/pro/from/2\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getProposalsFrom(
      @PathVariable(required = true) Integer fromProfileId,
      @RequestParam(required = false, defaultValue = "") String status,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProvider,
      @RequestParam(required = false, defaultValue = "false") Boolean includeUser,
      HttpServletRequest httpRequest)
      throws GetUserProfileException {

    Iterable<Request> requests = null;

    if (status.length() > 0)
      requests =
          requestService.getRequestsByFromProfileIdAndTypeAndStatus(
              fromProfileId, Request.TYPE_PRO, status);
    else
      requests = requestService.getRequestsByFromProfileIdAndType(fromProfileId, Request.TYPE_PRO);

    ArrayList<Proposal> proposals = new ArrayList<Proposal>();

    for (Request request : requests) {
      Proposal proposal = new Proposal(request);

      if (includeProvider)
        proposal.setProvider(getUserProfile(proposal.getFromProfileId(), httpRequest));

      if (includeUser) proposal.setUser(getUserProfile(proposal.getToProfileId(), httpRequest));

      proposals.add(proposal);
    }

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), proposals, httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets proposals by {@code toProfileId}.
   *
   * @param toProfileId the profile id of the user recipient of the proposal
   * @param status the status of the proposal
   * @param includeProvider when {@code true}, includes the profile of the solution provider in the
   *     returned proposals
   * @param includeUser when {@code true}, includes the profile of the user in the returned
   *     proposals
   * @param httpRequest the http request
   * @return a list of proposals
   */
  @GetMapping("/pro/to/{toProfileId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve proposals by to profile Id",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the list of proposals in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithProposals.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-proposalsTo-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-05T17:18:59.6148033\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 4,\n"
                                    + "      \"fromProfileId\": 4,\n"
                                    + "      \"toProfileId\": 1,\n"
                                    + "      \"requestId\": 1,\n"
                                    + "      \"title\": \"DOBOT MG400 Lightweight Desktop Robotic Arm\",\n"
                                    + "      \"type\": \"PRO\",\n"
                                    + "      \"status\": \"ACC\",\n"
                                    + "      \"description\": \"DOBOT MG400 is a lightweight space-saving desktop robotic arm suitable for diversified manufacturing needs. It is flexible to deploy and easy to use, perfect for small space applications. MG400 is a good fit for automated workbench scenarios in tight workspaces that require fast deployment and changeover.\",\n"
                                    + "      \"cost\": 2888000,\n"
                                    + "      \"repayment\": null,\n"
                                    + "      \"specifications\": \"With the footprint dimension of 190mm, MG400 can fit in any production environment smaller than one piece of A4 paper and free up more space in the plant for production. It is the perfect fit for repeating lightweight tasks and automated workbench scenarios in tight workspaces. The compact desktop collaborative robot weighs only 8kg but has a payload up to 750g.\",\n"
                                    + "      \"createdTimestamp\": \"2022-07-05T17:17:23.271415\",\n"
                                    + "      \"fromProfile\": {\n"
                                    + "        \"id\": 4,\n"
                                    + "        \"username\": \"spiderman\",\n"
                                    + "        \"password\": \"$2a$10$gNrADxB519Sstykd25dZw.ARokQEN8kVuDT.DWOcOmQ8Bwe6QecCe\",\n"
                                    + "        \"firstName\": \"peter\",\n"
                                    + "        \"lastName\": \"parker\",\n"
                                    + "        \"email\": \"spiderman@gmail.com\",\n"
                                    + "        \"phone\": \"98760004\",\n"
                                    + "        \"userType\": \"D\",\n"
                                    + "        \"registrationDate\": \"2022-07-05T17:17:22.794403\",\n"
                                    + "        \"walletId\": \"5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK\"\n"
                                    + "      },\n"
                                    + "      \"toProfile\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"username\": \"superman\",\n"
                                    + "        \"password\": \"$2a$10$4BRegF7QaZwRbVYkyUxJj.MsM0d0oGMw3pw56i9MAFF0rdQ6rocJi\",\n"
                                    + "        \"firstName\": \"clark\",\n"
                                    + "        \"lastName\": \"kent\",\n"
                                    + "        \"email\": \"superman@gmail.com\",\n"
                                    + "        \"phone\": \"98760001\",\n"
                                    + "        \"userType\": \"U\",\n"
                                    + "        \"registrationDate\": \"2022-07-05T17:17:22.794403\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"provider\": {\n"
                                    + "        \"id\": 4,\n"
                                    + "        \"username\": \"spiderman\",\n"
                                    + "        \"password\": \"$2a$10$gNrADxB519Sstykd25dZw.ARokQEN8kVuDT.DWOcOmQ8Bwe6QecCe\",\n"
                                    + "        \"firstName\": \"peter\",\n"
                                    + "        \"lastName\": \"parker\",\n"
                                    + "        \"email\": \"spiderman@gmail.com\",\n"
                                    + "        \"phone\": \"98760004\",\n"
                                    + "        \"userType\": \"D\",\n"
                                    + "        \"registrationDate\": \"2022-07-05T17:17:22.794403\",\n"
                                    + "        \"walletId\": \"5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK\"\n"
                                    + "      },\n"
                                    + "      \"user\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"username\": \"superman\",\n"
                                    + "        \"password\": \"$2a$10$4BRegF7QaZwRbVYkyUxJj.MsM0d0oGMw3pw56i9MAFF0rdQ6rocJi\",\n"
                                    + "        \"firstName\": \"clark\",\n"
                                    + "        \"lastName\": \"kent\",\n"
                                    + "        \"email\": \"superman@gmail.com\",\n"
                                    + "        \"phone\": \"98760001\",\n"
                                    + "        \"userType\": \"U\",\n"
                                    + "        \"registrationDate\": \"2022-07-05T17:17:22.794403\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"open\": false,\n"
                                    + "      \"accepted\": false,\n"
                                    + "      \"closed\": false,\n"
                                    + "      \"rfp\": false,\n"
                                    + "      \"pro\": true,\n"
                                    + "      \"rejected\": false,\n"
                                    + "      \"rff\": false,\n"
                                    + "      \"approved\": false\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/request/pro/to/1\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getProposalsTo(
      @PathVariable(required = true) Integer toProfileId,
      @RequestParam(required = false, defaultValue = "") String status,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProvider,
      @RequestParam(required = false, defaultValue = "false") Boolean includeUser,
      HttpServletRequest httpRequest)
      throws GetUserProfileException {

    Iterable<Request> requests;

    if (status.length() > 0)
      requests =
          requestService.getRequestsByToProfileIdAndTypeAndStatus(
              toProfileId, Request.TYPE_PRO, status);
    else requests = requestService.getRequestsByToProfileIdAndType(toProfileId, Request.TYPE_PRO);

    ArrayList<Proposal> proposals = new ArrayList<Proposal>();

    for (Request request : requests) {
      Proposal proposal = new Proposal(request);

      if (includeProvider)
        proposal.setProvider(getUserProfile(proposal.getFromProfileId(), httpRequest));

      if (includeUser) proposal.setUser(getUserProfile(proposal.getToProfileId(), httpRequest));

      proposals.add(proposal);
    }

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), proposals, httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  public Contract getContract(int requestId, HttpServletRequest request) throws RequestException {

    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(getHttpHeaders(request));

    String url = urlBase + String.format(urlContractGetByRequestId, requestId);

    ResponseMessageWithContract msg =
        restTemplate
            .exchange(url, HttpMethod.GET, entity, ResponseMessageWithContract.class)
            .getBody();

    if (msg.isOk()) return msg.getData();
    else throw new RequestException(msg.getMessage());
  }

  /**
   * Gets request for fundings.
   *
   * @param statuses the list of status of the requests
   * @param includeUser when {@code true}, includes the profile of the user in the returned requests
   * @param includeProposal when {@code true}, includes the proposals the request for fundings are
   *     based on
   * @param includeProvider when {@code true}, includes the profile of the solution provider in the
   *     returned request for fundings
   * @param includeContract when {@code true}, includes the contract in the returned request for
   *     fundings
   * @param httpRequest the http request
   * @return a list of request for fundings
   */
  @GetMapping("/rff")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve request for fundings",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the list of request for fundings in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequestForFundings.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-requestForFundings-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-13T15:13:53.961125\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 3,\n"
                                    + "      \"fromProfileId\": 1,\n"
                                    + "      \"toProfileId\": 0,\n"
                                    + "      \"requestId\": 2,\n"
                                    + "      \"title\": \"Single Patient Dialysis Machine TR-8000\",\n"
                                    + "      \"type\": \"RFF\",\n"
                                    + "      \"status\": \"PF\",\n"
                                    + "      \"description\": \"The single patients dialysis machine can perform prescribed dialysis which adjust the dialysate conductivity in accordance with each patient. It mainly consists of Monitor/alarm part, Dialysate supply/UF control part, Extracorporeal blood circuit part, and Electrical control part.\",\n"
                                    + "      \"cost\": 1300000,\n"
                                    + "      \"repayment\": 1500000,\n"
                                    + "      \"specifications\": \"Supported by the advanced technologies of TORAY, TR-8000 offers comfortable dialysis treatment to patients, easy operation to medical staff and contributes to medical development.\\n\\n- Easy operability by changing the position of external parts\\n- The position of rinse ports, couplers and bicarbonate cartridge was changed and you can operate them without squat.\\n- Casters became larger and you can move it by less power.\\nImprovement of standard functions\\n- The chamber level adjustment has been newly equipped as standard\\n- Self-test time became shorter\\nVarious new options and accessories\\n- BVM, Kt/V indicator, Bicarbonate cartridge, etc.\",\n"
                                    + "      \"createdTimestamp\": \"2022-07-13T07:11:35.793\",\n"
                                    + "      \"fromProfile\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"username\": \"sbipcc\",\n"
                                    + "        \"password\": \"$2a$10$sgOceMLdInG7v4M3tUXhceHWCWDitpswZnsF/CMbg5R3jSWVYQEhe\",\n"
                                    + "        \"firstName\": \"SBIP\",\n"
                                    + "        \"lastName\": \"Community Clinic\",\n"
                                    + "        \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "        \"phone\": \"98760001\",\n"
                                    + "        \"userType\": \"U\",\n"
                                    + "        \"registrationDate\": \"2022-07-13T15:09:50.719216\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"toProfile\": null,\n"
                                    + "      \"proposal\": {\n"
                                    + "        \"id\": 2,\n"
                                    + "        \"fromProfileId\": 2,\n"
                                    + "        \"toProfileId\": 1,\n"
                                    + "        \"requestId\": 1,\n"
                                    + "        \"title\": \"Single Patient Dialysis Machine TR-8000\",\n"
                                    + "        \"type\": \"PRO\",\n"
                                    + "        \"status\": \"FR\",\n"
                                    + "        \"description\": \"The single patients dialysis machine can perform prescribed dialysis which adjust the dialysate conductivity in accordance with each patient. It mainly consists of Monitor/alarm part, Dialysate supply/UF control part, Extracorporeal blood circuit part, and Electrical control part.\",\n"
                                    + "        \"cost\": 1300000,\n"
                                    + "        \"repayment\": null,\n"
                                    + "        \"specifications\": \"Supported by the advanced technologies of TORAY, TR-8000 offers comfortable dialysis treatment to patients, easy operation to medical staff and contributes to medical development.\\n\\n- Easy operability by changing the position of external parts\\n- The position of rinse ports, couplers and bicarbonate cartridge was changed and you can operate them without squat.\\n- Casters became larger and you can move it by less power.\\nImprovement of standard functions\\n- The chamber level adjustment has been newly equipped as standard\\n- Self-test time became shorter\\nVarious new options and accessories\\n- BVM, Kt/V indicator, Bicarbonate cartridge, etc.\",\n"
                                    + "        \"createdTimestamp\": \"2022-07-13T07:11:18.964\",\n"
                                    + "        \"fromProfile\": {\n"
                                    + "          \"id\": 2,\n"
                                    + "          \"username\": \"toraymed\",\n"
                                    + "          \"password\": \"$2a$10$Px.aXaMPL92Aprczh0pFY.mLcecqFDfR0AZgOwoQWpq7ybAZfS3cm\",\n"
                                    + "          \"firstName\": \"Toray\",\n"
                                    + "          \"lastName\": \"Medical\",\n"
                                    + "          \"email\": \"toray@gmail.com\",\n"
                                    + "          \"phone\": \"98760002\",\n"
                                    + "          \"userType\": \"S\",\n"
                                    + "          \"registrationDate\": \"2022-07-13T15:09:50.719216\",\n"
                                    + "          \"walletId\": \"SXLRdrywXBntChoDLPjEF1KDQH95eu5EvkA4Uge1hjU\"\n"
                                    + "        },\n"
                                    + "        \"toProfile\": null,\n"
                                    + "        \"provider\": {\n"
                                    + "          \"id\": 2,\n"
                                    + "          \"username\": \"toraymed\",\n"
                                    + "          \"password\": \"$2a$10$Px.aXaMPL92Aprczh0pFY.mLcecqFDfR0AZgOwoQWpq7ybAZfS3cm\",\n"
                                    + "          \"firstName\": \"Toray\",\n"
                                    + "          \"lastName\": \"Medical\",\n"
                                    + "          \"email\": \"toray@gmail.com\",\n"
                                    + "          \"phone\": \"98760002\",\n"
                                    + "          \"userType\": \"S\",\n"
                                    + "          \"registrationDate\": \"2022-07-13T15:09:50.719216\",\n"
                                    + "          \"walletId\": \"SXLRdrywXBntChoDLPjEF1KDQH95eu5EvkA4Uge1hjU\"\n"
                                    + "        },\n"
                                    + "        \"user\": null,\n"
                                    + "        \"open\": false,\n"
                                    + "        \"closed\": false,\n"
                                    + "        \"rfp\": false,\n"
                                    + "        \"rff\": false,\n"
                                    + "        \"rejected\": false,\n"
                                    + "        \"approved\": false,\n"
                                    + "        \"pro\": true,\n"
                                    + "        \"accepted\": false\n"
                                    + "      },\n"
                                    + "      \"contract\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"requestId\": 3,\n"
                                    + "        \"walletId\": \"GAyCywe7wYQ49XA92BrDBVvj2CMKeEGMmGjseQR3yFua\",\n"
                                    + "        \"targetAmount\": 1300000,\n"
                                    + "        \"repaymentAmount\": 1500000,\n"
                                    + "        \"status\": \"PF\",\n"
                                    + "        \"createdTimestamp\": \"2022-07-06T20:48:28.46218\",\n"
                                    + "        \"fundings\": [\n"
                                    + "          {\n"
                                    + "            \"id\": 1,\n"
                                    + "            \"contractId\": 1,\n"
                                    + "            \"profileId\": 4,\n"
                                    + "            \"status\": \"FIC\",\n"
                                    + "            \"fundingAmount\": 400000,\n"
                                    + "            \"repaymentAmount\": 460000,\n"
                                    + "            \"disbursedAmount\": 0,\n"
                                    + "            \"createdTimestamp\": \"2022-07-13T15:10:21.875806\",\n"
                                    + "            \"profile\": null\n"
                                    + "          },\n"
                                    + "          {\n"
                                    + "            \"id\": 2,\n"
                                    + "            \"contractId\": 1,\n"
                                    + "            \"profileId\": 5,\n"
                                    + "            \"status\": \"FIC\",\n"
                                    + "            \"fundingAmount\": 400000,\n"
                                    + "            \"repaymentAmount\": 460000,\n"
                                    + "            \"disbursedAmount\": 0,\n"
                                    + "            \"createdTimestamp\": \"2022-07-13T15:12:15.163718\",\n"
                                    + "            \"profile\": null\n"
                                    + "          }\n"
                                    + "        ],\n"
                                    + "        \"outstandingAmount\": 500000,\n"
                                    + "        \"yield\": 15,\n"
                                    + "        \"raisedAmount\": 800000\n"
                                    + "      },\n"
                                    + "      \"fromUser\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"username\": \"sbipcc\",\n"
                                    + "        \"password\": \"$2a$10$sgOceMLdInG7v4M3tUXhceHWCWDitpswZnsF/CMbg5R3jSWVYQEhe\",\n"
                                    + "        \"firstName\": \"SBIP\",\n"
                                    + "        \"lastName\": \"Community Clinic\",\n"
                                    + "        \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "        \"phone\": \"98760001\",\n"
                                    + "        \"userType\": \"U\",\n"
                                    + "        \"registrationDate\": \"2022-07-13T15:09:50.719216\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"open\": false,\n"
                                    + "      \"closed\": false,\n"
                                    + "      \"rfp\": false,\n"
                                    + "      \"rff\": true,\n"
                                    + "      \"rejected\": false,\n"
                                    + "      \"approved\": false,\n"
                                    + "      \"pro\": false,\n"
                                    + "      \"accepted\": false\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/request/rff\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getRequestForFundings(
      @RequestParam(required = false, defaultValue = "") List<String> statuses,
      @RequestParam(required = false, defaultValue = "false") Boolean includeUser,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProposal,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProvider,
      @RequestParam(required = false, defaultValue = "false") Boolean includeContract,
      HttpServletRequest httpRequest)
      throws RequestException, EntityNotFoundException {

    Iterable<Request> requests = null;

    if (statuses.size() == 0) requests = requestService.getRequestsByType(Request.TYPE_RFF);
    else {

      for (String status : statuses) {

        if (requests == null)
          requests = requestService.getRequestsByTypeAndStatus(Request.TYPE_RFF, status);
        else
          requests =
              Iterables.concat(
                  requests, requestService.getRequestsByTypeAndStatus(Request.TYPE_RFF, status));
      }
    }

    ArrayList<RequestForFunding> rffs = new ArrayList<RequestForFunding>();

    for (Request request : requests) {

      RequestForFunding rff = new RequestForFunding(request);

      if (includeUser) rff.setFromUser(getUserProfile(rff.getFromProfileId(), httpRequest));

      if (includeProposal) {
        Proposal proposal =
            (Proposal)
                getProposal(rff.getRequestId(), includeProvider, false, httpRequest)
                    .getBody()
                    .getData();
        rff.setProposal(proposal);
      }

      if (includeContract) rff.setContract(getContract(rff.getId(), httpRequest));

      rffs.add(rff);
    }

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), rffs, httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets request for fundings by {@code fromProfileId}.
   *
   * @param fromProfileId the profile id of user that raised the request for funding
   * @param statuses the list of status of the requests
   * @param includeUser when {@code true}, includes the profile of the user in the returned requests
   * @param includeProposal when {@code true}, includes the proposals the request for fundings are
   *     based on
   * @param includeProvider when {@code true}, includes the profile of the solution provider in the
   *     returned request for fundings
   * @param includeContract when {@code true}, includes the contract in the returned request for
   *     fundings
   * @param httpRequest the http request
   * @return a list of request for fundings
   */
  @GetMapping("/rff/from/{fromProfileId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve request for fundings from user",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the list of request for fundings in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequestForFundings.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-requestForFundingsFrom-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-13T16:46:46.0059342\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 3,\n"
                                    + "      \"fromProfileId\": 1,\n"
                                    + "      \"toProfileId\": 0,\n"
                                    + "      \"requestId\": 2,\n"
                                    + "      \"title\": \"Single Patient Dialysis Machine TR-8000\",\n"
                                    + "      \"type\": \"RFF\",\n"
                                    + "      \"status\": \"PF\",\n"
                                    + "      \"description\": \"The single patients dialysis machine can perform prescribed dialysis which adjust the dialysate conductivity in accordance with each patient. It mainly consists of Monitor/alarm part, Dialysate supply/UF control part, Extracorporeal blood circuit part, and Electrical control part.\",\n"
                                    + "      \"cost\": 1300000,\n"
                                    + "      \"repayment\": 1500000,\n"
                                    + "      \"specifications\": \"Supported by the advanced technologies of TORAY, TR-8000 offers comfortable dialysis treatment to patients, easy operation to medical staff and contributes to medical development.\\n\\n- Easy operability by changing the position of external parts\\n- The position of rinse ports, couplers and bicarbonate cartridge was changed and you can operate them without squat.\\n- Casters became larger and you can move it by less power.\\nImprovement of standard functions\\n- The chamber level adjustment has been newly equipped as standard\\n- Self-test time became shorter\\nVarious new options and accessories\\n- BVM, Kt/V indicator, Bicarbonate cartridge, etc.\",\n"
                                    + "      \"createdTimestamp\": \"2022-07-13T08:44:03.72\",\n"
                                    + "      \"fromProfile\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"username\": \"sbipcc\",\n"
                                    + "        \"password\": \"$2a$10$YC.MkDk6ENenlaXayPC0Se7MuPWS4ulkfnj5yZmSDKXNFdpvdhNd6\",\n"
                                    + "        \"firstName\": \"SBIP\",\n"
                                    + "        \"lastName\": \"Community Clinic\",\n"
                                    + "        \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "        \"phone\": \"98760001\",\n"
                                    + "        \"userType\": \"U\",\n"
                                    + "        \"registrationDate\": \"2022-07-13T16:43:09.873556\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"toProfile\": null,\n"
                                    + "      \"proposal\": null,\n"
                                    + "      \"contract\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"requestId\": 3,\n"
                                    + "        \"walletId\": \"GAyCywe7wYQ49XA92BrDBVvj2CMKeEGMmGjseQR3yFua\",\n"
                                    + "        \"targetAmount\": 1300000,\n"
                                    + "        \"repaymentAmount\": 1500000,\n"
                                    + "        \"status\": \"PF\",\n"
                                    + "        \"createdTimestamp\": \"2022-07-06T20:48:28.46218\",\n"
                                    + "        \"fundings\": [\n"
                                    + "          {\n"
                                    + "            \"id\": 1,\n"
                                    + "            \"contractId\": 1,\n"
                                    + "            \"profileId\": 4,\n"
                                    + "            \"status\": \"FIC\",\n"
                                    + "            \"fundingAmount\": 8000,\n"
                                    + "            \"repaymentAmount\": 9200,\n"
                                    + "            \"disbursedAmount\": 0,\n"
                                    + "            \"createdTimestamp\": \"2022-07-13T16:46:28.406471\",\n"
                                    + "            \"profile\": null\n"
                                    + "          }\n"
                                    + "        ],\n"
                                    + "        \"outstandingAmount\": 1292000,\n"
                                    + "        \"raisedAmount\": 8000,\n"
                                    + "        \"yield\": 15\n"
                                    + "      },\n"
                                    + "      \"fromUser\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"username\": \"sbipcc\",\n"
                                    + "        \"password\": \"$2a$10$YC.MkDk6ENenlaXayPC0Se7MuPWS4ulkfnj5yZmSDKXNFdpvdhNd6\",\n"
                                    + "        \"firstName\": \"SBIP\",\n"
                                    + "        \"lastName\": \"Community Clinic\",\n"
                                    + "        \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "        \"phone\": \"98760001\",\n"
                                    + "        \"userType\": \"U\",\n"
                                    + "        \"registrationDate\": \"2022-07-13T16:43:09.873556\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"open\": false,\n"
                                    + "      \"accepted\": false,\n"
                                    + "      \"closed\": false,\n"
                                    + "      \"rfp\": false,\n"
                                    + "      \"pro\": false,\n"
                                    + "      \"approved\": false,\n"
                                    + "      \"rff\": true,\n"
                                    + "      \"rejected\": false\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/request/rff/from/1\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getRequestForFundingsFrom(
      @PathVariable Integer fromProfileId,
      @RequestParam(required = false, defaultValue = "") List<String> statuses,
      @RequestParam(required = false, defaultValue = "false") Boolean includeUser,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProposal,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProvider,
      @RequestParam(required = false, defaultValue = "false") Boolean includeContract,
      HttpServletRequest httpRequest)
      throws RequestException, EntityNotFoundException {

    Iterable<Request> requests = null;

    if (statuses.size() == 0)
      requests = requestService.getRequestsByFromProfileIdAndType(fromProfileId, Request.TYPE_RFF);
    else {

      for (String status : statuses) {

        if (requests == null)
          requests =
              requestService.getRequestsByFromProfileIdAndTypeAndStatus(
                  fromProfileId, Request.TYPE_RFF, status);
        else
          requests =
              Iterables.concat(
                  requests,
                  requestService.getRequestsByFromProfileIdAndTypeAndStatus(
                      fromProfileId, Request.TYPE_RFF, status));
      }
    }

    ArrayList<RequestForFunding> rffs = new ArrayList<RequestForFunding>();

    for (Request request : requests) {

      RequestForFunding rff = new RequestForFunding(request);

      if (includeUser) rff.setFromUser(getUserProfile(rff.getFromProfileId(), httpRequest));

      if (includeProposal) {
        Proposal proposal =
            (Proposal)
                getProposal(rff.getRequestId(), includeProvider, false, httpRequest)
                    .getBody()
                    .getData();
        rff.setProposal(proposal);
      }

      if (includeContract) rff.setContract(getContract(rff.getId(), httpRequest));

      rffs.add(rff);
    }

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), rffs, httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets request for fundings of proposals for {@code providerId}.
   *
   * @param providerId the profile id of solution provider of the proposal
   * @param statuses the list of status of the requests
   * @param includeUser when {@code true}, includes the profile of the user in the returned requests
   * @param includeProposal when {@code true}, includes the proposal in the returned requests
   * @param includeContract when {@code true}, includes the contract in the returned requests
   * @param httpRequest the http request
   * @return a list of request for fundings
   */
  @GetMapping("/rff/for/{providerId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve request for fundings from user",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the list of request for fundings in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequestForFundings.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-requestForFundingsFor-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-13T18:03:13.3886436\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"id\": 3,\n"
                                    + "      \"fromProfileId\": 1,\n"
                                    + "      \"toProfileId\": 0,\n"
                                    + "      \"requestId\": 2,\n"
                                    + "      \"title\": \"Single Patient Dialysis Machine TR-8000\",\n"
                                    + "      \"type\": \"RFF\",\n"
                                    + "      \"status\": \"NF\",\n"
                                    + "      \"description\": \"The single patients dialysis machine can perform prescribed dialysis which adjust the dialysate conductivity in accordance with each patient. It mainly consists of Monitor/alarm part, Dialysate supply/UF control part, Extracorporeal blood circuit part, and Electrical control part.\",\n"
                                    + "      \"cost\": 1300000,\n"
                                    + "      \"repayment\": 1500000,\n"
                                    + "      \"specifications\": \"Supported by the advanced technologies of TORAY, TR-8000 offers comfortable dialysis treatment to patients, easy operation to medical staff and contributes to medical development.\\n\\n- Easy operability by changing the position of external parts\\n- The position of rinse ports, couplers and bicarbonate cartridge was changed and you can operate them without squat.\\n- Casters became larger and you can move it by less power.\\nImprovement of standard functions\\n- The chamber level adjustment has been newly equipped as standard\\n- Self-test time became shorter\\nVarious new options and accessories\\n- BVM, Kt/V indicator, Bicarbonate cartridge, etc.\",\n"
                                    + "      \"createdTimestamp\": \"2022-07-13T09:59:46.406\",\n"
                                    + "      \"fromProfile\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"username\": \"sbipcc\",\n"
                                    + "        \"password\": \"$2a$10$T1BStmgZj0kbtu23mgoRIeGVCKrwwyF/1AbA2Dlc/Zg631jKp7Qo.\",\n"
                                    + "        \"firstName\": \"SBIP\",\n"
                                    + "        \"lastName\": \"Community Clinic\",\n"
                                    + "        \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "        \"phone\": \"98760001\",\n"
                                    + "        \"userType\": \"U\",\n"
                                    + "        \"registrationDate\": \"2022-07-13T17:55:29.445038\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"toProfile\": null,\n"
                                    + "      \"proposal\": {\n"
                                    + "        \"id\": 2,\n"
                                    + "        \"fromProfileId\": 2,\n"
                                    + "        \"toProfileId\": 1,\n"
                                    + "        \"requestId\": 1,\n"
                                    + "        \"title\": \"Single Patient Dialysis Machine TR-8000\",\n"
                                    + "        \"type\": \"PRO\",\n"
                                    + "        \"status\": \"FR\",\n"
                                    + "        \"description\": \"The single patients dialysis machine can perform prescribed dialysis which adjust the dialysate conductivity in accordance with each patient. It mainly consists of Monitor/alarm part, Dialysate supply/UF control part, Extracorporeal blood circuit part, and Electrical control part.\",\n"
                                    + "        \"cost\": 1300000,\n"
                                    + "        \"repayment\": null,\n"
                                    + "        \"specifications\": \"Supported by the advanced technologies of TORAY, TR-8000 offers comfortable dialysis treatment to patients, easy operation to medical staff and contributes to medical development.\\n\\n- Easy operability by changing the position of external parts\\n- The position of rinse ports, couplers and bicarbonate cartridge was changed and you can operate them without squat.\\n- Casters became larger and you can move it by less power.\\nImprovement of standard functions\\n- The chamber level adjustment has been newly equipped as standard\\n- Self-test time became shorter\\nVarious new options and accessories\\n- BVM, Kt/V indicator, Bicarbonate cartridge, etc.\",\n"
                                    + "        \"createdTimestamp\": \"2022-07-13T09:59:03.012\",\n"
                                    + "        \"fromProfile\": null,\n"
                                    + "        \"toProfile\": null,\n"
                                    + "        \"provider\": null,\n"
                                    + "        \"user\": null,\n"
                                    + "        \"open\": false,\n"
                                    + "        \"accepted\": false,\n"
                                    + "        \"closed\": false,\n"
                                    + "        \"rfp\": false,\n"
                                    + "        \"pro\": true,\n"
                                    + "        \"rff\": false,\n"
                                    + "        \"rejected\": false\n"
                                    + "      },\n"
                                    + "      \"contract\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"requestId\": 3,\n"
                                    + "        \"walletId\": \"GAyCywe7wYQ49XA92BrDBVvj2CMKeEGMmGjseQR3yFua\",\n"
                                    + "        \"targetAmount\": 1300000,\n"
                                    + "        \"repaymentAmount\": 1500000,\n"
                                    + "        \"status\": \"O\",\n"
                                    + "        \"createdTimestamp\": \"2022-07-13T09:59:46.478\",\n"
                                    + "        \"fundings\": [],\n"
                                    + "        \"outstandingAmount\": 1300000,\n"
                                    + "        \"yield\": 15,\n"
                                    + "        \"raisedAmount\": 0\n"
                                    + "      },\n"
                                    + "      \"fromUser\": {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"username\": \"sbipcc\",\n"
                                    + "        \"password\": \"$2a$10$T1BStmgZj0kbtu23mgoRIeGVCKrwwyF/1AbA2Dlc/Zg631jKp7Qo.\",\n"
                                    + "        \"firstName\": \"SBIP\",\n"
                                    + "        \"lastName\": \"Community Clinic\",\n"
                                    + "        \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "        \"phone\": \"98760001\",\n"
                                    + "        \"userType\": \"U\",\n"
                                    + "        \"registrationDate\": \"2022-07-13T17:55:29.445038\",\n"
                                    + "        \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "      },\n"
                                    + "      \"open\": false,\n"
                                    + "      \"accepted\": false,\n"
                                    + "      \"closed\": false,\n"
                                    + "      \"rfp\": false,\n"
                                    + "      \"pro\": false,\n"
                                    + "      \"rff\": true,\n"
                                    + "      \"rejected\": false\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/request/rff/for/2\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getRequestForFundingsFor(
      @PathVariable Integer providerId,
      @RequestParam(required = false, defaultValue = "") List<String> statuses,
      @RequestParam(required = false, defaultValue = "false") Boolean includeUser,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProposal,
      @RequestParam(required = false, defaultValue = "false") Boolean includeContract,
      HttpServletRequest httpRequest)
      throws RequestException, EntityNotFoundException {

    Iterable<RequestForFunding> rffs = requestService.getRequestForFundingsFor(providerId);

    ArrayList<RequestForFunding> selected = new ArrayList<RequestForFunding>();

    ResponseMessage msg;

    for (RequestForFunding rff : rffs)
      if (statuses.size() == 0 || statuses.contains(rff.getStatus())) {
        if (includeUser) rff.setFromUser(getUserProfile(rff.getFromProfileId(), httpRequest));

        if (includeProposal) {

          msg = getProposal(rff.getRequestId(), false, true, httpRequest).getBody();

          if (msg.isOk()) rff.setProposal((Proposal) msg.getData());
          else throw new GetProposalException(msg.getMessage());
        }

        if (includeContract) rff.setContract(getContract(rff.getId(), httpRequest));

        selected.add(rff);
      }

    msg = new ResponseMessage(HttpStatus.OK.value(), selected, httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets request for payments by {@code fromProfileId}.
   *
   * @param fromProfileId the from user profile id
   * @param status the status of the request for payments
   * @param includeToUser when {@code true}, includes the to user profile in the returned request
   *     for payments
   * @param httpRequest the http request
   * @return a list of request for payments
   */
  @GetMapping("/rpy/from/{fromProfileId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve request for payments by from profile Id",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the list of request for payments in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithProposals.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-requestForPaymentsFrom-200.json",
                            value = ""))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getRequestForPaymentsFrom(
      @PathVariable Integer fromProfileId,
      @RequestParam(required = false, defaultValue = "") String status,
      @RequestParam(required = false, defaultValue = "false") Boolean includeToUser,
      HttpServletRequest httpRequest)
      throws RequestException {

    Iterable<Request> requests = null;

    if (status.length() > 0)
      requests =
          requestService.getRequestsByFromProfileIdAndTypeAndStatus(
              fromProfileId, Request.TYPE_RPY, status);
    else
      requests = requestService.getRequestsByFromProfileIdAndType(fromProfileId, Request.TYPE_RPY);

    ArrayList<RequestForPayment> rpys = new ArrayList<RequestForPayment>();

    for (Request request : requests) {
      RequestForPayment rpy = new RequestForPayment(request);

      if (includeToUser) rpy.setToProfile(getUserProfile(rpy.getToProfileId(), httpRequest));

      rpys.add(rpy);
    }

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), rpys, httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets request for payments by {@code toProfileId}.
   *
   * @param toProfileId the to user profile id
   * @param status the status of the request for payments
   * @param includeFromUser when {@code true}, includes the from user profile in the returned request
   *     for payments
   * @param httpRequest the http request
   * @return a list of request for payments
   */
  @GetMapping("/rpy/to/{toProfileId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve request for payments by to profile Id",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the list of request for payments in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithProposals.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-requestForPaymentsTo-200.json",
                            value = ""))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getRequestForPaymentsTo(
      @PathVariable Integer toProfileId,
      @RequestParam(required = false, defaultValue = "") String status,
      @RequestParam(required = false, defaultValue = "false") Boolean includeFromUser,
      HttpServletRequest httpRequest)
      throws RequestException {

    Iterable<Request> requests = null;

    if (status.length() > 0)
      requests =
          requestService.getRequestsByToProfileIdAndTypeAndStatus(
                  toProfileId, Request.TYPE_RPY, status);
    else
      requests = requestService.getRequestsByToProfileIdAndType(toProfileId, Request.TYPE_RPY);

    ArrayList<RequestForPayment> rpys = new ArrayList<RequestForPayment>();

    for (Request request : requests) {
      RequestForPayment rpy = new RequestForPayment(request);

      if (includeFromUser) rpy.setFromProfile(getUserProfile(rpy.getFromProfileId(), httpRequest));

      rpys.add(rpy);
    }

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), rpys, httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets request by id.
   *
   * @param id the request id
   * @param includeFromProfile when {@code true}, includes the from profile in the returned request
   * @param includeToProfile when {@code true}, includes the to profile in the returned request
   * @param httpRequest the http request
   * @return the request with the id if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/id/{id}")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve request by id",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a request in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequest.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-id-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-18T16:51:16.1411388\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 2,\n"
                                    + "    \"fromProfileId\": 2,\n"
                                    + "    \"toProfileId\": 1,\n"
                                    + "    \"requestId\": 1,\n"
                                    + "    \"title\": \"Single Patient Dialysis Machine TR-8000\",\n"
                                    + "    \"type\": \"PRO\",\n"
                                    + "    \"status\": \"FR\",\n"
                                    + "    \"description\": \"The single patients dialysis machine can perform prescribed dialysis which adjust the dialysate conductivity in accordance with each patient. It mainly consists of Monitor/alarm part, Dialysate supply/UF control part, Extracorporeal blood circuit part, and Electrical control part.\",\n"
                                    + "    \"cost\": 1200000,\n"
                                    + "    \"repayment\": null,\n"
                                    + "    \"specifications\": \"Supported by the advanced technologies of TORAY, TR-8000 offers comfortable dialysis treatment to patients, easy operation to medical staff and contributes to medical development.\\n\\n- Easy operability by changing the position of external parts\\n- The position of rinse ports, couplers and bicarbonate cartridge was changed and you can operate them without squat.\\n- Casters became larger and you can move it by less power.\\nImprovement of standard functions\\n- The chamber level adjustment has been newly equipped as standard\\n- Self-test time became shorter\\nVarious new options and accessories\\n- BVM, Kt/V indicator, Bicarbonate cartridge, etc.\",\n"
                                    + "    \"createdTimestamp\": \"2022-07-18T08:48:44.276\",\n"
                                    + "    \"fromProfile\": {\n"
                                    + "      \"id\": 2,\n"
                                    + "      \"username\": \"toraymed\",\n"
                                    + "      \"password\": \"$2a$10$/zDLxWg9A44YrJReBxLCee9vm7jzC3WJhkVxNeAe1V52zZ7FXmKxy\",\n"
                                    + "      \"firstName\": \"Toray\",\n"
                                    + "      \"lastName\": \"Medical\",\n"
                                    + "      \"email\": \"toray@gmail.com\",\n"
                                    + "      \"phone\": \"98760002\",\n"
                                    + "      \"userType\": \"S\",\n"
                                    + "      \"registrationDate\": \"2022-07-18T16:45:09.636589\",\n"
                                    + "      \"walletId\": \"SXLRdrywXBntChoDLPjEF1KDQH95eu5EvkA4Uge1hjU\"\n"
                                    + "    },\n"
                                    + "    \"toProfile\": {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"username\": \"sbipcc\",\n"
                                    + "      \"password\": \"$2a$10$IENpCwlTjYEb4UMBuJL1/elpUP2SOGivoDdPhSoX2rb1JiH0NZE1G\",\n"
                                    + "      \"firstName\": \"SBIP\",\n"
                                    + "      \"lastName\": \"Community Clinic\",\n"
                                    + "      \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "      \"phone\": \"98760001\",\n"
                                    + "      \"userType\": \"U\",\n"
                                    + "      \"registrationDate\": \"2022-07-18T16:45:09.636589\",\n"
                                    + "      \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "    },\n"
                                    + "    \"open\": false,\n"
                                    + "    \"accepted\": false,\n"
                                    + "    \"closed\": false,\n"
                                    + "    \"rfp\": false,\n"
                                    + "    \"pro\": true,\n"
                                    + "    \"rff\": false,\n"
                                    + "    \"rejected\": false\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/request/id/2\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Request with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-id-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-12T18:19:30.0991326\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"Request with id=19 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/request/id/19\",\n"
                                    + "  \"ok\": false\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> getById(
      @PathVariable Integer id,
      @RequestParam(required = false, defaultValue = "false") Boolean includeFromProfile,
      @RequestParam(required = false, defaultValue = "false") Boolean includeToProfile,
      HttpServletRequest httpRequest)
      throws GetUserProfileException, EntityNotFoundException {

    Request request = requestService.getRequestById(id);

    if (includeFromProfile)
      request.setFromProfile(getUserProfile(request.getFromProfileId(), httpRequest));

    if (includeToProfile)
      request.setToProfile(getUserProfile(request.getToProfileId(), httpRequest));

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), request, httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets user profile by {@code id}.
   *
   * @param productId the product id
   * @param request the http request
   * @return the product
   * @throws GetProductException any exception thrown when retrieving the product
   */
  protected Product getProduct(int productId, HttpServletRequest request)
      throws GetProductException {
    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(getHttpHeaders(request));

    String url = urlBase + String.format(urlProductGetById, productId);

    ResponseMessageWithProduct msg =
        restTemplate
            .exchange(url, HttpMethod.GET, entity, ResponseMessageWithProduct.class)
            .getBody();

    if (msg.isOk()) return msg.getData();
    else throw new GetProductException(msg.getMessage());
  }

  /**
   * Gets request for proposal.
   *
   * @param id the id of the request for proposal
   * @param includeProposals when {@code true}, includes proposals from solution providers in the
   *     returned request for proposal
   * @param includeUser when {@code true}, includes the profile of the user in the returned request
   *     for proposal
   * @param includeProvider when {@code true}, includes profiles of the solution providers in the
   *     returned proposals
   * @param includeProduct when {@code true}, includes the product the RFP is based on
   * @param request the http request
   * @return the request with the id if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/rfp/id/{id}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve a request for proposal",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a request for proposal in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequestForProposal.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-requestForProposal-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-08-02T11:41:47.1583137\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 1,\n"
                                    + "    \"fromProfileId\": 1,\n"
                                    + "    \"toProfileId\": 2,\n"
                                    + "    \"requestId\": 1,\n"
                                    + "    \"title\": \"TX2touch-90 POWER cobot\",\n"
                                    + "    \"type\": \"RFP\",\n"
                                    + "    \"status\": \"O\",\n"
                                    + "    \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "    \"cost\": 1000000,\n"
                                    + "    \"repayment\": null,\n"
                                    + "    \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "    \"createdTimestamp\": \"2022-05-30T16:51:43.929622\",\n"
                                    + "    \"fromProfile\": {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"username\": \"sbipcc\",\n"
                                    + "      \"password\": \"$2a$10$dZrpM5CqU.LWHOlgNnzcc.zBPGSoTOmZN5IkDyr4zLjTN66WH5uTm\",\n"
                                    + "      \"firstName\": \"SBIP\",\n"
                                    + "      \"lastName\": \"Community Clinic\",\n"
                                    + "      \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "      \"phone\": \"98760001\",\n"
                                    + "      \"userType\": \"U\",\n"
                                    + "      \"registrationDate\": \"2022-08-02T11:26:34.670542\",\n"
                                    + "      \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "    },\n"
                                    + "    \"toProfile\": null,\n"
                                    + "    \"product\": {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"profileId\": 2,\n"
                                    + "      \"name\": \"Single Patient Dialysis Machine TR-8000\",\n"
                                    + "      \"type\": \"Dialysis\",\n"
                                    + "      \"description\": \"The single patients dialysis machine can perform prescribed dialysis which adjust the dialysate conductivity in accordance with each patient. It mainly consists of Monitor/alarm part, Dialysate supply/UF control part, Extracorporeal blood circuit part, and Electrical control part.\",\n"
                                    + "      \"imageUrl\": \"http://localhost:8080/images/dialysis.jpg\",\n"
                                    + "      \"price\": 1200000,\n"
                                    + "      \"specifications\": \"Supported by the advanced technologies of TORAY, TR-8000 offers comfortable dialysis treatment to patients, easy operation to medical staff and contributes to medical development.\\n\\n- Easy operability by changing the position of external parts\\n- The position of rinse ports, couplers and bicarbonate cartridge was changed and you can operate them without squat.\\n- Casters became larger and you can move it by less power.\\nImprovement of standard functions\\n- The chamber level adjustment has been newly equipped as standard\\n- Self-test time became shorter\\nVarious new options and accessories\\n- BVM, Kt/V indicator, Bicarbonate cartridge, etc.\",\n"
                                    + "      \"createdTimestamp\": \"2022-08-02T11:26:35.243258\"\n"
                                    + "    },\n"
                                    + "    \"proposals\": [\n"
                                    + "      {\n"
                                    + "        \"id\": 1,\n"
                                    + "        \"fromProfileId\": 1,\n"
                                    + "        \"toProfileId\": 2,\n"
                                    + "        \"requestId\": 1,\n"
                                    + "        \"title\": \"TX2touch-90 POWER cobot\",\n"
                                    + "        \"type\": \"PRO\",\n"
                                    + "        \"status\": \"O\",\n"
                                    + "        \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "        \"cost\": 1000000,\n"
                                    + "        \"repayment\": null,\n"
                                    + "        \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "        \"createdTimestamp\": \"2022-05-30T16:51:43.929622\",\n"
                                    + "        \"fromProfile\": {\n"
                                    + "          \"id\": 1,\n"
                                    + "          \"username\": \"sbipcc\",\n"
                                    + "          \"password\": \"$2a$10$dZrpM5CqU.LWHOlgNnzcc.zBPGSoTOmZN5IkDyr4zLjTN66WH5uTm\",\n"
                                    + "          \"firstName\": \"SBIP\",\n"
                                    + "          \"lastName\": \"Community Clinic\",\n"
                                    + "          \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "          \"phone\": \"98760001\",\n"
                                    + "          \"userType\": \"U\",\n"
                                    + "          \"registrationDate\": \"2022-08-02T11:26:34.670542\",\n"
                                    + "          \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "        },\n"
                                    + "        \"toProfile\": null,\n"
                                    + "        \"provider\": {\n"
                                    + "          \"id\": 1,\n"
                                    + "          \"username\": \"sbipcc\",\n"
                                    + "          \"password\": \"$2a$10$dZrpM5CqU.LWHOlgNnzcc.zBPGSoTOmZN5IkDyr4zLjTN66WH5uTm\",\n"
                                    + "          \"firstName\": \"SBIP\",\n"
                                    + "          \"lastName\": \"Community Clinic\",\n"
                                    + "          \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "          \"phone\": \"98760001\",\n"
                                    + "          \"userType\": \"U\",\n"
                                    + "          \"registrationDate\": \"2022-08-02T11:26:34.670542\",\n"
                                    + "          \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "        },\n"
                                    + "        \"user\": null,\n"
                                    + "        \"open\": true,\n"
                                    + "        \"accepted\": false,\n"
                                    + "        \"rfp\": false,\n"
                                    + "        \"rff\": false,\n"
                                    + "        \"closed\": false,\n"
                                    + "        \"rejected\": false,\n"
                                    + "        \"pro\": true\n"
                                    + "      }\n"
                                    + "    ],\n"
                                    + "    \"productId\": 1,\n"
                                    + "    \"fromUser\": {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"username\": \"sbipcc\",\n"
                                    + "      \"password\": \"$2a$10$dZrpM5CqU.LWHOlgNnzcc.zBPGSoTOmZN5IkDyr4zLjTN66WH5uTm\",\n"
                                    + "      \"firstName\": \"SBIP\",\n"
                                    + "      \"lastName\": \"Community Clinic\",\n"
                                    + "      \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "      \"phone\": \"98760001\",\n"
                                    + "      \"userType\": \"U\",\n"
                                    + "      \"registrationDate\": \"2022-08-02T11:26:34.670542\",\n"
                                    + "      \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "    },\n"
                                    + "    \"toProvider\": null,\n"
                                    + "    \"open\": true,\n"
                                    + "    \"accepted\": false,\n"
                                    + "    \"rfp\": true,\n"
                                    + "    \"rff\": false,\n"
                                    + "    \"closed\": false,\n"
                                    + "    \"rejected\": false,\n"
                                    + "    \"pro\": false\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/request/rfp/id/1\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "400",
            description = "Request with <code>id</code> is not a request for proposal",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-requestForProposal-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-05T14:33:30.269776\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"InvalidRequestTypeException: Unable to process request of type=PRO\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/request/rfp/id/3\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "404",
            description = "Request with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequestForProposal.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-requestForProposal-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-05T14:32:53.0301872\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Request with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/request/rfp/id/99\",\n"
                                    + "  \"ok\": false\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> getRequestForProposal(
      @PathVariable(required = true) Integer id,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProposals,
      @RequestParam(required = false, defaultValue = "false") Boolean includeUser,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProvider,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProduct,
      HttpServletRequest request)
      throws EntityNotFoundException, RequestException {

    RequestForProposal rfp = requestService.getRequestForProposal(id);

    ResponseMessage msg;

    if (includeUser) rfp.setFromUser(getUserProfile(rfp.getFromProfileId(), request));

    if (includeProposals) {
      msg = getProposalsFor(id, includeProvider, Boolean.FALSE, request).getBody();

      if (msg.isOk()) rfp.setProposals((List<Request>) msg.getData());
    }

    if (includeProduct) {
      rfp.setProduct(getProduct(rfp.getProductId(), request));
    }

    msg = new ResponseMessage(HttpStatus.OK.value(), rfp, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets user profile by {@code id}.
   *
   * @param id the profile id
   * @param request the http request
   * @return the user profile
   * @throws GetUserProfileException any exception thrown when retrieving the user profile
   */
  protected UserProfile getUserProfile(int id, HttpServletRequest request)
      throws GetUserProfileException {
    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(getHttpHeaders(request));

    String url = urlBase + String.format(urlProfileGetById, id);

    ResponseMessageWithUserProfile msg =
        restTemplate
            .exchange(url, HttpMethod.GET, entity, ResponseMessageWithUserProfile.class)
            .getBody();

    if (msg.isOk()) return msg.getData();
    else throw new GetUserProfileException(msg.getMessage());
  }

  /**
   * Gets proposal by {@code id}.
   *
   * @param id the id of the request for proposal
   * @param includeProvider when {@code true}, includes profile of the solution provider in the
   *     returned proposal
   * @param includeUser when {@code true}, includes profile of the user in the returned proposal
   * @param request the http request
   * @return the proposal with the id if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/pro/id/{id}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve a proposal",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a proposal in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithProposal.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-proposal-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-05T13:48:14.7273714\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 3,\n"
                                    + "    \"fromProfileId\": 2,\n"
                                    + "    \"toProfileId\": 1,\n"
                                    + "    \"requestId\": 1,\n"
                                    + "    \"title\": \"TX2touch-90 POWER Cobot\",\n"
                                    + "    \"type\": \"PRO\",\n"
                                    + "    \"status\": \"O\",\n"
                                    + "    \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "    \"cost\": 3000000,\n"
                                    + "    \"repayment\": null,\n"
                                    + "    \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "    \"createdTimestamp\": \"2022-07-05T13:42:20.813729\",\n"
                                    + "    \"fromProfile\": {\n"
                                    + "      \"id\": 2,\n"
                                    + "      \"username\": \"batman\",\n"
                                    + "      \"password\": \"$2a$10$wjQyOK8ljfa7o8ekhTHRMe6kSiKXufUUXaCMib9fK6WDxEZECC3c6\",\n"
                                    + "      \"firstName\": \"bruce\",\n"
                                    + "      \"lastName\": \"wane\",\n"
                                    + "      \"email\": \"batman@gmail.com\",\n"
                                    + "      \"phone\": \"98760002\",\n"
                                    + "      \"userType\": \"S\",\n"
                                    + "      \"registrationDate\": \"2022-07-05T13:42:19.441911\",\n"
                                    + "      \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "    },\n"
                                    + "    \"toProfile\": {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"username\": \"superman\",\n"
                                    + "      \"password\": \"$2a$10$UJQTnbDXfAtmop60fjVgC.8FVi6Dkb2hN3aFjrgAHMTjWzGsUDU1K\",\n"
                                    + "      \"firstName\": \"clark\",\n"
                                    + "      \"lastName\": \"kent\",\n"
                                    + "      \"email\": \"superman@gmail.com\",\n"
                                    + "      \"phone\": \"98760001\",\n"
                                    + "      \"userType\": \"U\",\n"
                                    + "      \"registrationDate\": \"2022-07-05T13:42:19.441911\",\n"
                                    + "      \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "    },\n"
                                    + "    \"provider\": {\n"
                                    + "      \"id\": 2,\n"
                                    + "      \"username\": \"batman\",\n"
                                    + "      \"password\": \"$2a$10$wjQyOK8ljfa7o8ekhTHRMe6kSiKXufUUXaCMib9fK6WDxEZECC3c6\",\n"
                                    + "      \"firstName\": \"bruce\",\n"
                                    + "      \"lastName\": \"wane\",\n"
                                    + "      \"email\": \"batman@gmail.com\",\n"
                                    + "      \"phone\": \"98760002\",\n"
                                    + "      \"userType\": \"S\",\n"
                                    + "      \"registrationDate\": \"2022-07-05T13:42:19.441911\",\n"
                                    + "      \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "    },\n"
                                    + "    \"user\": {\n"
                                    + "      \"id\": 1,\n"
                                    + "      \"username\": \"superman\",\n"
                                    + "      \"password\": \"$2a$10$UJQTnbDXfAtmop60fjVgC.8FVi6Dkb2hN3aFjrgAHMTjWzGsUDU1K\",\n"
                                    + "      \"firstName\": \"clark\",\n"
                                    + "      \"lastName\": \"kent\",\n"
                                    + "      \"email\": \"superman@gmail.com\",\n"
                                    + "      \"phone\": \"98760001\",\n"
                                    + "      \"userType\": \"U\",\n"
                                    + "      \"registrationDate\": \"2022-07-05T13:42:19.441911\",\n"
                                    + "      \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "    },\n"
                                    + "    \"open\": true,\n"
                                    + "    \"accepted\": false,\n"
                                    + "    \"closed\": false,\n"
                                    + "    \"rejected\": false,\n"
                                    + "    \"rfp\": false,\n"
                                    + "    \"pro\": true,\n"
                                    + "    \"approved\": false,\n"
                                    + "    \"rff\": false\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/request/pro/id/3\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "400",
            description = "Request with <code>id</code> is not a proposal",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithProposal.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-proposal-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-05T13:45:36.6005978\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"InvalidRequestTypeException: Unable to process request of type=RFP\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/request/pro/id/1\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "404",
            description = "Request with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-proposal-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-05T13:49:38.3116253\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Request with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/request/pro/id/99\",\n"
                                    + "  \"ok\": false\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> getProposal(
      @PathVariable Integer id,
      @RequestParam(required = false, defaultValue = "false") Boolean includeProvider,
      @RequestParam(required = false, defaultValue = "false") Boolean includeUser,
      HttpServletRequest request)
      throws EntityNotFoundException, RequestException {

    Proposal proposal = requestService.getProposal(id);

    if (includeProvider) proposal.setProvider(getUserProfile(proposal.getFromProfileId(), request));
    if (includeUser) proposal.setUser(getUserProfile(proposal.getToProfileId(), request));

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), proposal, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets request for payment by {@code id}.
   *
   * @param id the id of the request for payment
   * @param includeFromUser when {@code true}, includes profile of the from user in the returned
   *     request for payment
   * @param includeToUser when {@code true}, includes profile of the to user in the returned request
   *     for payment
   * @param request the http request
   * @return the request for payment with the id if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/rpy/id/{id}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve a proposal",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a proposal in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithProposal.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-requestForPayment-200.json",
                            value = ""))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "400",
            description = "Request with <code>id</code> is not a proposal",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithProposal.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-requestForPayment-400.json",
                            value = ""))),
        @ApiResponse(
            responseCode = "404",
            description = "Request with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-get-requestForPayment-404.json",
                            value = "")))
      })
  public ResponseEntity<ResponseMessage> getRequestForPayment(
      @PathVariable Integer id,
      @RequestParam(required = false, defaultValue = "false") Boolean includeFromUser,
      @RequestParam(required = false, defaultValue = "false") Boolean includeToUser,
      HttpServletRequest request)
      throws EntityNotFoundException, RequestException {

    RequestForPayment rpy = requestService.getRequestForPayment(id);

    if (includeFromUser) rpy.setFromProfile(getUserProfile(rpy.getFromProfileId(), request));
    if (includeToUser) rpy.setToProfile(getUserProfile(rpy.getToProfileId(), request));

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), rpy, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Creates a request.
   *
   * @param request the request
   * @param result the validation result
   * @param httpRequest the http request
   * @return the {@code ResponseMessage} containing the request created with the auto-generated
   *     {@code id}
   */
  @PostMapping("/")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Creates a new request",
      tags = {"Request"},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description =
                  "Requires a request json object, the <code>id</code> field (if specified) will be ignored",
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = Request.class),
                      examples =
                          @ExampleObject(
                              externalValue =
                                  "http://localhost:8080/swagger/request/request-create-req.json",
                              value =
                                  "{\n"
                                      + "  \"fromProfileId\": 1,\n"
                                      + "  \"toProfileId\": 2,\n"
                                      + "  \"requestId\": 3,\n"
                                      + "  \"title\": \"Robot for Spot Welding\",\n"
                                      + "  \"type\": \"RFP\",\n"
                                      + "  \"status\": \"O\",\n"
                                      + "  \"description\": \"Need an articulated robot that is connected to the base with a twisting joint. Will use it for spot welding in metal casting.\",\n"
                                      + "  \"cost\": 1000000,\n"
                                      + "  \"specifications\": \"Programmable through ACE software and eV+ language, or through the familiar IEC 61131-3 when using ePLC Connect. Good repeatability for precision assembly. High Payload for using tools for screw-driving and adhesive application. Minimum footprint with separate controller. Robot with integral power and signal cables. Reach 500 mm, Maximum Payload 5 kg, Weight 29 kg\",\n"
                                      + "  \"createdTimestamp\": \"2022-05-30T16:51:43.9296215\"\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Returns request (with the auto-generated <code>id</code>) in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithRequest.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/request/request-create-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-08-02T11:36:25.6632435\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"\",\n"
                                  + "  \"data\": {\n"
                                  + "    \"id\": 1,\n"
                                  + "    \"fromProfileId\": 1,\n"
                                  + "    \"toProfileId\": 2,\n"
                                  + "    \"requestId\": 1,\n"
                                  + "    \"title\": \"TX2touch-90 POWER cobot\",\n"
                                  + "    \"type\": \"RFP\",\n"
                                  + "    \"status\": \"O\",\n"
                                  + "    \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                  + "    \"cost\": 1000000,\n"
                                  + "    \"repayment\": null,\n"
                                  + "    \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                  + "    \"createdTimestamp\": \"2022-05-30T16:51:43.9296215\",\n"
                                  + "    \"fromProfile\": null,\n"
                                  + "    \"toProfile\": null,\n"
                                  + "    \"open\": true,\n"
                                  + "    \"accepted\": false,\n"
                                  + "    \"rfp\": true,\n"
                                  + "    \"rff\": false,\n"
                                  + "    \"closed\": false,\n"
                                  + "    \"rejected\": false,\n"
                                  + "    \"pro\": false\n"
                                  + "  },\n"
                                  + "  \"path\": \"/api/v1/request/\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to create request due to validation reasons.  Refer to schema of Request for details.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/request/request-create-400-title.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-12T17:36:15.0759733\",\n"
                                  + "  \"status\": 400,\n"
                                  + "  \"message\": \"javax.validation.ConstraintViolationException: create.request.fromProfileId: fromProfileId must not be null, create.request.fromProfileId: fromProfileId must not be null\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/request/\",\n"
                                  + "  \"ok\": false\n"
                                  + "}"))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> create(
      @Valid @RequestBody Request request, BindingResult result, HttpServletRequest httpRequest) {

    String uri = httpRequest.getRequestURI();

    Request created = requestService.createRequest(request);

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), created, uri);

    return ResponseEntity.ok(msg);
  }

  public Contract createContract(Contract contract, HttpServletRequest httpRequest)
      throws CreateContractException, JsonProcessingException {

    String jsonContract = objectMapper.writeValueAsString(contract);

    // Duplicate authorization headers from request
    HttpEntity entity = new HttpEntity<String>(jsonContract, getHttpHeaders(httpRequest));

    ResponseMessageWithContract msg =
        restTemplate
            .exchange(
                urlBase + urlContractCreate,
                HttpMethod.POST,
                entity,
                ResponseMessageWithContract.class)
            .getBody();

    if (msg.isOk()) return msg.getData();
    else throw new CreateContractException(msg.getMessage());
  }

  /**
   * Creates a request for funding.
   *
   * @param request the request for funding
   * @param result the validation result
   * @param httpRequest the http request
   * @return the {@code ResponseMessage} containing the request for funding created with the
   *     auto-generated {@code id}
   */
  @PostMapping("/rff/")
  @PreAuthorize("hasAuthority('U') or hasAuthority('A')")
  @Operation(
      summary = "Creates a new request for funding",
      tags = {"Request"},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description =
                  "Requires a request for funding json object, the <code>id</code> field (if specified) will be ignored",
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = Request.class),
                      examples =
                          @ExampleObject(
                              externalValue =
                                  "http://localhost:8080/swagger/request/request-create-requestForFunding-req.json",
                              value =
                                  "{\n"
                                      + "  \"fromProfileId\": 1,\n"
                                      + "  \"toProfileId\": 0,\n"
                                      + "  \"requestId\": 2,\n"
                                      + "  \"title\": \"TX2touch-90 POWER cobot\",\n"
                                      + "  \"type\": \"RFF\",\n"
                                      + "  \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                      + "  \"cost\": 1300000,\n"
                                      + "  \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                      + "  \"repayment\": 1500000,\n"
                                      + "  \"status\": \"O\",\n"
                                      + "  \"createdTimestamp\": \"2022-07-06T20:48:28.4621798\"\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Returns request (with the auto-generated <code>id</code>) in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithRequest.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/request/request-create-requestForFunding-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-07-13T21:42:30.3789511\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"\",\n"
                                  + "  \"data\": [\n"
                                  + "    {\n"
                                  + "      \"id\": 6,\n"
                                  + "      \"fromProfileId\": 1,\n"
                                  + "      \"toProfileId\": 0,\n"
                                  + "      \"requestId\": 2,\n"
                                  + "      \"title\": \"TX2touch-90 POWER cobot\",\n"
                                  + "      \"type\": \"RFF\",\n"
                                  + "      \"status\": \"NF\",\n"
                                  + "      \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                  + "      \"cost\": 1300000,\n"
                                  + "      \"repayment\": 1500000,\n"
                                  + "      \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                  + "      \"createdTimestamp\": \"2022-07-06T20:48:28.4621798\",\n"
                                  + "      \"fromProfile\": null,\n"
                                  + "      \"toProfile\": null,\n"
                                  + "      \"open\": false,\n"
                                  + "      \"accepted\": false,\n"
                                  + "      \"closed\": false,\n"
                                  + "      \"rejected\": false,\n"
                                  + "      \"rfp\": false,\n"
                                  + "      \"pro\": false,\n"
                                  + "      \"rff\": true\n"
                                  + "    },\n"
                                  + "    {\n"
                                  + "      \"id\": 2,\n"
                                  + "      \"requestId\": 6,\n"
                                  + "      \"walletId\": \"GAyCywe7wYQ49XA92BrDBVvj2CMKeEGMmGjseQR3yFua\",\n"
                                  + "      \"targetAmount\": 1300000,\n"
                                  + "      \"repaymentAmount\": 1500000,\n"
                                  + "      \"status\": \"NF\",\n"
                                  + "      \"createdTimestamp\": \"2022-07-13T21:42:30.3241232\",\n"
                                  + "      \"fundings\": [],\n"
                                  + "      \"outstandingAmount\": 1300000,\n"
                                  + "      \"yield\": 15,\n"
                                  + "      \"raisedAmount\": 0\n"
                                  + "    }\n"
                                  + "  ],\n"
                                  + "  \"path\": \"/api/v1/request/rff/\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to create request due to validation reasons.  Refer to schema of Request for details.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/request/request-create-requestForFunding-400.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-07-13T21:46:04.4759978\",\n"
                                  + "  \"status\": 400,\n"
                                  + "  \"message\": \"ConstraintViolationException: createRequestForFunding.request.cost: cost must be at least 1, createRequestForFunding.request.cost: cost must be positive\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/request/rff/\",\n"
                                  + "  \"ok\": false\n"
                                  + "}"))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> createRequestForFunding(
      @Valid @RequestBody Request request, BindingResult result, HttpServletRequest httpRequest)
      throws CreateContractException, EntityNotFoundException, JsonProcessingException {

    String uri = httpRequest.getRequestURI();

    Request created = requestService.createRequest(request);

    // Update status of the proposal
    updateStatus(created.getRequestId(), Request.STATUS_FUNDING_REQUESTED, httpRequest);

    // Create a contract instance based on the request for funding
    Contract contract = new Contract(request);
    contract = createContract(contract, httpRequest);

    // Return both the request for funding and the contract
    Object[] array = new Object[] {created, contract};

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), array, uri);

    return ResponseEntity.ok(msg);
  }

  /**
   * Updates a request.
   *
   * @param request the request
   * @param result the validation result
   * @param httpRequest the http request
   * @return the {@code ResponseMessage} containing the request updated
   */
  @PutMapping("/")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Updates a request",
      tags = {"Request"},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description =
                  "Requires a request json object, fields will be updated based on the primary key <code>id</code> ",
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              externalValue =
                                  "http://localhost:8080/swagger/request/request-update-req.json",
                              value =
                                  "{\n"
                                      + "  \"id\": 1,\n"
                                      + "  \"fromProfileId\": 8,\n"
                                      + "  \"toProfileId\": 2,\n"
                                      + "  \"requestId\": 999,\n"
                                      + "  \"title\": \"TX2touch-90 POWER cobot\",\n"
                                      + "  \"type\": \"RFP\",\n"
                                      + "  \"status\": \"O\",\n"
                                      + "  \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                      + "  \"cost\": 2000000,\n"
                                      + "  \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                      + "  \"createdTimestamp\": \"2022-05-30T16:51:43.9296215\"\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns updated request in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequest.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-update-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-14T13:54:14.3332876\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 1,\n"
                                    + "    \"fromProfileId\": 8,\n"
                                    + "    \"toProfileId\": 2,\n"
                                    + "    \"requestId\": 999,\n"
                                    + "    \"title\": \"TX2touch-90 POWER cobot\",\n"
                                    + "    \"type\": \"RFP\",\n"
                                    + "    \"status\": \"O\",\n"
                                    + "    \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "    \"cost\": 2000000,\n"
                                    + "    \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "    \"createdTimestamp\": \"2022-05-30T16:51:43.9296215\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/request/\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to update request due to validation reasons. Refer to schema of Request for details.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-update-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-12T18:02:39.2763604\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"javax.validation.ConstraintViolationException: update.request.type: type must contain between 1 to 10 characters, update.request.type: type must not be blank\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/request/\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Request with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-update-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-12T18:03:50.4649064\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"Request with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/request/\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
      })
  public ResponseEntity<ResponseMessage> update(
      @Valid @RequestBody Request request, BindingResult result, HttpServletRequest httpRequest)
      throws EntityNotFoundException {

    String uri = httpRequest.getRequestURI();

    Request updated = requestService.updateRequest(request);

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), updated, uri);

    return ResponseEntity.ok(msg);
  }

  /**
   * Updates a request status.
   *
   * @param id the request id
   * @param status the request status
   * @param httpRequest the http request
   * @return the {@code ResponseMessage} with updated message
   */
  @PatchMapping("/id/{id}/status/{status}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Updates the status of a request",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns updated status confirmation",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequest.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-update-status-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-15T11:51:59.3763999\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"Request with id=1, status=C updated\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/request/id/1/status/C\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to update request due to validation reasons. Refer to schema of Request for details.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-update-status-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-15T12:17:50.1129511\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"javax.validation.ConstraintViolationException: updateStatus.id: must be greater than 0\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/request/id/-9/status/C\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Request with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-update-status-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-15T11:50:59.1550764\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"Request with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/request/id/99/status/C\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
      })
  public ResponseEntity<ResponseMessage> updateStatus(
      @PathVariable("id") @Positive Integer id,
      @PathVariable("status") @NotBlank String status,
      HttpServletRequest httpRequest)
      throws EntityNotFoundException {

    String uri = httpRequest.getRequestURI();

    requestService.updateRequestStatus(id, status);

    String reason = "Request with id=%d, status=%s updated";
    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), String.format(reason, id, status), uri);

    return ResponseEntity.ok(msg);
  }

  /**
   * Accepts a solution provider's proposal.
   *
   * @param id the id of the proposal to accept
   * @param httpRequest the http request
   * @return the {@code ResponseMessage} with updated message
   */
  @PatchMapping("/pro/accept/{id}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('A')")
  @Operation(
      summary = "Accepts the proposal from a solution provider",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Returns the <code>ResponseMessage</code> with confirmation of acceptance",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-proposal-accept-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-16T14:48:07.2264917\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"Proposal with id=1, status=ACC updated, proposals with ids=[2], status=REJ updated.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/request/pro/accept/id/1\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to accept proposal due to validation reasons. Refer to schema of Request for details.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-update-accept-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-16T14:53:39.4793231\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"InvalidRequestTypeException: Unable to process request of type=RFS\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/request/accept/id/3\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Request with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-update-accept-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-16T14:38:13.5676166\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"EntityNotFoundException: Request with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/request/accept/id/99\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
      })
  public ResponseEntity<ResponseMessage> acceptProposal(
      @PathVariable("id") @Positive Integer id, HttpServletRequest httpRequest)
      throws EntityNotFoundException, RequestException {

    List<Integer> rejectedIds = requestService.acceptProposal(id);

    String reason =
        "Proposal with id=%d, status=%s updated, proposals with ids=%s, status=%s updated.";

    ResponseMessage msg =
        new ResponseMessage(
            HttpStatus.OK.value(),
            String.format(
                reason,
                id,
                Request.STATUS_ACCEPTED,
                rejectedIds.toString(),
                Request.STATUS_REJECTED),
            httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Updates a request repayment amount.
   *
   * @param id the request id
   * @param repayment the repayment amount after the request completes
   * @param httpRequest the http request
   * @return the {@code ResponseMessage} with updated message
   */
  @PatchMapping("/id/{id}/repayment/{repayment}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Updates the repayment amount of a request",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns updated request in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithRequest.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-update-repayment-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-15T12:20:59.4314107\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"Request with id=1, repayment=888 updated\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/request/id/1/repayment/888\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to update request due to validation reasons. Refer to schema of Request for details.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-update-repayment-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-15T12:23:36.2521651\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"javax.validation.ConstraintViolationException: updateRepayment.repayment: must be greater than 0\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/request/id/99/repayment/-99\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Request with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/request/request-update-repayment-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-15T12:22:09.9824173\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"Request with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/request/id/99/repayment/888\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
      })
  public ResponseEntity<ResponseMessage> updateRepayment(
      @PathVariable @NotNull @Positive Integer id,
      @PathVariable @NotNull @Positive Long repayment,
      HttpServletRequest httpRequest)
      throws EntityNotFoundException {

    String uri = httpRequest.getRequestURI();

    requestService.updateRequestRepayment(id, repayment);

    String reason = "Request with id=%d, repayment=%d updated";

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), String.format(reason, id, repayment), uri);

    return ResponseEntity.ok(msg);
  }

  /**
   * Removes a request by {@code id}.
   *
   * @param id the request id
   * @param httpRequest the http request
   * @return HTTP status NOT FOUND if the request with {@code id} is not found. If the delete is
   *     successful, the HTTP status OK.
   */
  @DeleteMapping("/id/{id}")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Remove request by id",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns deleted request in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithRequest.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/request/request-delete-id-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-14T14:28:29.549509\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"Request with id=4 deleted.\",\n"
                                  + "  \"data\": {\n"
                                  + "    \"id\": 4,\n"
                                  + "    \"fromProfileId\": 8,\n"
                                  + "    \"toProfileId\": 2,\n"
                                  + "    \"requestId\": null,\n"
                                  + "    \"title\": \"TX2touch-90 POWER cobot\",\n"
                                  + "    \"type\": \"RFP\",\n"
                                  + "    \"status\": \"O\",\n"
                                  + "    \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                  + "    \"cost\": 1000000,\n"
                                  + "    \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                  + "    \"createdTimestamp\": \"2022-05-30T16:51:43.929622\"\n"
                                  + "  },\n"
                                  + "  \"path\": \"/api/v1/request/id/4\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Request with <code>id</code> not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/request/request-delete-id-404.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-12T18:49:43.4401725\",\n"
                                  + "  \"status\": 404,\n"
                                  + "  \"message\": \"Request with id=99 NOT found.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/request/id/99\",\n"
                                  + "  \"ok\": false\n"
                                  + "}"))
            })
      })
  public ResponseEntity<ResponseMessage> removeById(
      @PathVariable int id, HttpServletRequest httpRequest) throws EntityNotFoundException {

    requestService.deleteRequestById(id);

    String reason = "Request with id=%d deleted.";
    ResponseMessage msg =
        new ResponseMessage(
            HttpStatus.OK.value(), String.format(reason, id), httpRequest.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Removes requests by {@code fromProfileId}.
   *
   * @param profileId the from profile id
   * @param request the http request
   * @return HTTP status NOT FOUND if requests with {@code fromProfileId} are not found. If the
   *     delete is successful, the HTTP status OK.
   */
  @DeleteMapping("/fromProfileId/{profileId}")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Remove requests by from profile id",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns deleted requests in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithRequests.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/request/request-delete-fromProfileId-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-14T14:34:12.5449562\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"Requests from profile id=1 deleted.\",\n"
                                  + "  \"data\": [\n"
                                  + "    {\n"
                                  + "      \"id\": 2,\n"
                                  + "      \"fromProfileId\": 1,\n"
                                  + "      \"toProfileId\": 2,\n"
                                  + "      \"requestId\": null,\n"
                                  + "      \"title\": \"Epson VT6L All-in-One 6-Axis Robot\",\n"
                                  + "      \"type\": \"RFA\",\n"
                                  + "      \"status\": \"C\",\n"
                                  + "      \"description\": \"Features Slimline design perfect for factories with limited floor space and compact wrist pitch that enables robot easy access to hard-to-reach areas. Ideal for load/ unload, packaging or parts assembly applications. Cleanroom (ISO4) and Protected (IP67) models available.\",\n"
                                  + "      \"cost\": 2000000,\n"
                                  + "      \"specifications\": \"VT6L offers a reach up to 900 mm and a payload up to 6 kg. A feature-packed performer, it includes a built-in controller, plus simplified cabling with a hollow end-of-arm design – all at a remarkably low cost, in a compact, SlimLine structure. The VT6L offers 110 V and 220 V power and installs in minutes.\",\n"
                                  + "      \"createdTimestamp\": \"2022-06-14T11:51:14.458968\"\n"
                                  + "    }\n"
                                  + "  ],\n"
                                  + "  \"path\": \"/api/v1/request/fromProfileId/1\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Requests from <code>fromProfileId</code> not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/request/request-delete-fromProfileId-404.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-12T18:58:44.1485155\",\n"
                                  + "  \"status\": 404,\n"
                                  + "  \"message\": \"No requests from profile id=99 found.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/request/fromProfileId/99\",\n"
                                  + "  \"ok\": false\n"
                                  + "}"))
            })
      })
  public ResponseEntity<ResponseMessage> removeByFromProfileId(
      @PathVariable int profileId, HttpServletRequest request) {

    String uri = request.getRequestURI();
    String reason;
    ResponseMessage msg;

    Iterable<Request> requests = requestService.getRequestsByFromProfileId(profileId);

    if (requests.iterator().hasNext()) {

      List<Request> deleted = requestService.deleteRequestsByFromProfileId(profileId);

      reason = "Requests from profile id=%d deleted.";
      msg =
          new ResponseMessage(
              HttpStatus.OK.value(), String.format(reason, profileId), deleted, uri);

      return ResponseEntity.ok(msg);
    } else {
      reason = "No requests from profile id=%d found.";

      msg =
          new ResponseMessage(HttpStatus.NOT_FOUND.value(), String.format(reason, profileId), uri);

      return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Removes requests by {@code toProfileId}.
   *
   * @param profileId the to profile id
   * @param request the http request
   * @return HTTP status NOT FOUND if requests with {@code toProfileId} are not found. If the delete
   *     is successful, the HTTP status OK.
   */
  @DeleteMapping("/toProfileId/{profileId}")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Remove requests by to profile id",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns deleted requests in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithRequests.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/request/request-delete-toProfileId-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-14T14:38:47.7791017\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"Requests to profile id=2 deleted.\",\n"
                                  + "  \"data\": [\n"
                                  + "    {\n"
                                  + "      \"id\": 1,\n"
                                  + "      \"fromProfileId\": 8,\n"
                                  + "      \"toProfileId\": 2,\n"
                                  + "      \"requestId\": 999,\n"
                                  + "      \"title\": \"TX2touch-90 POWER cobot\",\n"
                                  + "      \"type\": \"RFP\",\n"
                                  + "      \"status\": \"O\",\n"
                                  + "      \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                  + "      \"cost\": 2000000,\n"
                                  + "      \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                  + "      \"createdTimestamp\": \"2022-05-30T16:51:43.929622\"\n"
                                  + "    },\n"
                                  + "    {\n"
                                  + "      \"id\": 3,\n"
                                  + "      \"fromProfileId\": 8,\n"
                                  + "      \"toProfileId\": 2,\n"
                                  + "      \"requestId\": 1,\n"
                                  + "      \"title\": \"TX2touch-90 POWER cobot\",\n"
                                  + "      \"type\": \"RFA\",\n"
                                  + "      \"status\": \"O\",\n"
                                  + "      \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                  + "      \"cost\": 3000000,\n"
                                  + "      \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                  + "      \"createdTimestamp\": \"2022-06-14T11:51:14.458968\"\n"
                                  + "    }\n"
                                  + "  ],\n"
                                  + "  \"path\": \"/api/v1/request/toProfileId/2\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Requests from <code>toProfileId</code> not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/request/request-delete-toProfileId-404.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-12T21:12:45.4255116\",\n"
                                  + "  \"status\": 404,\n"
                                  + "  \"message\": \"No requests to profile id=99 found.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/request/toProfileId/99\",\n"
                                  + "  \"ok\": false\n"
                                  + "}"))
            })
      })
  public ResponseEntity<ResponseMessage> removeByToProfileId(
      @PathVariable int profileId, HttpServletRequest request) {

    String uri = request.getRequestURI();
    String reason;
    ResponseMessage msg;

    Iterable<Request> requests = requestService.getRequestsByToProfileId(profileId);

    if (requests.iterator().hasNext()) {

      List<Request> deleted = requestService.deleteRequestsByToProfileId(profileId);

      reason = "Requests with to profile id=%d deleted.";
      msg =
          new ResponseMessage(
              HttpStatus.OK.value(), String.format(reason, profileId), deleted, uri);

      return ResponseEntity.ok(msg);
    } else {
      reason = "No requests with to profile id=%d found.";

      msg =
          new ResponseMessage(HttpStatus.NOT_FOUND.value(), String.format(reason, profileId), uri);

      return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Removes requests with {@code profileId}.
   *
   * @param profileId the profile id
   * @param request the http request
   * @return HTTP status NOT FOUND if requests with {@code profileId} are not found. If the delete
   *     is successful, the HTTP status OK.
   */
  @DeleteMapping("/profileId/{profileId}")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Remove requests with profile id",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Requests with profile id deleted",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithRequests.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/request/request-delete-profileId-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-12T21:37:10.1526965\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"Requests with profile id=1 deleted.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/request/profileId/1\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Requests from <code>toProfileId</code> not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/request/request-delete-profileId-404.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-12T21:37:54.89466\",\n"
                                  + "  \"status\": 404,\n"
                                  + "  \"message\": \"No requests with profile id=99 found.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/request/profileId/99\",\n"
                                  + "  \"ok\": false\n"
                                  + "}"))
            })
      })
  public ResponseEntity<ResponseMessage> removeByProfileId(
      @PathVariable int profileId, HttpServletRequest request) {

    String uri = request.getRequestURI();
    String reason;
    ResponseMessage msg;

    Iterable<Request> requests = requestService.getRequestsByProfileId(profileId);

    if (requests.iterator().hasNext()) {

      requestService.deleteRequestsByProfileId(profileId);

      reason = "Requests with profile id=%d deleted.";
      msg = new ResponseMessage(HttpStatus.OK.value(), String.format(reason, profileId), uri);

      return ResponseEntity.ok(msg);
    } else {
      reason = "No requests with profile id=%d found.";

      msg =
          new ResponseMessage(HttpStatus.NOT_FOUND.value(), String.format(reason, profileId), uri);

      return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Removes all requests.
   *
   * @param request the http request
   * @return the HTTP status OK is successful
   */
  @DeleteMapping("/")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Removes all requests",
      tags = {"Request"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "All requests deleted",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/request/request-delete-all-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-12T21:42:53.697333\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"All requests deleted.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/request/\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> removeAll(HttpServletRequest request) {

    String uri = request.getRequestURI();

    requestService.deleteAllRequests();

    String reason = "All requests deleted.";

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), reason, uri);

    return ResponseEntity.ok(msg);
  }
}
