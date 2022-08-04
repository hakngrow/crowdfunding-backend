package com.aestus.api.profile;

import com.aestus.api.profile.model.UserProfile;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

/** A collection of helper functions for use in testing the Aestus microservice modules. */
@Slf4j
public class AestusProfileMicroserviceTestUtil {
  protected static final String BASE_URI_PROFILE = "/api/v1/profile/";

  /**
   * Gets the User Profile microservice base uri used in the controllers.
   *
   * @return the base uri
   */
  public static String getProfileBaseUri() {
    return BASE_URI_PROFILE;
  }

  /**
   * Get user profiles used in the setup of test cases.
   *
   * @return an array of user profiles
   */
  public static UserProfile[] getProfiles() {

    LocalDateTime registrationDate = LocalDateTime.now();

    UserProfile profile1 =
            new UserProfile(
                    "superman",
                    "11110101",
                    "clark",
                    "kent",
                    "superman@gmail.com",
                    "98760001",
                    "U",
                    registrationDate,
                    "0x12340001");

    UserProfile profile2 =
            new UserProfile(
                    "batman",
                    "22220202",
                    "bruce",
                    "wane",
                    "batman@gmail.com",
                    "98760002",
                    "S",
                    registrationDate,
                    "0x12340002");

    UserProfile profile3 =
            new UserProfile(
                    "ironman",
                    "33330303",
                    "tony",
                    "starks",
                    "ironman@gmail.com",
                    "98760003",
                    "I",
                    registrationDate,
                    "0x12340003");

    UserProfile profile4 =
            new UserProfile(
                    "spiderman",
                    "44440404",
                    "peter",
                    "parker",
                    "spiderman@gmail.com",
                    "98760004",
                    "D",
                    registrationDate,
                    "0x12340004");

    UserProfile profile5 =
            new UserProfile(
                    "drstrange",
                    "55550505",
                    "stephen",
                    "strange",
                    "drstrange@gmail.com",
                    "98760005",
                    "U",
                    registrationDate,
                    "0x12340005");

    return new UserProfile[]{profile1, profile2, profile3, profile4, profile5};
  }

  /**
   * Gets user profiles as a list.
   *
   * @return the user profiles list
   */
  public static List<UserProfile> getProfilesList() {
    return Arrays.asList(getProfiles());
  }

  /**
   * Gets user profiles as a map with the username as the key.
   *
   * @return the user profiles map
   */
  public static HashMap<String, UserProfile> getProfilesMap() {

    HashMap<String, UserProfile> map = new HashMap<>();

    for (UserProfile profile : getProfiles()) {
        map.put(profile.getUsername(), profile);
    }

    return map;
  }
  /**
   * Gets the Jackson object mapper loaded in the Spring framework.
   *
   * @return the object mapper
   */
  public static ObjectMapper getObjectMapper() {

    ObjectMapper objectMapper = new ObjectMapper();

    objectMapper.findAndRegisterModules();

    return objectMapper;
  }

  /**
   * Checks for equality of 2 LocalDateTime parameters. This function is needed when comparing a
   * LocalDateTime generate in-program against one retrieved from the database. After an in-program
   * LocalDateTime is persisted in the database, it loses some precision.
   *
   * @param date1 the date 1
   * @param date2 the date 2
   * @return {@code true} if the dates are equal after normalizing the precision to thousandths of a second.
   */
  public static boolean isEqual(LocalDateTime date1, LocalDateTime date2) {

    String strDate1 = date1.toString();
    String strDate2 = date2.toString();

    log.info("date1: " + strDate1 + ", date2: " + strDate2);
    strDate1 = strDate1.substring(0, 23);
    strDate2 = strDate2.substring(0, 23);
    log.info("date1: " + strDate1 + ", date2: " + strDate2);

    return strDate1.equals(strDate2);
  }
}
