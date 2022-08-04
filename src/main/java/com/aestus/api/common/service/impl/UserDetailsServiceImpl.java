package com.aestus.api.common.service.impl;

import com.aestus.api.profile.model.UserProfile;
import com.aestus.api.profile.repository.ProfileRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired private ProfileRepository profileRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    UserProfile profile =
        profileRepository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        String.format("User profile with username=%s not found", username)));

    List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(profile.getUserType());

    UserDetails userDetails = new User(profile.getUsername(), profile.getPassword(), authorities);

    return userDetails;
  }
}
