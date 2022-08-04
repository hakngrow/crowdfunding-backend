package com.aestus.api.common.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** The disabled Security configuration for the Spring Boot application. */
@Profile({"nojwt"})
@Slf4j
@Configuration
public class NoSecurityConfig extends WebSecurityConfigurerAdapter {

  /**
   * Password encoder using BCrypt password-hashing function.
   *
   * @return the BCrypt password encoder
   */
  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Java web token authentication manager.
   *
   * @return the authentication manager
   * @throws Exception the exception
   */
  @Bean
  public AuthenticationManager jwtAuthenticationManager() throws Exception {
    return super.authenticationManager();
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    // Ignore all routes
    web.ignoring().antMatchers("/**");
  }
}
