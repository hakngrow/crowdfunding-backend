package com.aestus.api.profile.repository;

import com.aestus.api.profile.model.UserProfile;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * The ProfileRepository extends the {@code CrudRepository} for basic CRUD operations on a RDBMS via
 * Hibernate.
 */
public interface ProfileRepository extends CrudRepository<UserProfile, Integer> {
  /**
   * Find by username optional.
   *
   * @param username the username
   * @return a container {@code UserProfile} object which may or may not contain a non-null value
   *     depending on the outcome of the search.
   */
  Optional<UserProfile> findByUsername(String username);

  /**
   * Find by email address optional.
   *
   * @param email the username
   * @return a container {@code UserProfile} object which may or may not contain a non-null value
   *     depending on the outcome of the search.
   */
  Optional<UserProfile> findByEmail(String email);
}
