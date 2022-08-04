package com.aestus.api.common.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The DTO marshalled from the HTTP request to the {@code authenticate} function of the {@code
 * CommonController}*.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequestBody {

  /** The username of the authentication request. */
  private String username;

  /** The password of the authentication request. */
  private String password;
}
