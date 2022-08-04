package com.aestus.api.profile.service.impl;

import com.aestus.api.profile.service.ProfileService;
import com.aestus.api.profile.model.UserProfile;
import com.aestus.api.profile.repository.ProfileRepository;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The implementation of the User Profile service. This implementation uses a {@code CrudRepository}
 * for persistence to a RDBMS via Hibernate.
 */
@Service
@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService {
  private final ProfileRepository profileRepository;

  public Iterable<UserProfile> getAllProfiles() {
    return profileRepository.findAll();
  }

  public Optional<UserProfile> getProfileById(int id) {
    return profileRepository.findById(id);
  }

  public Optional<UserProfile> getProfileByUsername(String username) {
    return profileRepository.findByUsername(username);
  }

  public Optional<UserProfile> getProfileByEmail(String email) {
    return profileRepository.findByEmail(email);
  }

  public UserProfile createProfile(UserProfile profile) {
    return profileRepository.save(profile);
  }

  public UserProfile updateProfile(UserProfile profile) {
    return profileRepository.save(profile);
  }

  public void deleteProfileById(int id) {
    profileRepository.deleteById(id);
  }

  public void deleteAllProfiles() {
    profileRepository.deleteAll();
  }
}
