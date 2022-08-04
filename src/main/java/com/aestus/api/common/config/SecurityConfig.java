package com.aestus.api.common.config;

import com.aestus.api.common.filter.JwtTokenAuthenticationFilter;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/** The Security configuration for the Spring Boot application. */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("!test")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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

  /**
   * Java web token authentication filter for request filtering.
   *
   * @return the jwt token authentication filter
   */
  @Bean
  public JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter() {
    return new JwtTokenAuthenticationFilter();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "UPDATE", "DELETE"));
    // configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(
        Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    // Enable cors but disable csrf as apis are stateless and using jwt
    http.cors()
        .and()
        .csrf()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    // Request filtering
    http.addFilterBefore(jwtTokenAuthenticationFilter(), BasicAuthenticationFilter.class);
    // .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

    http.authorizeRequests()
        // Permitted requests
        .antMatchers(
            "/**/public",
            "/**/ping",
            "/swagger/**",
            "/swagger-ui/**",
            "/images/**",
            "/v3/api-docs/**",
            "/api/v1/authenticate",
            "/api/v1/encrypt/**",
            "/api/v1/blockchain/wallet/address")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/api/v1/profile/")
        .permitAll()

        // Authenticated requests
        .anyRequest()
        .authenticated();
  }
}
