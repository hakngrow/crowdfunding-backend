package com.aestus.api.profile.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.profile.model.UserProfile;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with a User Profile as the payload. Used for schema documentation in
 * Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithUserProfile extends ResponseMessage {
  @Override
  public UserProfile getData() {
    return (UserProfile) super.getData();
  }

  /**
   * Sets data payload.
   *
   * @param profile the user profile
   */
  public void setData(UserProfile profile) {
    super.setData(profile);
  }
}
