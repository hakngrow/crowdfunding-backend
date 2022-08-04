package com.aestus.api.profile.service;

import com.aestus.api.profile.model.UserProfile;

import java.util.Optional;

/**
 * The ProfileService interface provides access to application functionality and features for user
 * profiles. It acts as a proxy or endpoint to the service. The specific implementation can be found
 * in the {@code ProfileServiceImpl} class in the {@code impl} package.
 */
public interface ProfileService {
  /**
   * Gets all user profiles.
   *
   * @return the all user profiles
   */
  Iterable<UserProfile> getAllProfiles();

  /**
   * Gets a user profile by id.
   *
   * @param id the id
   * @return the user profile by id
   */
  Optional<UserProfile> getProfileById(int id);

  /**
   * Gets user profile by username.
   *
   * @param username the username
   * @return the user profile by username
   */
  Optional<UserProfile> getProfileByUsername(String username);

  /**
   * Gets user profile by email address.
   *
   * @param email the email address
   * @return the user profile by email address
   */
  Optional<UserProfile> getProfileByEmail(String email);

  /**
   * Creates a user profile.
   *
   * @param profile the user profile
   * @return the user profile with generated id
   */
  UserProfile createProfile(UserProfile profile);

  /**
   * Updates the user profile.
   *
   * @param profile the user profile
   * @return the user profile
   */
  UserProfile updateProfile(UserProfile profile);

  /**
   * Delete user profile by id.
   *
   * @param id the id
   */
  void deleteProfileById(int id);

  /** Delete all user profiles. */
  void deleteAllProfiles();
}
