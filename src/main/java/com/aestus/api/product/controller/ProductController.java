package com.aestus.api.product.controller;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.product.service.ProductService;
import com.aestus.api.product.model.Product;
import com.aestus.api.product.model.swagger.ResponseMessageWithProduct;
import com.aestus.api.product.model.swagger.ResponseMessageWithProducts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/** Provides the product controller functionality. */
@Slf4j
@RestController
@RequestMapping("api/v1/product")
@Validated
public class ProductController {
  @Autowired private ProductService productService;

  @Autowired RestTemplate restTemplate;

  /**
   * Pinging the controller.
   *
   * @param request the request
   * @return the ping returned message in the {@code ResponseEntity} container object
   */
  @GetMapping("/ping")
  @Operation(
      summary = "Ping test",
      tags = {"Product"},
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
                              "http://localhost:8080/swagger/product/product-ping-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-10T21:12:45.1904532\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"ping pong\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/product/ping\",\n"
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
   * Creates a product via a {@code Product} object in the request body.
   *
   * @param product the product
   * @param request the http request
   * @return the {@code ResponseMessage} containing the product created with the auto-generated
   *     {@code id}
   */
  @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Creates a new product via a <code>Product</code> json object",
      tags = {"Product"},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description =
                  "Requires a product json object, the <code>id</code> field (if specified) will be ignored",
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              externalValue =
                                  "http://localhost:8080/swagger/product/product-create-req.json",
                              value =
                                  "{\n"
                                      + "  \"profileId\": 2,\n"
                                      + "  \"name\": \"Epson T3-B All-in-One SCARA Robot\",\n"
                                      + "  \"type\": \"SCARA\",\n"
                                      + "  \"description\": \"The ideal alternative to slide-based solutions, All-in-One design includes power for end-of-arm tooling, 4 built-in axes in one compact design. Perfect for pick and place, simple assembly, material handing and dispensing.\",\n"
                                      + "  \"imageUrl\": \"http://localhost:8080/images/robot.jpg\",\n"
                                      + "  \"price\": 1000000,\n"
                                      + "  \"specifications\": \"Designed to seamlessly fit in a variety of workspaces, this all-in-one solution features a built-in controller, power for end-of-arm tooling and 110 V or 220 V power—virtually eliminating any space-constraint issues. Plus, it offers a 400 mm reach and a payload of up to 3 kg to easily handle a variety of tasks.\",\n"
                                      + "  \"createdTimestamp\": \"2022-06-10T21:19:12.2533473\"\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Returns a product (with the auto-generated <code>id</code>) in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithProduct.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/product/product-create-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-07-07T11:39:49.1975611\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 7,\n"
                                    + "    \"profileId\": 2,\n"
                                    + "    \"name\": \"Epson T3-B All-in-One SCARA Robot\",\n"
                                    + "    \"type\": \"SCARA\",\n"
                                    + "    \"description\": \"The ideal alternative to slide-based solutions, All-in-One design includes power for end-of-arm tooling, 4 built-in axes in one compact design. Perfect for pick and place, simple assembly, material handing and dispensing.\",\n"
                                    + "    \"imageUrl\": \"http://localhost:8080/images/robot.jpg\",\n"
                                    + "    \"price\": 1000000,\n"
                                    + "    \"specifications\": \"Designed to seamlessly fit in a variety of workspaces, this all-in-one solution features a built-in controller, power for end-of-arm tooling and 110 V or 220 V power—virtually eliminating any space-constraint issues. Plus, it offers a 400 mm reach and a payload of up to 3 kg to easily handle a variety of tasks.\",\n"
                                    + "    \"createdTimestamp\": \"2022-06-10T21:19:12.2533473\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/product/\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to create product due to validation reasons.  Refer to schema of Product for details.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/product/product-create-400.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-07-07T11:39:09.930089\",\n"
                                  + "  \"status\": 400,\n"
                                  + "  \"message\": \"ConstraintViolationException: create.product.imageUrl: must be a valid URL\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/product/\",\n"
                                  + "  \"ok\": false\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
      })
  public ResponseEntity<ResponseMessage> create(
      @Valid @RequestBody Product product, BindingResult result, HttpServletRequest request) {

    String uri = request.getRequestURI();

    Product created = productService.createProduct(product);

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), created, uri);

    return ResponseEntity.ok(msg);
  }

  /**
   * Updates a product.
   *
   * @param product the product
   * @param result the validation result
   * @param request the request
   * @return the {@code ResponseMessage} containing the updated product
   */
  @PutMapping("/")
  @PreAuthorize("hasAuthority('U') or hasAuthority('A')")
  @Operation(
      summary = "Updates a product",
      tags = {"Product"},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description =
                  "Requires a <code>Product</code> json object, fields will be updated based on the primary key <code>id</code> ",
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              externalValue =
                                  "http://localhost:8080/swagger/product/product-update-req.json",
                              value =
                                  "{\n"
                                      + "  \"id\": 1,\n"
                                      + "  \"profileId\": 2,\n"
                                      + "  \"name\": \"Epson T3-B All-in-One SCARA Robot\",\n"
                                      + "  \"type\": \"SCARA\",\n"
                                      + "  \"description\": \"The ideal alternative to slide-based solutions, All-in-One design includes power for end-of-arm tooling, 4 built-in axes in one compact design. Perfect for pick and place, simple assembly, material handing and dispensing.\",\n"
                                      + "  \"price\": 1800000,\n"
                                      + "  \"specifications\": \"Designed to seamlessly fit in a variety of workspaces, this all-in-one solution features a built-in controller, power for end-of-arm tooling and 110 V or 220 V power—virtually eliminating any space-constraint issues. Plus, it offers a 400 mm reach and a payload of up to 3 kg to easily handle a variety of tasks.\",\n"
                                      + "  \"createdTimestamp\": \"2022-06-10T21:19:12.2533473\"\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns updated product in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithProduct.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/product/product-update-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-11T17:43:39.5230145\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 1,\n"
                                    + "    \"profileId\": 2,\n"
                                    + "    \"name\": \"Epson T3-B All-in-One SCARA Robot\",\n"
                                    + "    \"type\": \"SCARA\",\n"
                                    + "    \"description\": \"The ideal alternative to slide-based solutions, All-in-One design includes power for end-of-arm tooling, 4 built-in axes in one compact design. Perfect for pick and place, simple assembly, material handing and dispensing.\",\n"
                                    + "    \"price\": 1800000,\n"
                                    + "    \"specifications\": \"Designed to seamlessly fit in a variety of workspaces, this all-in-one solution features a built-in controller, power for end-of-arm tooling and 110 V or 220 V power—virtually eliminating any space-constraint issues. Plus, it offers a 400 mm reach and a payload of up to 3 kg to easily handle a variety of tasks.\",\n"
                                    + "    \"createdTimestamp\": \"2022-06-10T21:19:12.2533473\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/product/\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Unable to update product due to validation reasons. Refer to schema of <code>Product</code> for details.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/product/product-update-400.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-11T09:20:02.0347893\",\n"
                                    + "  \"status\": 400,\n"
                                    + "  \"message\": \"javax.validation.ConstraintViolationException: create.product.profileId: profileId must not be Null, create.product.profileId: profileId must not be Null\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/product/\",\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Product with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/product/product-update-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-11T09:21:25.7543352\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"Product with id=11 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/product/\",\n"
                                    + "}"))),
      })
  public ResponseEntity<ResponseMessage> update(
      @Valid @RequestBody Product product, BindingResult result, HttpServletRequest request) {

    String uri = request.getRequestURI();
    ResponseMessage msg;

    Optional<Product> optProfile = productService.getProductById(product.getId());

    if (optProfile.isPresent()) {

      Product updated = productService.updateProduct(product);

      msg = new ResponseMessage(HttpStatus.OK.value(), updated, uri);

      return ResponseEntity.ok(msg);
    } else {

      String reason = "Product with id=%d NOT found.";
      msg =
          new ResponseMessage(
              HttpStatus.NOT_FOUND.value(), String.format(reason, product.getId()), uri);

      return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Gets all products.
   *
   * @param request the http request
   * @return all products
   */
  @GetMapping("/")
  @PreAuthorize("hasAuthority('U') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve all products",
      tags = {"Product"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns all products in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithProducts.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/product/product-get-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-11T16:43:15.1542426\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"\",\n"
                                  + "  \"data\": [\n"
                                  + "    {\n"
                                  + "      \"id\": 1,\n"
                                  + "      \"profileId\": 2,\n"
                                  + "      \"name\": \"Epson T3-B All-in-One SCARA Robot\",\n"
                                  + "      \"type\": \"SCARA\",\n"
                                  + "      \"description\": \"The ideal alternative to slide-based solutions, All-in-One design includes power for end-of-arm tooling, 4 built-in axes in one compact design. Perfect for pick and place, simple assembly, material handing and dispensing.\",\n"
                                  + "      \"price\": 1000000,\n"
                                  + "      \"specifications\": \"Designed to seamlessly fit in a variety of workspaces, this all-in-one solution features a built-in controller, power for end-of-arm tooling and 110 V or 220 V power—virtually eliminating any space-constraint issues. Plus, it offers a 400 mm reach and a payload of up to 3 kg to easily handle a variety of tasks.\",\n"
                                  + "      \"createdTimestamp\": \"2022-06-11T16:36:34.410758\"\n"
                                  + "    },\n"
                                  + "    {\n"
                                  + "      \"id\": 2,\n"
                                  + "      \"profileId\": 2,\n"
                                  + "      \"name\": \"Epson VT6L All-in-One 6-Axis Robot\",\n"
                                  + "      \"type\": \"6-AXIS\",\n"
                                  + "      \"description\": \"Features Slimline design perfect for factories with limited floor space and compact wrist pitch that enables robot easy access to hard-to-reach areas. Ideal for load/ unload, packaging or parts assembly applications. Cleanroom (ISO4) and Protected (IP67) models available.\",\n"
                                  + "      \"price\": 2000000,\n"
                                  + "      \"specifications\": \"VT6L offers a reach up to 900 mm and a payload up to 6 kg. A feature-packed performer, it includes a built-in controller, plus simplified cabling with a hollow end-of-arm design – all at a remarkably low cost, in a compact, SlimLine structure. The VT6L offers 110 V and 220 V power and installs in minutes.\",\n"
                                  + "      \"createdTimestamp\": \"2022-06-11T16:36:34.410758\"\n"
                                  + "    },\n"
                                  + "    {\n"
                                  + "      \"id\": 3,\n"
                                  + "      \"profileId\": 6,\n"
                                  + "      \"name\": \"TX2touch-90 POWER cobot\",\n"
                                  + "      \"type\": \"COBOT\",\n"
                                  + "      \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                  + "      \"price\": 2000000,\n"
                                  + "      \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                  + "      \"createdTimestamp\": \"2022-06-11T16:36:34.410758\"\n"
                                  + "    }\n"
                                  + "  ],\n"
                                  + "  \"path\": \"/api/v1/product/\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getAll(HttpServletRequest request) {

    Iterable<Product> products = productService.getAllProducts();

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), products, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets products by profile id.
   *
   * @param profileId the profile id
   * @param request the http request
   * @return a list products by profile id
   */
  @GetMapping("/profileId/{profileId}")
  @PreAuthorize("hasAuthority('S') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve products by profile id",
      tags = {"Product"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Return products in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithProducts.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/product/product-get-profileId-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-11T17:47:50.3650505\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"\",\n"
                                  + "  \"data\": [\n"
                                  + "    {\n"
                                  + "      \"id\": 1,\n"
                                  + "      \"profileId\": 2,\n"
                                  + "      \"name\": \"Epson T3-B All-in-One SCARA Robot\",\n"
                                  + "      \"type\": \"SCARA\",\n"
                                  + "      \"description\": \"The ideal alternative to slide-based solutions, All-in-One design includes power for end-of-arm tooling, 4 built-in axes in one compact design. Perfect for pick and place, simple assembly, material handing and dispensing.\",\n"
                                  + "      \"price\": 1800000,\n"
                                  + "      \"specifications\": \"Designed to seamlessly fit in a variety of workspaces, this all-in-one solution features a built-in controller, power for end-of-arm tooling and 110 V or 220 V power—virtually eliminating any space-constraint issues. Plus, it offers a 400 mm reach and a payload of up to 3 kg to easily handle a variety of tasks.\",\n"
                                  + "      \"createdTimestamp\": \"2022-06-10T21:19:12.253347\"\n"
                                  + "    },\n"
                                  + "    {\n"
                                  + "      \"id\": 4,\n"
                                  + "      \"profileId\": 2,\n"
                                  + "      \"name\": \"Epson T3-B All-in-One SCARA Robot\",\n"
                                  + "      \"type\": \"SCARA\",\n"
                                  + "      \"description\": \"The ideal alternative to slide-based solutions, All-in-One design includes power for end-of-arm tooling, 4 built-in axes in one compact design. Perfect for pick and place, simple assembly, material handing and dispensing.\",\n"
                                  + "      \"price\": 1000000,\n"
                                  + "      \"specifications\": \"Designed to seamlessly fit in a variety of workspaces, this all-in-one solution features a built-in controller, power for end-of-arm tooling and 110 V or 220 V power—virtually eliminating any space-constraint issues. Plus, it offers a 400 mm reach and a payload of up to 3 kg to easily handle a variety of tasks.\",\n"
                                  + "      \"createdTimestamp\": \"2022-06-10T21:19:12.253347\"\n"
                                  + "    },\n"
                                  + "    {\n"
                                  + "      \"id\": 2,\n"
                                  + "      \"profileId\": 2,\n"
                                  + "      \"name\": \"Epson VT6L All-in-One 6-Axis Robot\",\n"
                                  + "      \"type\": \"6-AXIS\",\n"
                                  + "      \"description\": \"Features Slimline design perfect for factories with limited floor space and compact wrist pitch that enables robot easy access to hard-to-reach areas. Ideal for load/ unload, packaging or parts assembly applications. Cleanroom (ISO4) and Protected (IP67) models available.\",\n"
                                  + "      \"price\": 2000000,\n"
                                  + "      \"specifications\": \"VT6L offers a reach up to 900 mm and a payload up to 6 kg. A feature-packed performer, it includes a built-in controller, plus simplified cabling with a hollow end-of-arm design – all at a remarkably low cost, in a compact, SlimLine structure. The VT6L offers 110 V and 220 V power and installs in minutes.\",\n"
                                  + "      \"createdTimestamp\": \"2022-06-11T17:37:50.052834\"\n"
                                  + "    }\n"
                                  + "  ],\n"
                                  + "  \"path\": \"/api/v1/product/profileId/2\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByProfileId(
      @Valid @PathVariable Integer profileId, HttpServletRequest request) {

    Iterable<Product> products = productService.getProductsByProfileId(profileId);

    ResponseMessage msg =
        new ResponseMessage(HttpStatus.OK.value(), products, request.getRequestURI());

    return ResponseEntity.ok(msg);
  }

  /**
   * Gets product by id.
   *
   * @param id the product id
   * @param request the http request
   * @return the product with the id if available, if not, the NOT FOUND HTTP status
   */
  @GetMapping("/id/{id}")
  @PreAuthorize(
      "hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I') or hasAuthority('A')")
  @Operation(
      summary = "Retrieve product by id",
      tags = {"Product"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns a product in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithProduct.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/product/product-get-id-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-11T16:46:02.8065866\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": {\n"
                                    + "    \"id\": 3,\n"
                                    + "    \"profileId\": 6,\n"
                                    + "    \"name\": \"TX2touch-90 POWER cobot\",\n"
                                    + "    \"type\": \"COBOT\",\n"
                                    + "    \"description\": \"TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due to the performance, smart connectivity and reliability inherited from TX2 robots and its CS9 controller.\",\n"
                                    + "    \"price\": 2000000,\n"
                                    + "    \"specifications\": \"The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.\",\n"
                                    + "    \"createdTimestamp\": \"2022-06-11T16:36:34.410758\"\n"
                                    + "  },\n"
                                    + "  \"path\": \"/api/v1/product/id/3\",\n"
                                    + "  \"ok\": true\n"
                                    + "}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Product with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/product/product-get-id-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-06-11T16:47:29.5164184\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"Product with id=6 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/product/id/6\",\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> getById(
      @PathVariable Integer id, HttpServletRequest request) {

    Optional<Product> optProduct = productService.getProductById(id);

    ResponseMessage msg;

    if (optProduct.isPresent()) {

      msg = new ResponseMessage(HttpStatus.OK.value(), optProduct.get(), request.getRequestURI());

      return new ResponseEntity<>(msg, HttpStatus.OK);
    } else {

      String reason = "Product with id=%d NOT found.";

      msg =
          new ResponseMessage(
              HttpStatus.NOT_FOUND.value(), String.format(reason, id), request.getRequestURI());

      return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Removes a product by {@code id}.
   *
   * @param id the product id
   * @param request the http request
   * @return HTTP status NOT FOUND if the product with {@code id} is not found. If the delete is
   *     successful, the HTTP status OK.
   */
  @DeleteMapping("/id/{id}")
  @PreAuthorize("hasAuthority('S') or hasAuthority('A')")
  @Operation(
      summary = "Remove product by id",
      tags = {"Product"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns deleted product in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithProduct.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/product/product-delete-id-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-11T18:10:30.8935151\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"Product with id=1 deleted.\",\n"
                                  + "  \"data\": {\n"
                                  + "    \"id\": 1,\n"
                                  + "    \"profileId\": 2,\n"
                                  + "    \"name\": \"Epson T3-B All-in-One SCARA Robot\",\n"
                                  + "    \"type\": \"SCARA\",\n"
                                  + "    \"description\": \"The ideal alternative to slide-based solutions, All-in-One design includes power for end-of-arm tooling, 4 built-in axes in one compact design. Perfect for pick and place, simple assembly, material handing and dispensing.\",\n"
                                  + "    \"price\": 1000000,\n"
                                  + "    \"specifications\": \"Designed to seamlessly fit in a variety of workspaces, this all-in-one solution features a built-in controller, power for end-of-arm tooling and 110 V or 220 V power—virtually eliminating any space-constraint issues. Plus, it offers a 400 mm reach and a payload of up to 3 kg to easily handle a variety of tasks.\",\n"
                                  + "    \"createdTimestamp\": \"2022-06-11T18:09:58.422717\"\n"
                                  + "  },\n"
                                  + "  \"path\": \"/api/v1/product/id/1\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Product with <code>id</code> not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/product/product-delete-id-404.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-11T18:11:41.3677378\",\n"
                                  + "  \"status\": 404,\n"
                                  + "  \"message\": \"Product with id=77 NOT found.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/product/id/77\",\n"
                                  + "  \"ok\": false\n"
                                  + "}"))
            })
      })
  public ResponseEntity<ResponseMessage> removeById(
      @PathVariable int id, HttpServletRequest request) {

    String uri = request.getRequestURI();
    String reason;
    ResponseMessage msg;

    Optional<Product> optProduct = productService.getProductById(id);

    if (optProduct.isPresent()) {

      productService.deleteProductById(id);

      reason = "Product with id=%d deleted.";
      msg =
          new ResponseMessage(
              HttpStatus.OK.value(), String.format(reason, id), optProduct.get(), uri);

      return ResponseEntity.ok(msg);
    } else {

      reason = "Product with id=%d NOT found.";
      msg = new ResponseMessage(HttpStatus.NOT_FOUND.value(), String.format(reason, id), uri);

      return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Removes all products.
   *
   * @param request the http request
   * @return the HTTP status OK is successful
   */
  @DeleteMapping("/")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Removes all products",
      tags = {"Product"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "All products deleted",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessage.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/product/product-delete-all-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-06-11T18:13:37.7594407\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"All products deleted.\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/product/\",\n"
                                  + "  \"ok\": true\n"
                                  + "}"))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> removeAll(HttpServletRequest request) {

    String uri = request.getRequestURI();

    productService.deleteAllProducts();

    String reason = "All products deleted.";

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), reason, uri);

    return ResponseEntity.ok(msg);
  }
}
