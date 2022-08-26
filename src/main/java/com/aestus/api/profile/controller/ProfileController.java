package com.aestus.api.profile.controller;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.common.util.JWTUtils;
import com.aestus.api.profile.model.swagger.ResponseMessageWithUserProfile;
import com.aestus.api.profile.model.swagger.ResponseMessageWithUserProfiles;
import com.aestus.api.profile.model.UserProfile;
import com.aestus.api.profile.service.ProfileService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static com.aestus.api.common.controller.CommonController.getBadRequestResponse;

/** Provides the user profile controller functionality. */
@Slf4j
@RestController
@RequestMapping("api/v1/profile")
public class ProfileController {
  @Autowired private ProfileService profileService;
  @Autowired private RestTemplate restTemplate;
  @Autowired private BCryptPasswordEncoder passwordEncoder;
  @Autowired private JWTUtils jwtUtils;

  @Value("${com.aestus.base.url}")
  private final String urlBase = null;

  @Value("${com.aestus.blockchain.get.wallet.address.url}")
  private final String BlockchainGetWalletAddress = null;

  /**
   * Pinging the controller.
   *
   * @param request the request
   * @return the ping returned message in the {@code ResponseEntity} container object
   */
  @GetMapping("/ping")
  @Operation(
      summary = "Ping test",
      tags = {"User Profile"},
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
                              "http://localhost:8080/swagger/profile/profile-ping-200.json",
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

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets all user profiles.
   *
   * @param request the http request
   * @return all user profiles
   */
  @GetMapping("/")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Retrieve all user profiles",
      tags = {"User Profile"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns all user profiles in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithUserProfiles.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/profile/profile-get-all-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-05-23T12:02:41.9514056\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"\",\n"
                                  + "  \"data\": [\n"
                                  + "    {\n"
                                  + "      \"id\": 1,\n"
                                  + "      \"username\": \"superman\",\n"
                                  + "      \"password\": \"$2a$10$x.mpVCxlINrOXQlxejrsUeEf4cdZFIim1tt9Ewl4./6esw0dMctIe\",\n"
                                  + "      \"firstName\": \"clark\",\n"
                                  + "      \"lastName\": \"kent\",\n"
                                  + "      \"email\": \"superman@gmail.com\",\n"
                                  + "      \"phone\": \"98760001\",\n"
                                  + "      \"userType\": \"U\",\n"
                                  + "      \"registrationDate\": \"2022-05-23T11:42:07.891166\",\n"
                                  + "      \"walletId\": \"0x12340001\"\n"
                                  + "    },\n"
                                  + "    {\n"
                                  + "      \"id\": 2,\n"
                                  + "      \"username\": \"batman\",\n"
                                  + "      \"password\": \"$2a$10$905pmsxf0ewjnCsMkVVJq..bZrxqmwcSIqp24cVdK4kgzSrjbVhi2\",\n"
                                  + "      \"firstName\": \"bruce\",\n"
                                  + "      \"lastName\": \"wane\",\n"
                                  + "      \"email\": \"batman@gmail.com\",\n"
                                  + "      \"phone\": \"98760002\",\n"
                                  + "      \"userType\": \"S\",\n"
                                  + "      \"registrationDate\": \"2022-05-23T11:42:07.891166\",\n"
                                  + "      \"walletId\": \"0x12340002\"\n"
                                  + "    },\n"
                                  + "    {\n"
                                  + "      \"id\": 3,\n"
                                  + "      \"username\": \"ironman\",\n"
                                  + "      \"password\": \"$2a$10$okbGBYzX61ZyvTtQhEU8ue3ht1HBjn51HdCBdhbccgGKklYYk8fxG\",\n"
                                  + "      \"firstName\": \"tony\",\n"
                                  + "      \"lastName\": \"starks\",\n"
                                  + "      \"email\": \"ironman@gmail.com\",\n"
                                  + "      \"phone\": \"98760003\",\n"
                                  + "      \"userType\": \"I\",\n"
                                  + "      \"registrationDate\": \"2022-05-23T11:42:07.891166\",\n"
                                  + "      \"walletId\": \"0x12340003\"\n"
                                  + "    },\n"
                                  + "    {\n"
                                  + "      \"id\": 4,\n"
                                  + "      \"username\": \"spiderman\",\n"
                                  + "      \"password\": \"$2a$10$C3p80vNZC.vuZtyUmR5wRertmoB4KxOpElWvTEjLABPvBXEv/03/.\",\n"
                                  + "      \"firstName\": \"peter\",\n"
                                  + "      \"lastName\": \"parker\",\n"
                                  + "      \"email\": \"spiderman@gmail.com\",\n"
                                  + "      \"phone\": \"98760004\",\n"
                                  + "      \"userType\": \"D\",\n"
                                  + "      \"registrationDate\": \"2022-05-23T11:42:07.891166\",\n"
                                  + "      \"walletId\": \"0x12340004\"\n"
                                  + "    },\n"
                                  + "    {\n"
                                  + "      \"id\": 5,\n"
                                  + "      \"username\": \"drstrange\",\n"
                                  + "      \"password\": \"$2a$10$5OX2.dLRF1lnt6nfcB8Aae2wRMjrgF750ao0V1QY5.sT7PNM4qYci\",\n"
                                  + "      \"firstName\": \"stephen\",\n"
                                  + "      \"lastName\": \"strange\",\n"
                                  + "      \"email\": \"drstrange@gmail.com\",\n"
                                  + "      \"phone\": \"98760005\",\n"
                                  + "      \"userType\": \"A\",\n"
                                  + "      \"registrationDate\": \"2022-05-23T11:42:07.891166\",\n"
                                  + "      \"walletId\": \"0x12340005\"\n"
                                  + "    }\n"
                                  + "  ],\n"
                                  + "  \"path\": \"/api/v1/profile/\"\n"
                                  + "}"))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getAll(HttpServletRequest request) {

    Iterable<UserProfile> profiles = profileService.getAllProfiles();

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), profiles, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets {@code walletId} by {@code userType}.
   *
   * @param request the http request
   * @param userType the user type
   * @return all {@code walletId} by {@code userType}
   */
  @GetMapping("/walletId/userType/{userType}")
  @PreAuthorize("hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve wallet ids by user type",
      tags = {"User Profile"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns wallet ids in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithUserProfiles.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/profile/profile-get-all-walletIds-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-08-22T16:25:38.0494338\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"\",\n"
                                  + "  \"data\": [\n"
                                  + "    \"SXLRdrywXBntChoDLPjEF1KDQH95eu5EvkA4Uge1hjU\",\n"
                                  + "    \"5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK\"\n"
                                  + "  ],\n"
                                  + "  \"path\": \"/api/v1/profile/walletId/userType/S\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getWalletIds(
      @PathVariable String userType, HttpServletRequest request) {

    Iterable<UserProfile> profiles = profileService.getProfilesByUserType(userType);

    List<String> walletIds =
        StreamSupport.stream(profiles.spliterator(), false)
            .map(UserProfile::getWalletId)
            .collect(Collectors.toList());

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), walletIds, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets user profile by id.
   *
   * @param id the id
   * @param request the http request
   * @return the user profile with the id if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/id/{id}")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve user profile by id",
      tags = {"User Profile"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a user profile in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithUserProfile.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-get-id-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-05-23T16:06:11.0307498\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 3,\n"
                                    + "    \"username\": \"ironman\",\n"
                                    + "    \"password\": \"$2a$10$fp4fhzObKPfQwuEW0BrpfuTe8kVnfuUU9wFZzIPwbyUxa7as75q7m\",\n"
                                    + "    \"firstName\": \"tony\",\n"
                                    + "    \"lastName\": \"starks\",\n"
                                    + "    \"email\": \"ironman@gmail.com\",\n"
                                    + "    \"phone\": \"98760003\",\n"
                                    + "    \"userType\": \"I\",\n"
                                    + "    \"registrationDate\": \"2022-05-23T16:03:50.195722\",\n"
                                    + "    \"walletId\": \"0x12340003\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/profile/id/3\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "User profile with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-get-id-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-05-23T16:14:19.5132308\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"User profile with id=33 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/profile/id/33\"\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> getById(
      @PathVariable Integer id, HttpServletRequest request) {

    Optional<UserProfile> optProfile = profileService.getProfileById(id);

    ResponseMessage msg;

    if (optProfile.isPresent()) {

      msg = new ResponseMessage(HttpStatus.OK.value(), optProfile.get(), request.getRequestURI());

      return ResponseEntity.ok(msg);
    } else {

      String reason = "User profile with id=%d NOT found.";

      msg =
          new ResponseMessage(
              HttpStatus.NOT_FOUND.value(), String.format(reason, id), request.getRequestURI());

      return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Gets user profile by username.
   *
   * @param username the username
   * @param request the http request
   * @return the user profile with the username if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/username/{username}")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve user profile by username",
      tags = {"User Profile"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a user profile in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithUserProfile.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-get-username-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-05-23T16:06:11.0307498\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 3,\n"
                                    + "    \"username\": \"ironman\",\n"
                                    + "    \"password\": \"$2a$10$fp4fhzObKPfQwuEW0BrpfuTe8kVnfuUU9wFZzIPwbyUxa7as75q7m\",\n"
                                    + "    \"firstName\": \"tony\",\n"
                                    + "    \"lastName\": \"starks\",\n"
                                    + "    \"email\": \"ironman@gmail.com\",\n"
                                    + "    \"phone\": \"98760003\",\n"
                                    + "    \"userType\": \"I\",\n"
                                    + "    \"registrationDate\": \"2022-05-23T16:03:50.195722\",\n"
                                    + "    \"walletId\": \"0x12340003\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/profile/id/3\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "404",
            description = "User profile with <code>username</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-get-username-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-05-23T16:16:09.5031058\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"User profile with username=thor NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/profile/username/thor\"\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByUsername(
      @PathVariable String username, HttpServletRequest request) {
    Optional<UserProfile> optProfile = profileService.getProfileByUsername(username);

    ResponseMessage msg;

    if (optProfile.isPresent()) {

      msg = new ResponseMessage(HttpStatus.OK.value(), optProfile.get(), request.getRequestURI());

      return ResponseEntity.ok(msg);
    } else {

      String reason = "User profile with username=%s NOT found.";

      msg =
          new ResponseMessage(
              HttpStatus.NOT_FOUND.value(),
              String.format(reason, username),
              request.getRequestURI());

      return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Gets user profile by email address.
   *
   * @param email the email address
   * @param request the http request
   * @return the user profile with the email address if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/email/{email}")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve user profile by email address",
      tags = {"User Profile"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a user profile in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithUserProfile.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-get-email-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-07T23:37:55.8294275\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 2,\n"
                                    + "    \"username\": \"batman\",\n"
                                    + "    \"password\": \"$2a$10$rjftpvz/R2KAoIY6V2ryc.LrjI6GXc5buLgW2dAPvFGwkQf8gnSAy\",\n"
                                    + "    \"firstName\": \"bruce\",\n"
                                    + "    \"lastName\": \"wane\",\n"
                                    + "    \"email\": \"batman@gmail.com\",\n"
                                    + "    \"phone\": \"98760002\",\n"
                                    + "    \"userType\": \"S\",\n"
                                    + "    \"registrationDate\": \"2022-06-07T23:33:45.074602\",\n"
                                    + "    \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/profile/email/batman@gmail.com\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "404",
            description = "User profile with <code>email</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-get-email-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-07T23:34:50.0478077\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"User profile with email=thor@gmail.com NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/profile/email/thor@gmail.com\"\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByEmail(
      @PathVariable String email, HttpServletRequest request) {
    Optional<UserProfile> optProfile = profileService.getProfileByEmail(email);

    ResponseMessage msg;

    if (optProfile.isPresent()) {

      msg = new ResponseMessage(HttpStatus.OK.value(), optProfile.get(), request.getRequestURI());

      return ResponseEntity.ok(msg);
    } else {

      String reason = "User profile with email=%s NOT found.";

      msg =
          new ResponseMessage(
              HttpStatus.NOT_FOUND.value(), String.format(reason, email), request.getRequestURI());

      return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Gets user profile by jwt token.
   *
   * @param request the http request
   * @return the user profile with username embedded in the jwt if available, if not, the NOT FOUND
   *     HTTP status
   */
  @GetMapping("/token")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve user profile using the jwt token",
      tags = {"User Profile"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a user profile in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithUserProfile.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-get-token-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-02T12:04:26.1662345\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 5,\n"
                                    + "    \"username\": \"drstrange\",\n"
                                    + "    \"password\": \"$2a$10$2zUA9WTsH0bN9VY5lZ7aUObxoYXgZ.QNF0X07KRIq1cK3aKxzTBii\",\n"
                                    + "    \"firstName\": \"stephen\",\n"
                                    + "    \"lastName\": \"strange\",\n"
                                    + "    \"email\": \"drstrange@gmail.com\",\n"
                                    + "    \"phone\": \"98760005\",\n"
                                    + "    \"userType\": \"A\",\n"
                                    + "    \"registrationDate\": \"2022-06-02T12:04:16.351208\",\n"
                                    + "    \"walletId\": \"0x12340005\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/profile/token\"\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByToken(HttpServletRequest request) {

    String token = request.getHeader("authorization").split(" ")[1];

    String username = jwtUtils.getUsernameFromToken(token);

    return getByUsername(username, request);
  }

  /**
   * Creates a user profile.
   *
   * @param profile the user profile
   * @param result the validation result
   * @param request the http request
   * @return the {@code ResponseMessage} containing the user profile created with the auto-generated
   *     {@code id}
   */
  @PostMapping("/")
  @Operation(
      summary = "Creates a new user profile",
      tags = {"User Profile"},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description =
                  "Requires a user profile json object, the <code>id</code> field (if specified) will be ignored",
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              externalValue =
                                  "http://localhost:8080/swagger/profile/profile-create-req.json",
                              value =
                                  "{\n"
                                      + "    \"username\": \"ghostrider\",\n"
                                      + "    \"password\": \"66660606\",\n"
                                      + "    \"firstName\": \"johnny\",\n"
                                      + "    \"lastName\": \"blaze\",\n"
                                      + "    \"email\": \"ghostrider@gmail.com\",\n"
                                      + "    \"phone\": \"98760006\",\n"
                                      + "    \"userType\": \"U\",\n"
                                      + "    \"registrationDate\": \"2022-05-23T21:56:01.7593049\"\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Returns user profile (with the auto-generated <code>id</code>) in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithUserProfile.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/profile/profile-create-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-05-23T21:58:01.7052518\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"\",\n"
                                  + "  \"data\": {\n"
                                  + "    \"id\": 6,\n"
                                  + "    \"username\": \"ghostrider\",\n"
                                  + "    \"password\": \"$2a$10$teVciT./zgDyPQM4cTzFrOqAe7K5G46BHe7970Pzyzn/qgnMBKun6\",\n"
                                  + "    \"firstName\": \"johnny\",\n"
                                  + "    \"lastName\": \"blaze\",\n"
                                  + "    \"email\": \"ghostrider@gmail.com\",\n"
                                  + "    \"phone\": \"98760006\",\n"
                                  + "    \"userType\": \"U\",\n"
                                  + "    \"registrationDate\": \"2022-05-23T21:56:01.7593049\",\n"
                                  + "    \"walletId\": \"0x12340006\"\n"
                                  + "  },\n"
                                  + "  \"path\": \"/api/v1/profile/\"\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to create user profile due to validation reasons.  Refer to schema of User Profile for details.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/profile/profile-create-400.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-05-24T10:58:57.9240571\",\n"
                                  + "  \"status\": 400,\n"
                                  + "  \"message\": \"username must not be blank, username must not contain special characters, username must contain between 6 to 20 characters\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/profile/\"\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description =
                "Unable to create user profile due to unexpected conditions e.g. a unique key constraint",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/profile/profile-create-500-unique.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-05-24T11:26:00.8833737\",\n"
                                  + "  \"status\": 500,\n"
                                  + "  \"message\": \"could not execute statement; SQL [n/a]; constraint [user_profiles.UK_dqltqkaw58m11jbov0udx8xqg]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/profile/\"\n"
                                  + "}"))
            })
      })
  public ResponseEntity<ResponseMessage> create(
      @Valid @RequestBody UserProfile profile, BindingResult result, HttpServletRequest request) {

    ResponseMessage msg;
    String uri = request.getRequestURI();

    // Encode password before persisting
    profile.setPassword(passwordEncoder.encode(profile.getPassword()));

    // Generate wallet address
    msg = restTemplate.getForObject(urlBase + BlockchainGetWalletAddress, ResponseMessage.class);
    if (msg.isOk()) profile.setWalletId((String) msg.getData());
    else return ResponseEntity.internalServerError().body(msg);

    UserProfile created = profileService.createProfile(profile);

    msg = new ResponseMessage(HttpStatus.OK.value(), created, uri);

    return ResponseEntity.ok(msg);
  }

  /**
   * Updates a user profile.
   *
   * @param profile the user profile
   * @param result the validation result
   * @param request the request
   * @return the {@code ResponseMessage} containing the updated user profile
   */
  @PutMapping("/")
  @PreAuthorize("hasAuthority('U') or hasAuthority('A')")
  @Operation(
      summary = "Updates a user profile",
      tags = {"User Profile"},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description =
                  "Requires a user profile json object, fields will be updated based on the primary key <code>id</code> ",
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              externalValue =
                                  "http://localhost:8080/swagger/profile/profile-update-req.json",
                              value =
                                  "{\n"
                                      + "  \"id\": 1,\n"
                                      + "  \"username\": \"starlord\",\n"
                                      + "  \"password\": \"$2a$10$Yf4ZSq2HvzulTOYoM5JGOOPg0VzAdvcmqVRC3vvIgQ2TCj5z6ACMm\",\n"
                                      + "  \"firstName\": \"peter\",\n"
                                      + "  \"lastName\": \"quill\",\n"
                                      + "  \"email\": \"starlord@gmail.com\",\n"
                                      + "  \"phone\": \"98760007\",\n"
                                      + "  \"userType\": \"U\",\n"
                                      + "  \"registrationDate\": \"2022-05-23T21:56:01.7593049\",\n"
                                      + "  \"walletId\": \"0x12340007\"\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns updated user profile in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithUserProfile.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-update-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-05-24T15:38:34.7636703\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 1,\n"
                                    + "    \"username\": \"starlord\",\n"
                                    + "    \"password\": \"$2a$10$Yf4ZSq2HvzulTOYoM5JGOOPg0VzAdvcmqVRC3vvIgQ2TCj5z6ACMm\",\n"
                                    + "    \"firstName\": \"peter\",\n"
                                    + "    \"lastName\": \"quill\",\n"
                                    + "    \"email\": \"starlord@gmail.com\",\n"
                                    + "    \"phone\": \"98760007\",\n"
                                    + "    \"userType\": \"U\",\n"
                                    + "    \"registrationDate\": \"2022-05-23T21:56:01.7593049\",\n"
                                    + "    \"walletId\": \"0x12340007\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/profile/\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to update user profile due to validation reasons. Refer to schema of User Profile for details.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-update-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-05-24T16:06:46.1931124\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"username must contain between 6 to 20 characters\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/profile/\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "User profile with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-update-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-05-24T16:00:14.9594333\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"User profile with id=11 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/profile/\"\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "500",
            description =
                "Unable to update user profile due to unexpected conditions e.g. a unique key constraint",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-update-500-unique.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-05-24T11:26:00.8833737\",\n"
                                    + "  \"status\": 500,\n"
                                    + "  \"message\": \"could not execute statement; SQL [n/a]; constraint [user_profiles.UK_dqltqkaw58m11jbov0udx8xqg]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/profile/\"\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> update(
      @Valid @RequestBody UserProfile profile, BindingResult result, HttpServletRequest request) {

    String uri = request.getRequestURI();
    ResponseMessage msg;

    ResponseEntity<ResponseMessage> response = getBadRequestResponse(result, uri);

    if (response != null) return response;

    Optional<UserProfile> optProfile = profileService.getProfileById(profile.getId());

    if (optProfile.isPresent()) {

      UserProfile updated = profileService.updateProfile(profile);

      msg = new ResponseMessage(HttpStatus.OK.value(), updated, uri);

      return ResponseEntity.ok(msg);
    } else {

      String reason = "User profile with id=%d NOT found.";
      msg =
          new ResponseMessage(
              HttpStatus.NOT_FOUND.value(), String.format(reason, profile.getId()), uri);

      return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Applies a json patch list of operations to a user profile.
   *
   * @param patch the json patch instance with list of operations to be applied on {@code profile}
   * @param profile the user profile
   * @return the updated user profile
   * @throws JsonPatchException exceptions when applying list of operations
   * @throws JsonProcessingException exceptions when marshalling between json objects
   */
  protected UserProfile applyPatchToUserProfile(JsonPatch patch, UserProfile profile)
      throws JsonPatchException, JsonProcessingException {

    // Configure the object mapper date format to prevent timestamps being converted to arrays
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.findAndRegisterModules();

    // mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    // mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));

    JsonNode patched = patch.apply(mapper.convertValue(profile, JsonNode.class));
    return mapper.treeToValue(patched, UserProfile.class);
  }

  /**
   * Patches the fields of a user profile.
   *
   * @param id the user profile id
   * @param request the request
   * @return the {@code ResponseMessage} containing the updated user profile
   */
  @PatchMapping(path = "/id/{id}", consumes = "application/json-patch+json")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Updates the fields of a user profile",
      tags = {"User Profile"},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description =
                  "Requires a list of operations to fields of the json user profile object.",
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              externalValue =
                                  "http://localhost:8080/swagger/profile/profile-patch-id-req.json",
                              value =
                                  "[\n"
                                      + "    {\n"
                                      + "        \"op\": \"replace\",\n"
                                      + "        \"path\": \"/email\",\n"
                                      + "        \"value\": \"patched@gmail.com\"\n"
                                      + "    },\n"
                                      + "    {\n"
                                      + "        \"op\": \"replace\",\n"
                                      + "        \"path\": \"/phone\",\n"
                                      + "        \"value\": \"1122334455\"\n"
                                      + "    }\n"
                                      + "]"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns updated user profile in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithUserProfile.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-patch-id-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-08-23T15:06:38.7004764\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 1,\n"
                                    + "    \"username\": \"sbipcc\",\n"
                                    + "    \"password\": \"$2a$10$qtFUT0sjJckTDCNtnInUQOvhcP98t00YlFdwvPupKs5n3ts6kJEHW\",\n"
                                    + "    \"firstName\": \"SBIP\",\n"
                                    + "    \"lastName\": \"Community Clinic\",\n"
                                    + "    \"email\": \"patched@gmail.com\",\n"
                                    + "    \"phone\": \"1122334455\",\n"
                                    + "    \"userType\": \"U\",\n"
                                    + "    \"registrationDate\": \"2022-08-23T15:06:23.923027\",\n"
                                    + "    \"walletId\": \"4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/profile/id/1\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to update fields due to validation reasons. Refer to schema of User Profile for details.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-patch-id-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-08-23T15:11:14.4145037\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"Validation failed for classes [com.aestus.api.profile.model.UserProfile] during update time for groups [javax.validation.groups.Default, ]\\nList of constraint violations:[\\n\\tConstraintViolationImpl{interpolatedMessage='email must be a valid format', propertyPath=email, rootBeanClass=class com.aestus.api.profile.model.UserProfile, messageTemplate='email must be a valid format'}\\n]\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/profile/id/1\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "User profile with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-patch-id-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-08-23T15:07:42.7917537\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"User profile with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/profile/id/99\",\n"
                                    + "  \"ok\": false\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> update(
      @PathVariable Integer id, @RequestBody JsonPatch patch, HttpServletRequest request) {

    String uri = request.getRequestURI();
    ResponseEntity<ResponseMessage> entity = getById(id, request);
    ResponseMessage msg = null;

    if (entity.getBody().isOk()) {
      try {
        UserProfile profile =
            applyPatchToUserProfile(patch, (UserProfile) entity.getBody().getData());

        UserProfile updated = profileService.updateProfile(profile);

        msg = new ResponseMessage(HttpStatus.OK.value(), updated, uri);

        return ResponseEntity.ok(msg);
      } catch (JsonProcessingException jpex) {
        msg = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), jpex.getMessage(), uri);
        return ResponseEntity.badRequest().body(msg);
      } catch (JsonPatchException jpex) {
        msg = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), jpex.getMessage(), uri);
        return ResponseEntity.badRequest().body(msg);
      }
    } else return entity;
  }

  /**
   * Updates the wallet id of a user profile.
   *
   * @param id the id of the user profile
   * @param walletId the wallet id to be updated
   * @param request the http request
   * @return the {@code ResponseMessage} containing the updated user profile
   */
  @PatchMapping("/id/{id}/walletId/{walletId}")
  @PreAuthorize("hasAuthority('U') or hasAuthority('S') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Updates the wallet id of a user profile",
      tags = {"User Profile"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns updated user profile in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithUserProfile.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-patch-walletId-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-08-23T16:08:21.5113646\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 1,\n"
                                    + "    \"username\": \"sbipcc\",\n"
                                    + "    \"password\": \"$2a$10$IuH2XU2BqP0TYKypbzqckOh//7TwP6mgiBY.xokujAmWr2SNSw5vC\",\n"
                                    + "    \"firstName\": \"SBIP\",\n"
                                    + "    \"lastName\": \"Community Clinic\",\n"
                                    + "    \"email\": \"sbip-clinic@gmail.com\",\n"
                                    + "    \"phone\": \"98760001\",\n"
                                    + "    \"userType\": \"U\",\n"
                                    + "    \"registrationDate\": \"2022-08-23T16:06:55.315684\",\n"
                                    + "    \"walletId\": \"12345abcde\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/profile/id/1/walletId/12345abcde\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to update wallet id due to validation reasons. Refer to schema of User Profile for details.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-patch-walletId-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-08-23T16:10:23.3185104\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"Validation failed for classes [com.aestus.api.profile.model.UserProfile] during update time for groups [javax.validation.groups.Default, ]\\nList of constraint violations:[\\n\\tConstraintViolationImpl{interpolatedMessage='walletId must contain between 10 to 100 characters', propertyPath=walletId, rootBeanClass=class com.aestus.api.profile.model.UserProfile, messageTemplate='walletId must contain between 10 to 100 characters'}\\n]\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/profile/id/1/walletId/12345\",\n"
                                    + "  \"ok\": false\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "User profile with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/profile/profile-patch-walletId-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-08-23T16:09:48.6797019\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"User profile with id=99 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/profile/id/99/walletId/12345abcde\",\n"
                                    + "  \"ok\": false\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> updateWalletId(
      @PathVariable Integer id, @PathVariable String walletId, HttpServletRequest request) {

    String uri = request.getRequestURI();
    ResponseMessage msg;

    Optional<UserProfile> optProfile = profileService.getProfileById(id);

    if (optProfile.isPresent()) {

      UserProfile profile = optProfile.get();
      profile.setWalletId(walletId);

      UserProfile updated = profileService.updateProfile(profile);

      msg = new ResponseMessage(HttpStatus.OK.value(), updated, uri);

      return ResponseEntity.ok(msg);
    } else {

      String reason = "User profile with id=%d NOT found.";
      msg = new ResponseMessage(HttpStatus.NOT_FOUND.value(), String.format(reason, id), uri);

      return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Removes a user profile by {@code id}.
   *
   * @param id the profile id
   * @param request the http request
   * @return HTTP status NOT FOUND if the user profile with {@code id} is not found. If the delete
   *     is successful, the HTTP status OK.
   */
  @DeleteMapping("/id/{id}")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Remove user profile by id",
      tags = {"User Profile"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns deleted user profile in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithUserProfile.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/profile/profile-delete-id-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-05-24T12:12:13.1852669\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"User profile with id=1 deleted.\",\n"
                                  + "  \"data\": {\n"
                                  + "    \"id\": 1,\n"
                                  + "    \"username\": \"superman\",\n"
                                  + "    \"password\": \"$2a$10$yA3n4WnfWr0vHXsFMItoGe5bGKK.F6FX3rNJ22GA2/DJ2M/CNzccW\",\n"
                                  + "    \"firstName\": \"clark\",\n"
                                  + "    \"lastName\": \"kent\",\n"
                                  + "    \"email\": \"superman@gmail.com\",\n"
                                  + "    \"phone\": \"98760001\",\n"
                                  + "    \"userType\": \"U\",\n"
                                  + "    \"registrationDate\": \"2022-05-24T11:57:25.268969\",\n"
                                  + "    \"walletId\": \"0x12340001\"\n"
                                  + "  },\n"
                                  + "  \"path\": \"/api/v1/profile/id/1\"\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "User profile with <code>id</code> not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/profile/profile-delete-id-404.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-05-24T12:11:07.0690836\",\n"
                                  + "  \"status\": 404,\n"
                                  + "  \"message\": \"User profile with id=33 NOT found.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/profile/id/33\"\n"
                                  + "}"))
            })
      })
  public ResponseEntity<ResponseMessage> removeById(
      @PathVariable int id, HttpServletRequest request) {

    String uri = request.getRequestURI();
    String reason;
    ResponseMessage msg;

    Optional<UserProfile> optProfile = profileService.getProfileById(id);

    if (optProfile.isPresent()) {

      profileService.deleteProfileById(id);

      reason = "User profile with id=%d deleted.";
      msg =
          new ResponseMessage(
              HttpStatus.OK.value(), String.format(reason, id), optProfile.get(), uri);

      return ResponseEntity.ok(msg);
    } else {

      reason = "User profile with id=%d NOT found.";
      msg = new ResponseMessage(HttpStatus.NOT_FOUND.value(), String.format(reason, id), uri);

      return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Removes all user profiles.
   *
   * @param request the http request
   * @return the HTTP status OK is successful
   */
  @DeleteMapping("/")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Removes all user profiles",
      tags = {"User Profile"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "All user profiles deleted",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/profile/profile-delete-all-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-05-24T12:34:26.7034673\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"All user profiles deleted.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/profile/\"\n"
                                  + "}"))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> removeAll(HttpServletRequest request) {
    String uri = request.getRequestURI();

    profileService.deleteAllProfiles();

    String reason = "All profiles deleted.";

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), reason, uri);

    return ResponseEntity.ok(msg);
  }
}
