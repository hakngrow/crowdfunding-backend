package com.aestus.api.attachment.service.impl;

import com.aestus.api.attachment.model.Attachment;
import com.aestus.api.attachment.service.AttachmentService;

import com.aestus.api.attachment.repository.AttachmentRepository;

import java.io.IOException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * The implementation of the Attachment service. This implementation uses a {@code CrudRepository}
 * for persistence to a RDBMS via Hibernate.
 */
@Service
@AllArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

  private final AttachmentRepository attachmentRepository;

  public Stream<Attachment> getAllAttachments() {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(
            attachmentRepository.findAll().iterator(), Spliterator.ORDERED),
        false);
  }

  public Stream<Attachment> getAttachmentsByDocumentId(int documentId) {

    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(
            attachmentRepository.findByDocumentId(documentId).iterator(), Spliterator.ORDERED),
        false);
  }

  public Optional<Attachment> getAttachmentById(int id) {
    return attachmentRepository.findById(id);
  }

  /**
   * If user uploads a file >16MB (mediumblob), MySql will throw a data truncation exception,
   * stating that the data too long for column 'data'. This is a MySql specific exception ({@code
   * com.mysql.cj.jdbc.exceptions.MysqlDataTruncation}). At this stage of development, so as not to
   * bind the persistence tier to any RDBMS, this exception is allowed to propagate up the stack and
   * caught as a Hibernate DataException.
   *
   * <p>In deployment, when the RDBMS is decided, we can consider catching a DB specific exception
   * and providing a more meaningful error message to the controller.
   */
  public Attachment createAttachment(
      MultipartFile file, String fileDesc, String documentType, int documentId )
      throws IOException {

    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    Attachment attachment =
        new Attachment(fileName, file.getContentType(), fileDesc, file.getBytes(), documentType, documentId);

    return attachmentRepository.save(attachment);
  }

  public Attachment updateFileDescription(int id, String fileDesc) {

    Optional<Attachment> optAttachment = attachmentRepository.findById(id);

    if (optAttachment.isPresent()) {
      Attachment attachment = optAttachment.get();

      attachment.setFileDesc(fileDesc);

      attachmentRepository.save(attachment);

      return attachment;
    } else throw new IllegalArgumentException(String.format("Attachment with %d not found.", id));
  }

  public void deleteAttachmentById(int id) {
    attachmentRepository.deleteById(id);
  }

  public void deleteAttachmentsByDocumentId(int documentId) {
    attachmentRepository.deleteByDocumentId(documentId);
  }

  public void deleteAllAttachments() {
    attachmentRepository.deleteAll();
  }
}
