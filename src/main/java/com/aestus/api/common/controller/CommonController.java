package com.aestus.api.common.controller;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.common.request.AuthenticationRequestBody;
import com.aestus.api.common.util.JWTUtils;
import com.aestus.api.profile.repository.ProfileRepository;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

/** Provides shared utility functions to other controllers. */
@Slf4j
@RestController
@RequestMapping(value = "api/v1")
public class CommonController {

  @Autowired private BCryptPasswordEncoder passwordEncoder;

  @Autowired private ProfileRepository profileRepository;
  @Autowired private JWTUtils jwtUtils;
  @Autowired private AuthenticationManager authenticationManager;

  /**
   * Checks for validation errors and returns the HTTP Bad Request with a compiled list of error
   * messages if any.
   *
   * @param result the validation result
   * @param uri the uri
   * @return the {@code ResponseMessage} instance containing all error messages with the HTTP Bad
   *     Request status.
   */
  public static ResponseEntity<ResponseMessage> getBadRequestResponse(
      BindingResult result, String uri) {

    ResponseMessage msg;
    String reasons = "";

    if (result.hasErrors()) {
      List<ObjectError> errors = result.getAllErrors();

      for (ObjectError error : errors) {
        log.info(error.getDefaultMessage());
        reasons += error.getDefaultMessage() + ", ";
      }

      if (errors.size() > 0) reasons = reasons.substring(0, reasons.length() - 2);

      msg = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), reasons, uri);

      return ResponseEntity.badRequest().body(msg);
    } else return null;
  }

  /**
   * Copies the headers from a http request to an instance of HttpHeaders, for use when consuming a
   * REST api resource on another endpoint.
   *
   * @param request the http request
   * @return an instance of HttpHeaders populated with the headers from the http request
   */
  public static HttpHeaders getHttpHeaders(HttpServletRequest request) {

    HttpHeaders headers = new HttpHeaders();

    Collections.list(request.getHeaderNames())
        .forEach(
            name -> {
              headers.set(name, request.getHeader(name));
            });

    headers.setContentType(MediaType.APPLICATION_JSON);

    return headers;
  }

  private String getResponse(Authentication auth) {
    StringBuilder sb = new StringBuilder();
    auth.getAuthorities().forEach(sb::append);

    return "<h1>Hello, " + auth.getName() + " you have these authorities " + sb + "</h1>";
  }

  /**
   * Test for User authority.
   *
   * @param auth the authentication token in security context
   * @return the string with {@code username} and corresponding authorities
   */
  @PreAuthorize("hasAuthority('U')")
  @GetMapping(value = "/test/user")
  @Operation(
      summary = "To test the User authority",
      tags = {"Common"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Request made with proper authority",
            content = {
              @Content(
                  mediaType = "text/html",
                  examples =
                      @ExampleObject(
                          value = "<h1>Hello, superman you have these authorities U</h1>"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request, no User authority",
            content = @Content)
      })
  public String testUserAuthority(Authentication auth) {
    return getResponse(auth);
  }

  /**
   * Test for Solution Provider authority.
   *
   * @param auth the authentication token in security context
   * @return the string with {@code username} and corresponding authorities
   */
  @PreAuthorize("hasAuthority('S')")
  @GetMapping(value = "/test/solution")
  @Operation(
      summary = "To test the Solution Provider authority",
      tags = {"Common"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Request made with Solution Provider authority",
            content = {
              @Content(
                  mediaType = "text/html",
                  examples =
                      @ExampleObject(value = "<h1>Hello, batman you have these authorities S</h1>"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request, no Solution Provider authority",
            content = @Content)
      })
  public String testSolutionProviderAuthority(Authentication auth) {
    return getResponse(auth);
  }

  /**
   * Test Developer authority.
   *
   * @param auth the authentication token in security context
   * @return the string with {@code username} and corresponding authorities
   */
  @PreAuthorize("hasAuthority('D')")
  @GetMapping(value = "/test/developer")
  @Operation(
      summary = "To test the Developer authority",
      tags = {"Common"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Request made with Developer authority",
            content = {
              @Content(
                  mediaType = "text/html",
                  examples =
                      @ExampleObject(
                          value = "<h1>Hello, spiderman you have these authorities D</h1>"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request, no Developer authority",
            content = @Content)
      })
  public String testDeveloperAuthority(Authentication auth) {
    return getResponse(auth);
  }

  /**
   * Test Investor authority.
   *
   * @param auth the authentication token in security context
   * @return the string with {@code username} and corresponding authorities
   */
  @PreAuthorize("hasAuthority('I')")
  @GetMapping(value = "/test/investor")
  @Operation(
      summary = "To test the Investor authority",
      tags = {"Common"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Request made with proper authority",
            content = {
              @Content(
                  mediaType = "text/html",
                  examples =
                      @ExampleObject(
                          value = "<h1>Hello, ironman you have these authorities I</h1>"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request, no Investor authority",
            content = @Content)
      })
  public String testInvestorAuthority(Authentication auth) {
    return getResponse(auth);
  }

  /**
   * Test Admin authority.
   *
   * @param auth the authentication token in security context
   * @return the string with {@code username} and corresponding authorities
   */
  @PreAuthorize("hasAuthority('A')")
  @GetMapping(value = "/test/admin")
  @Operation(
      summary = "To test the Admin authority",
      tags = {"Common"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Request made with proper authority",
            content = {
              @Content(
                  mediaType = "text/html",
                  examples =
                      @ExampleObject(
                          value = "<h1>Hello, drstrange you have these authorities A</h1>"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request, no Admin authority",
            content = @Content)
      })
  public String testAdminAuthority(Authentication auth) {
    return getResponse(auth);
  }

  /**
   * Test public endpoint
   *
   * @param auth the authentication token in security context
   * @return the string with {@code username} and corresponding authorities if known
   */
  @GetMapping(value = "/test/public")
  @Operation(
      summary = "To test endpoint for public without authentication",
      tags = {"Common"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Request made with proper authority",
            content = {
              @Content(
                  mediaType = "text/html",
                  examples = {
                    @ExampleObject(
                        name = "Request made with valid jwt token",
                        description = "Returns username and given authorities",
                        value = "<h1>Hello, drstrange you have these authorities A</h1>"),
                    @ExampleObject(
                        name = "Request made without valid jwt token",
                        description = "States <b>unknown</b> user but endpoint is public",
                        value = "<h1>Hello, unknown user, this url is public to access</h1>")
                  })
            })
      })
  public String testPublic(Authentication auth) {

    if (auth != null) {
      StringBuilder sb = new StringBuilder();
      auth.getAuthorities().forEach(sb::append);

      return "<h1>Hello, " + auth.getName() + " you have these authorities " + sb + "</h1>";
    }
    return "<h1>Hello, unknown user, this url is public to access</h1>";
  }

  /**
   * Authenticates a user login and returns a JWT token if successful.
   *
   * @param rbAuthentication the authentication request body
   * @param request the http request
   * @return the response message entity
   */
  @PostMapping("/authenticate")
  @Operation(
      summary = "Authenticates a user login and returns a ResponseMessage containing a jwt token",
      tags = {"Common"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Authentication success",
            content = {
              @Content(
                  mediaType = "application/json",
                  examples =
                      @ExampleObject(
                          name = "Returns a jwt token",
                          description = "Returns a jwt token in the <b>data</b> field",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-05-21T17:42:10.9282709\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"\",\n"
                                  + "  \"data\": \"eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImJhdG1hbiIsInJvbGVzIjoiUyIsImlhdCI6MTY1MzEyNjEzMCwiZXhwIjoxNjUzMTI3MDMwfQ.DRlg1WxXGOEwtYrAJDcjOmpUjCcyCYLXixpbjStqd00\",\n"
                                  + "  \"path\": \"/api/v1/authenticate\"\n"
                                  + "}",
                          externalValue =
                              "http://localhost:8080/swagger/common-authenticate-200.json"),
                  schema = @Schema(implementation = ResponseMessage.class))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Authentication failed, incorrect username/password",
            content = @Content)
      })
  public ResponseEntity<ResponseMessage> authenticate(
          @RequestBody AuthenticationRequestBody rbAuthentication, HttpServletRequest request) {

    ResponseMessage msg;
    String uri = request.getRequestURI();

    try {

      Authentication authenticate =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  rbAuthentication.getUsername(), rbAuthentication.getPassword()));

      User user = (User) authenticate.getPrincipal();

      Object jwtToken =
          jwtUtils.generateToken(
              user.getUsername(), (Collection<GrantedAuthority>) authenticate.getAuthorities());

      msg = new ResponseMessage(HttpStatus.OK.value(), jwtToken, request.getRequestURI());

      return new ResponseEntity(msg, HttpStatus.OK);

    } catch (AuthenticationException auEx) {
      msg = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), auEx.getMessage(), uri);

      return ResponseEntity.badRequest().body(msg);
    }
  }

  /**
   * Encrypts a string.
   *
   * @param theString the string to be encrypted
   * @param request the http request
   * @return the response message entity
   */
  @GetMapping("/encrypt/{theString}")
  @Operation(
      summary = "Encrypts a string using the BCrypt password encoder",
      tags = {"Common"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns the encrypted string in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            externalValue = "http://localhost:8080/swagger/common-encrypt-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-05-23T16:52:22.2985135\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": \"$2a$10$teVciT./zgDyPQM4cTzFrOqAe7K5G46BHe7970Pzyzn/qgnMBKun6\",\n"
                                    + "  \"path\": \"/api/v1/encrypt/66666060\"\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> encrypt(
      @PathVariable String theString, HttpServletRequest request) {

    Object encoded = passwordEncoder.encode(theString);

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), encoded, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }
}
