package com.aestus.api.request.repository;

import com.aestus.api.request.model.Request;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * The RequestRepository extends the {@code CrudRepository} for basic CRUD operations on a RDBMS via
 * Hibernate.
 */
public interface RequestRepository extends CrudRepository<Request, Integer> {

  /**
   * Find requests by {@code toProfileId}.
   *
   * @param profileId the from profile id
   * @return list of requests
   */
  List<Request> findByFromProfileId(Integer profileId);

  /**
   * Find requests by {@code fromProfileId} and {@code type}.
   *
   * @param profileId the from profile id
   * @param type the type of request
   * @return list of requests
   */
  List<Request> findByFromProfileIdAndType(Integer profileId, String type);

  /**
   * Find requests by {@code fromProfileId}, {@code type} and {@code status}.
   *
   * @param profileId the from profile id
   * @param type the type of request
   * @param status the status of the request
   * @return list of requests
   */
  List<Request> findByFromProfileIdAndTypeAndStatus(Integer profileId, String type, String status);

  /**
   * Find requests by {@code toProfileId}.
   *
   * @param profileId the to profile id
   * @return list of requests
   */
  List<Request> findByToProfileId(Integer profileId);

  /**
   * Find requests by {@code toProfileId} and {@code type}.
   *
   * @param profileId the to profile id
   * @param type the request type
   * @return list of requests
   */
  List<Request> findByToProfileIdAndType(Integer profileId, String type);

  /**
   * Find requests by {@code toProfileId}, {@code type} and {@code status}.
   *
   * @param profileId the to profile id
   * @param type the request type
   * @param status the status of the request
   * @return list of requests
   */
  List<Request> findByToProfileIdAndTypeAndStatus(Integer profileId, String type, String status);

  /**
   * Find requests by {@code fromProfileId} and {@code toProfileId}.
   *
   * @param profileId the profile id used to match the from and to profile id
   * @return list of requests
   */
  @Query("SELECT r FROM Request r WHERE r.fromProfileId = :profileId OR r.toProfileId = :profileId")
  List<Request> findByProfileId(Integer profileId);

  /**
   * Find requests by {@code type}.
   *
   * @param type the request type
   * @return list of requests
   */
  List<Request> findByType(String type);

  /**
   * Find requests by {@code type} and {@code status}.
   *
   * @param type the request type
   * @param status the status of the request
   * @return list of requests
   */
  List<Request> findByTypeAndStatus(String type, String status);

  /**
   * Find requests by {@code requestId}.
   *
   * @param requestId the linked request id
   * @return list of requests
   */
  List<Request> findByRequestId(Integer requestId);

  /**
   * Update the status of the request identified by {@code id}.
   *
   * @param id the request id
   * @param status the status to be updated to
   */
  @Modifying
  @Transactional
  @Query("UPDATE FROM Request r SET r.status = :status WHERE r.id = :id")
  void updateStatus(Integer id, String status);

  /**
   * Update the repayment of the request identified by {@code id}.
   *
   * @param id the request id
   * @param repayment the repayment to be made after the request is completed
   */
  @Modifying
  @Transactional
  @Query("UPDATE FROM Request r SET r.repayment = :repayment WHERE r.id = :id")
  void updateRepayment(Integer id, Long repayment);

  /**
   * Delete requests by {@code fromProfileId).
   *
   * @param profileId the from profile id
   * @return list of deleted requests
   */
  @Transactional
  List<Request> deleteByFromProfileId(Integer profileId);

  /**
   * Delete requests by {@code toProfileId}.
   *
   * @param profileId the to profile id
   * @return list of deleted requests
   */
  @Transactional
  List<Request> deleteByToProfileId(Integer profileId);

  /**
   * Delete requests by {@code fromProfileId} and {@code toProfileId} .
   *
   * @param profileId the to profile id
   * @return list of deleted requests
   */
  @Modifying
  @Transactional
  @Query("DELETE FROM Request r WHERE r.fromProfileId = :profileId OR r.toProfileId = :profileId")
  void deleteByProfileId(Integer profileId);
}
