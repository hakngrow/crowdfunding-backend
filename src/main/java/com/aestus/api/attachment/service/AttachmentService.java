package com.aestus.api.attachment.service;

import com.aestus.api.attachment.model.Attachment;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.Optional;

import java.util.stream.Stream;

/**
 * The AttachmentService interface provides access to application functionality and features for
 * file attachments . It acts as a proxy or endpoint to the service. The specific implementation can
 * be found in the {@code AttachmentServiceImpl} class in the {@code impl} package.
 */
public interface AttachmentService {
  /**
   * Gets all attachments.
   *
   * @return the all attachments
   */
  Stream<Attachment> getAllAttachments();

  /**
   * Gets attachments by document id.
   *
   * @param documentId the document id
   * @return the attachments by document id
   */
  Stream<Attachment> getAttachmentsByDocumentId(int documentId);

  /**
   * Gets attachment by id.
   *
   * @param id the id
   * @return the attachment by id
   */
  Optional<Attachment> getAttachmentById(int id);

  /**
   * Create attachment attachment.
   *
   * @param file the file
   * @param fileDesc the description of the file
   * @param documentType the type of document the file is attached to
   * @param documentId the id of document the file is attached to
   * @return the attachment
   * @throws IOException the io exception
   */
  Attachment createAttachment(
      MultipartFile file, String fileDesc, String documentType, int documentId) throws IOException;

  /**
   * Update file description of the attachment.
   *
   * @param id the id
   * @param description the file description
   * @return the attachment
   */
  Attachment updateFileDescription(int id, String description);

  /**
   * Delete attachment by id.
   *
   * @param id the id
   */
  void deleteAttachmentById(int id);

  /**
   * Delete attachments by document id.
   *
   * @param documentId the document id
   */
  void deleteAttachmentsByDocumentId(int documentId);

  /** Delete all attachments. */
  void deleteAllAttachments();
}
