package com.aestus.api.profile.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.profile.model.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with an array of User Profiles as the payload. Used for schema
 * documentation in Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithUserProfiles extends ResponseMessage {
  @Override
  public UserProfile[] getData() {
    return (UserProfile[]) super.getData();
  }

  /**
   * Sets data payload.
   *
   * @param profiles the array of user profiles
   */
  public void setData(UserProfile[] profiles) {
    super.setData(profiles);
  }
}
