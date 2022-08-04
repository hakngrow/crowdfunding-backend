package com.aestus.api.common.filter;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.common.util.JWTUtils;
import com.aestus.api.common.service.impl.UserDetailsServiceImpl;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {
  @Autowired private UserDetailsServiceImpl userDetailsServiceImpl;
  @Autowired private JWTUtils jwtUtils;

  private String getResponseMessageAsJson(Exception ex, String uri) throws JsonProcessingException {

    String reason;

    if (JwtException.class.isInstance(ex)) reason = ex.getMessage();
    else if (IllegalArgumentException.class.isInstance(ex))
      reason = "Jwt token must not be null, empty or only whitespace";
    else reason = "Unknown exception";

    ResponseMessage msg = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), reason, uri);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();

    return objectMapper.writeValueAsString(msg);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    log.info("Filtering request url: " + request.getRequestURL());

    String token = jwtUtils.getToken(request);

    if (!Objects.isNull(token)) {
      try {

        String username = jwtUtils.getUsernameFromToken(token);

        if (!Objects.isNull(username) && jwtUtils.isValidToken(token)) {

          UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

          // Possibly remove password from the authentication token
          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());

          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      } catch (JwtException jwtEx) {
        log.error(jwtEx.getMessage());
      } catch (IllegalArgumentException iaEx) {
        log.error(iaEx.getMessage());
      }
    }

    filterChain.doFilter(request, response);
  }
}
