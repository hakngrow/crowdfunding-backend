package com.aestus.api.attachment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.time.LocalDateTime;

/** The type Attachment represents a file attached to a request. */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity()
@Table(name = "attachments")
public class Attachment {

  /** The auto-generated Id. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected int id;

  /** The name of the file. */
  @NotBlank(message = "fileName must not be blank")
  @Size(min = 2, max = 200, message = "fileName must contain between 2 to 200 characters")
  @Column(nullable = false)
  protected String fileName;

  /** The type of the file. */
  @NotBlank(message = "fileType must not be blank")
  @Size(min = 1, max = 50, message = "fileType must contain between 1 to 50 characters")
  // @Pattern(regexp = "[a-zA-Z]+/[a-zA-Z]+", message = "fileType must contain only alphabets")
  @Column(nullable = false)
  protected String fileType;

  /** The file description given by the user. */
  @Size(min = 2, max = 300, message = "fileDesc must contain between 2 to 300 characters")
  @Column(nullable = false)
  protected String fileDesc;

  /** The binary representation of the file. */
  @NotNull(message = "data must not be null")
  @Column(nullable = false, columnDefinition = "mediumblob")
  @Lob
  protected byte[] data;

  /** The type of document the file is attached to. */
  @NotBlank(message = "documentType must not be blank")
  @Size(min = 1, max = 10, message = "documentType must contain between 1 to 10 characters")
  @Column(nullable = false)
  protected String documentType;

  /** The id of the document the attachment belongs to. */
  @NotNull(message = "documentId must not be null")
  @Column(nullable = false)
  protected Integer documentId;

  /** The create timestamp of the attachment. */
  @NotNull(message = "createdTimestamp must not be null")
  @Column(nullable = false)
  protected LocalDateTime createdTimestamp;

  /**
   * Instantiates a new Attachment.
   *
   * @param fileName the file name
   * @param fileType the file type
   * @param fileDesc the file description
   * @param data the data
   * @param documentType the document type
   * @param documentId the document id
   */
  public Attachment(
      String fileName,
      String fileType,
      String fileDesc,
      byte[] data,
      String documentType,
      int documentId) {

    this.fileName = fileName;
    this.fileType = fileType;
    this.fileDesc = fileDesc;
    this.data = data;
    this.documentType = documentType;
    this.documentId = documentId;
    this.createdTimestamp = LocalDateTime.now();
  }
}
