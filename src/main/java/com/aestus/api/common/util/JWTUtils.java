package com.aestus.api.common.util;

import io.jsonwebtoken.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/** A collection of utility functions for dealing with Java Web Tokens (JWT). */
@Slf4j
@Component
public class JWTUtils {
  private final String AUTH_HEADER_PARAM_NAME = "Authorization";
  private final String AUTH_HEADER_TOKEN_PREFIX = "Bearer";

  @Value("${com.aestus.jwt.signing-key}")
  private final String SIGNING_KEY = null;
  @Value("${com.aestus.jwt.expiration-ms}")
  private final Long EXPIRE_IN = 0L;
  private final String AUTH_HEADER_USERNAME = "username";
  private final String AUTH_HEADER_PASSWORD = "password";
  private final String AUTH_HEADER_ROLES = "roles";

  /**
   * Checks the Http request for a JWT and returns it if found.
   *
   * @param request the Http servlet request
   * @return the token
   */
  public String getToken(HttpServletRequest request) {

    String authToken = request.getHeader(AUTH_HEADER_PARAM_NAME);

    if (Objects.isNull(authToken)) {
      return null;
    }

    return authToken.substring(AUTH_HEADER_TOKEN_PREFIX.length());
  }

  /**
   * Gets username from token.
   *
   * @param token the JWT token
   * @return the username from token
   * @throws JwtException the jwt exception
   * @throws IllegalArgumentException the illegal argument exception
   */
  public String getUsernameFromToken(String token) throws JwtException, IllegalArgumentException {

    final Claims claims =
        Jwts.parser().setSigningKey(SIGNING_KEY.getBytes()).parseClaimsJws(token).getBody();

    return String.valueOf(claims.get(AUTH_HEADER_USERNAME));
  }

  /**
   * Checks if the JWT token has expired.
   *
   * @param token the JWT token
   * @return true if token has not expired
   * @throws JwtException the jwt exception
   */
  public boolean isValidToken(String token) throws JwtException {

    boolean isValid = true;

    try {
      final Claims claims =
          Jwts.parser().setSigningKey(SIGNING_KEY.getBytes()).parseClaimsJws(token).getBody();
      isValid = !(claims.getExpiration().before(new Date()));
    } catch (JwtException jwtEx) {
      log.error(jwtEx.getMessage());
      throw jwtEx;
    } catch (IllegalArgumentException iaEx) {
      throw new JwtException("Jwt token must not be null, empty or only whitespace");
    }

    return isValid;
  }

  /**
   * Generate token string.
   *
   * @param username the username
   * @param authorities the authorities
   * @return the string
   */
  public String generateToken(String username, Collection<GrantedAuthority> authorities) {

    Claims claims = Jwts.claims();

    String roles =
        authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

    claims.put(AUTH_HEADER_USERNAME, username);
    claims.put(AUTH_HEADER_ROLES, roles);

    Date expiration = Date.from(Instant.ofEpochMilli(new Date().getTime() + EXPIRE_IN));

    String token =
        Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS256, SIGNING_KEY.getBytes())
            .compact();

    return token;
  }
}
