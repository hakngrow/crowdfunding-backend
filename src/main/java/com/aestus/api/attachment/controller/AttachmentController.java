package com.aestus.api.attachment.controller;

import com.aestus.api.attachment.model.Attachment;
import com.aestus.api.attachment.model.swagger.ResponseMessageWithAttachments;
import com.aestus.api.attachment.service.AttachmentService;
import com.aestus.api.common.model.ResponseFile;
import com.aestus.api.common.model.ResponseMessage;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** Provides the attachment controller functionality. */
@Slf4j
@RestController
@RequestMapping("api/v1/attachment")
public class AttachmentController {

  @Autowired private AttachmentService attachmentService;

  /**
   * Pinging the controller.
   *
   * @param request the request
   * @return the ping returned message in the {@code ResponseEntity} container object
   */
  @GetMapping("/ping")
  @Operation(
      summary = "Ping test",
      tags = {"Attachment"},
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
                          externalValue = "http://localhost:8080/swagger/attachment-ping-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-05-23T11:34:15.1883792\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"ping pong\",\n"
                                  + "  \"data\": null,\n"
                                  + "  \"path\": \"/api/v1/attachment/ping\"\n"
                                  + "}"))
            })
      })
  public ResponseEntity<ResponseMessage> test(HttpServletRequest request) {

    String baseUrl = request.getRequestURI();

    ResponseMessage msg = new ResponseMessage(HttpStatus.OK.value(), "ping pong", baseUrl);

    return new ResponseEntity<ResponseMessage>(msg, HttpStatus.OK);
  }

  /**
   * Gets all attachments.
   *
   * @param request the http request
   * @return all attachments
   */
  @GetMapping("/")
  @PreAuthorize("hasAuthority('A')")
  @Operation(
      summary = "Retrieve all attachments",
      tags = {"Attachment"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns all attachments in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithAttachments.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/attachment/attachment-get-all-200.json",
                          value = ""))
            }),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getAll(HttpServletRequest request) {

    ResponseMessage msg;

    try {

      List<ResponseFile> files =
          attachmentService
              .getAllAttachments()
              .map(
                  attachment -> {
                    String fileDownloadUri =
                        ServletUriComponentsBuilder.fromCurrentRequestUri()
                            .path("/id/")
                            .path(Integer.toString(attachment.getId()))
                            .toUriString();
                    return new ResponseFile(
                        attachment.getFileName(),
                        fileDownloadUri,
                        attachment.getFileType(),
                        attachment.getData().length);
                  })
              .collect(Collectors.toList());

      msg = new ResponseMessage(HttpStatus.OK.value(), files, request.getRequestURI());

      return ResponseEntity.status(HttpStatus.OK).body(msg);
    } catch (RuntimeException ex) {

      log.error(ex.getMessage());

      msg =
          new ResponseMessage(
              HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), request.getRequestURI());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
    }
  }

  /**
   * Gets by id.
   *
   * @param id the id
   * @param request the request
   * @return the by id
   */
  @GetMapping("/id/{id}")
  @PreAuthorize(
      "hasAuthority('A') or hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I')")
  @Operation(
      summary = "Retrieve attachment by id",
      tags = {"Attachment"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Returns an attachment in the <code>data</code> field",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/attachment/attachment-get-id-200-Event_5419992.ics.json",
                            value =
                                "BEGIN:VCALENDAR\n"
                                    + "VERSION:2.0\n"
                                    + "PRODID:-//Springshare//LibCal//EN\n"
                                    + "CALSCALE:GREGORIAN\n"
                                    + "METHOD:PUBLISH\n"
                                    + "X-WR-TIMEZONE:Asia/Singapore\n"
                                    + "X-PUBLISHED-TTL:PT15M\n"
                                    + "BEGIN:VEVENT\n"
                                    + "DTSTART:20220520T060000Z\n"
                                    + "DTEND:20220520T080000Z\n"
                                    + "DTSTAMP:20220520T000000Z\n"
                                    + "SUMMARY:Jumpstart Minis : Create your first 3D Model (Part 2)\n"
                                    + "DESCRIPTION:What is it?\\n\\nNew to 3D modelling and printing? Learn some \n"
                                    + " basic design principles for 3D printing and create simple 3D models using \n"
                                    + " freely available tools on Windows (Part 2).\\n\\nLearning \n"
                                    + " Objectives\\n\\nParticipants will be able to:\\n\\n\\n\tUse TinkerCAD to create a \n"
                                    + " model ready for 3D print\\n\\n\\nWhat do you need?\\n\\n\\n\tCheck the workshop's \n"
                                    + " Teams\\n\\n\t\\n\t\tYou will be added to the workshop's Teams 3 working days \n"
                                    + " before the day of the workshop.\\n\t\tPlease refer to the announcements and \n"
                                    + " workshop materials for more information\\n\t\\n\t\\n\tAn account on \n"
                                    + " TinkerCAD\\n\\n\\nDigital Badge Requirements\\n\\nTo get the 3D Modeller (Basic) \n"
                                    + " badge:\\n\\n\\n\tAttend both Parts 1 and 2\\n\tCreate a 3D model using TinkerCAD \n"
                                    + " and submit the .stl file output along with a write-up of the modelling \n"
                                    + " process\\n\\n\\n*Contents subject to change\n"
                                    + "ORGANIZER;CN=\"TEL Imaginarium Team\":MAILTO:telimaginarium@nus.edu.sg\n"
                                    + "CATEGORIES:Design\n"
                                    + "CONTACT;CN=\"TEL Imaginarium Team\":MAILTO:telimaginarium@nus.edu.sg\n"
                                    + "STATUS:CONFIRMED\n"
                                    + "UID:LibCal-5419992\n"
                                    + "URL:https://nus.libcal.com/event/5419992\n"
                                    + "X-MICROSOFT-CDO-BUSYSTATUS:BUSY\n"
                                    + "BEGIN:VALARM\n"
                                    + "TRIGGER:-PT15M\n"
                                    + "ACTION:DISPLAY\n"
                                    + "DESCRIPTION:Reminder\n"
                                    + "END:VALARM\n"
                                    + "END:VEVENT\n"
                                    + "\n"
                                    + "END:VCALENDAR"))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Attachment with <code>id</code> not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/attachment/attachment-get-id-404.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-05-25T12:14:45.4085163\",\n"
                                    + "  \"status\": 404,\n"
                                    + "  \"message\": \"Attachment with id=6 NOT found.\",\n"
                                    + "  \"data\": null,\n"
                                    + "  \"path\": \"/api/v1/attachment/id/6\"\n"
                                    + "}")))
      })
  public ResponseEntity getById(@PathVariable int id, HttpServletRequest request) {
    Optional<Attachment> optAttachment = attachmentService.getAttachmentById(id);

    if (optAttachment.isPresent()) {
      Attachment attachment = optAttachment.get();

      return ResponseEntity.ok()
          .header(
              HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"" + attachment.getFileName() + "\"")
          .body(attachment.getData());
    } else {

      String reason = "Attachment with id=%d NOT found.";

      ResponseMessage msg =
          new ResponseMessage(
              HttpStatus.NOT_FOUND.value(), String.format(reason, id), request.getRequestURI());

      return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Gets by document id.
   *
   * @param documentId the document id
   * @param request the request
   * @return the by document id
   */
  @GetMapping("/docId/{documentId}")
  @PreAuthorize(
      "hasAuthority('A') or hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I')")
  @Operation(
      summary = "Retrieve attachments by document id",
      tags = {"Attachment"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Returns all attachments by <code>documentId</code> in the <code>data</code> field",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessageWithAttachments.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/attachment/attachment-get-documentId-200.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-05-25T15:10:49.3728642\",\n"
                                    + "  \"status\": 200,\n"
                                    + "  \"message\": \"\",\n"
                                    + "  \"data\": [\n"
                                    + "    {\n"
                                    + "      \"name\": \"attachment-ping-200.json\",\n"
                                    + "      \"url\": \"http://localhost:8080/api/v1/attachment/id/6\",\n"
                                    + "      \"type\": \"application/json\",\n"
                                    + "      \"size\": 122\n"
                                    + "    },\n"
                                    + "    {\n"
                                    + "      \"name\": \"attachment-upload-200.json\",\n"
                                    + "      \"url\": \"http://localhost:8080/api/v1/attachment/id/7\",\n"
                                    + "      \"type\": \"application/json\",\n"
                                    + "      \"size\": 1034\n"
                                    + "    }\n"
                                    + "  ],\n"
                                    + "  \"path\": \"/api/v1/attachment/docId/88\"\n"
                                    + "}"))),
        @ApiResponse(responseCode = "403", description = "Unauthorized request", content = @Content)
      })
  public ResponseEntity<ResponseMessage> getByDocumentId(
      @PathVariable int documentId, HttpServletRequest request) {

    ResponseMessage msg;

    try {

      List<ResponseFile> files =
          attachmentService
              .getAttachmentsByDocumentId(documentId)
              .map(
                  attachment -> {
                    String fileDownloadUri =
                        ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("api/v1/attachment/id/")
                            .path(Integer.toString(attachment.getId()))
                            .toUriString();
                    return new ResponseFile(
                        attachment.getFileName(),
                        fileDownloadUri,
                        attachment.getFileType(),
                        attachment.getData().length);
                  })
              .collect(Collectors.toList());

      msg = new ResponseMessage(HttpStatus.OK.value(), files, request.getRequestURI());

      return ResponseEntity.status(HttpStatus.OK).body(msg);
    } catch (RuntimeException ex) {

      log.error(ex.getMessage());

      msg =
          new ResponseMessage(
              HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), request.getRequestURI());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
    }
  }

  /**
   * Upload attachments.
   *
   * @param files the files to be uploaded
   * @param descriptions the corresponding file descriptions
   * @param documentType the type of document the files are attached to
   * @param documentId the id of document the files are attached to
   * @param request the http request
   * @return the response entity
   */
  @PostMapping("/upload")
  @PreAuthorize(
      "hasAuthority('A') or hasAuthority('U') or hasAuthority('S') or hasAuthority('D') or hasAuthority('I')")
  @Operation(
      summary = "Uploads multiple files as attachments",
      tags = {"Attachment"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                "Returns the list of uploaded attachments (with the auto-generated <code>id</code>) in the <code>data</code> field",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResponseMessageWithAttachments.class),
                  examples =
                      @ExampleObject(
                          externalValue =
                              "http://localhost:8080/swagger/attachment/attachment-upload-200.json",
                          value =
                              "{\n"
                                  + "  \"timestamp\": \"2022-05-25T10:42:14.3054014\",\n"
                                  + "  \"status\": 200,\n"
                                  + "  \"message\": \"\",\n"
                                  + "  \"data\": [\n"
                                  + "    {\n"
                                  + "      \"id\": 6,\n"
                                  + "      \"fileName\": \"attachment-ping-200.json\",\n"
                                  + "      \"fileType\": \"application/json\",\n"
                                  + "      \"fileDesc\": \"ping\",\n"
                                  + "      \"data\": \"eyJ0aW1lc3RhbXAiOiIyMDIyLTA1LTI0VDIyOjE3OjQyLjk5Mzk4MiIsInN0YXR1cyI6MjAwLCJtZXNzYWdlIjoicGluZyBwb25nIiwiZGF0YSI6bnVsbCwicGF0aCI6Ii9hcGkvdjEvYXR0YWNobWVudC9waW5nIn0=\",\n"
                                  + "      \"documentType\": \"RFQ\",\n"
                                  + "      \"documentId\": 88,\n"
                                  + "      \"createdTimestamp\": \"2022-05-25T10:42:14.2528683\"\n"
                                  + "    },\n"
                                  + "    {\n"
                                  + "      \"id\": 7,\n"
                                  + "      \"fileName\": \"attachment-upload-200.json\",\n"
                                  + "      \"fileType\": \"application/json\",\n"
                                  + "      \"fileDesc\": \"update\",\n"
                                  + "      \"data\": \"eyJ0aW1lc3RhbXAiOiIyMDIyLTA1LTI1VDA2OjE1OjQzLjYyNjA4MzYiLCJzdGF0dXMiOjIwMCwibWVzc2FnZSI6IiIsImRhdGEiOlsib3BlbmFwaTMuanNvbiJdLCJwYXRoIjoiL2FwaS92MS9hdHRhY2htZW50L3VwbG9hZCJ9\",\n"
                                  + "      \"documentType\": \"RFQ\",\n"
                                  + "      \"documentId\": 88,\n"
                                  + "      \"createdTimestamp\": \"2022-05-25T10:42:14.2962561\"\n"
                                  + "    }\n"
                                  + "  ],\n"
                                  + "  \"path\": \"/api/v1/attachment/upload\"\n"
                                  + "}")),
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized request",
            content = @Content),
        @ApiResponse(
            responseCode = "417",
            description =
                "Expectation failed due to access errors (the temporary store may have failed).",
            content = @Content),
        @ApiResponse(
            responseCode = "500",
            description =
                "Unable to upload attachment. The maximum upload size (16MB currently) may have been exceeded.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseMessage.class),
                    examples =
                        @ExampleObject(
                            externalValue =
                                "http://localhost:8080/swagger/attachment/attachment-upload-500.json",
                            value =
                                "{\n"
                                    + "  \"timestamp\": \"2022-05-25T03:28:11.655+00:00\",\n"
                                    + "  \"status\": 500,\n"
                                    + "  \"error\": \"Internal Server Error\",\n"
                                    + "  \"exception\": \"org.springframework.web.multipart.MaxUploadSizeExceededException\",\n"
                                    + "  \"message\": \"Maximum upload size exceeded; nested exception is java.lang.IllegalStateException: org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException: the request was rejected because its size (28488710) exceeds the configured maximum (10485760)\",\n"
                                    + "  \"path\": \"/api/v1/attachment/upload\"\n"
                                    + "}")))
      })
  public ResponseEntity<ResponseMessage> upload(
      @RequestParam("files") MultipartFile[] files,
      @RequestParam("descriptions") String[] descriptions,
      @RequestParam("documentType") String documentType,
      @RequestParam("documentId") int documentId,
      HttpServletRequest request) {

    ResponseMessage msg;
    String uri = request.getRequestURI();

    List<Attachment> attachments = new ArrayList<Attachment>();

    try {

      for (int i = 0; i < files.length; i++) {

        attachments.add(
            attachmentService.createAttachment(
                files[i], descriptions[i], documentType, documentId));
      }

      msg = new ResponseMessage(HttpStatus.OK.value(), attachments, uri);

      return ResponseEntity.status(HttpStatus.OK).body(msg);
    } catch (IOException ioex) {
      log.error(ioex.getMessage());

      msg = new ResponseMessage(HttpStatus.EXPECTATION_FAILED.value(), ioex.getMessage(), uri);

      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(msg);
    } catch (RuntimeException ex) {

      log.error(ex.getMessage());

      msg = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), uri);

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
    }
  }
}
