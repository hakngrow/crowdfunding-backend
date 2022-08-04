package com.aestus.api.attachment.repository;

import com.aestus.api.attachment.model.Attachment;

import org.springframework.data.repository.CrudRepository;

/**
 * The AttachmentRepository extends the {@code CrudRepository} for basic CRUD operations on a RDBMS via
 * Hibernate.
 */
public interface AttachmentRepository extends CrudRepository<Attachment, Integer> {

  /**
   * Find by document id iterable.
   *
   * @param documentId the document id
   * @return the iterable of Attachments
   */
  public Iterable<Attachment> findByDocumentId(int documentId);

  /**
   * Delete by document id.
   *
   * @param documentId the document id
   */
  public void deleteByDocumentId(int documentId);
}
