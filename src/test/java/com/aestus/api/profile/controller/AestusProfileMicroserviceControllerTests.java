package com.aestus.api.profile.controller;

import com.aestus.api.profile.AestusProfileMicroserviceTestUtil;
import com.aestus.api.profile.model.UserProfile;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import java.util.*;

import lombok.extern.slf4j.Slf4j;

import org.hamcrest.core.IsNull;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AestusProfileMicroserviceControllerTests {
  private static String BASE_URI;
  private static ObjectMapper objectMapper;
  private static UserProfile[] profiles;
  @Autowired private MockMvc mockMvc;

  private List<UserProfile> lstProfiles;
  private Map<String, UserProfile> mapProfiles;

  @BeforeAll
  public static void initBeforeAllTests() {
    BASE_URI = AestusProfileMicroserviceTestUtil.getProfileBaseUri();
    objectMapper = AestusProfileMicroserviceTestUtil.getObjectMapper();
    profiles = AestusProfileMicroserviceTestUtil.getProfiles();
  }

  @BeforeEach
  public void initBeforeEachTest() {
    lstProfiles = Arrays.asList(profiles);

    mapProfiles = new HashMap<>();

    for (UserProfile profile : lstProfiles) {
      mapProfiles.put(profile.getUsername(), profile);
    }
  }

  @Test
  @Order(1)
  public void when_Ping_then_ReturnDefaultMessage() throws Exception {
    this.mockMvc
        .perform(get(BASE_URI + "ping/"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is("ping pong")));
  }

  @Test
  @Order(2)
  @Rollback(value = false)
  public void when_RemoveAll_then_ReturnOkStatus() throws Exception {
    this.mockMvc.perform(delete(BASE_URI)).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @Order(3)
  @Rollback(value = false)
  public void given_RemoveAll_when_GetAllProfiles_then_ReturnEmptyArray() throws Exception {
    this.mockMvc
        .perform(get(BASE_URI))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.timestamp", is(notNullValue())))
        .andExpect(jsonPath("$.status", is(200)))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data", hasSize(0)));
  }

  /**
   * To remove trailing zeros when converting a {@code LocalDateTime} instance to a {@code String}
   */
  protected String trimTrailingZeros(LocalDateTime timestamp) {
    return timestamp.toString().replaceAll("0*$", "");
  }

  @Test
  @Order(4)
  @Rollback(value = false)
  public void given_UserProfiles_when_Create_then_ReturnUserProfilesWithId() throws Exception {

    for (UserProfile profile : lstProfiles) {

      String jsonProfile = objectMapper.writeValueAsString(profile);

      this.mockMvc
          .perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(jsonProfile))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.timestamp", is(notNullValue())))
          .andExpect(jsonPath("$.status", is(200)))
          .andExpect(jsonPath("$.data").isNotEmpty())
          .andExpect(jsonPath("$.data.id").isNumber())
          .andExpect(jsonPath("$.data.username", is(profile.getUsername())))
          .andExpect(jsonPath("$.data.password", is(profile.getPassword())))
          .andExpect(jsonPath("$.data.firstName", is(profile.getFirstName())))
          .andExpect(jsonPath("$.data.lastName", is(profile.getLastName())))
          .andExpect(jsonPath("$.data.email", is(profile.getEmail())))
          .andExpect(jsonPath("$.data.phone", is(profile.getPhone())))
          // Need to remove trailing zeros of LocalDateTime value when converting to String
          .andExpect(
              jsonPath(
                  "$.data.registrationDate", is(trimTrailingZeros(profile.getRegistrationDate()))))
          .andExpect(jsonPath("$.data.userType", is(profile.getUserType())))
          .andExpect(jsonPath("$.data.walletId", is(profile.getWalletId())));
    }
  }

  @Test
  @Order(5)
  public void given_Id_when_GetById_then_ReturnUserProfile() throws Exception {

    this.mockMvc
        .perform(get(BASE_URI + "id/3"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.timestamp", is(notNullValue())))
        .andExpect(jsonPath("$.status", is(200)))
        .andExpect(jsonPath("$.data.username", is("ironman")));
  }

  @Test
  @Order(6)
  public void given_Username_when_GetByUsername_then_ReturnUserProfile() throws Exception {

    this.mockMvc
        .perform(get(BASE_URI + "username/spiderman"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.timestamp", is(notNullValue())))
        .andExpect(jsonPath("$.status", is(200)))
        .andExpect(jsonPath("$.data.username", is("spiderman")));
  }

  @Test
  @Order(7)
  public void when_GetAll_then_ReturnAllUserProfiles() throws Exception {

    this.mockMvc
        .perform(get(BASE_URI))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.timestamp", is(notNullValue())))
        .andExpect(jsonPath("$.status", is(200)))
        .andExpect(jsonPath("$.data").isNotEmpty())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data", hasSize(5)));
  }

  @Test
  @Order(8)
  @Rollback(value = false)
  public void given_UserProfileToModify_when_Update_then_ReturnUpdatedUserProfile()
      throws Exception {

    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(2)), UserProfile.class);

    profile.setId(3);
    profile.setUsername("wolverine");
    profile.setPassword("66660606");
    profile.setFirstName("james");
    profile.setLastName("howlett");
    profile.setEmail("wolverine@gmail.com");
    profile.setPhone("98760006");
    profile.setUserType("I");
    profile.setRegistrationDate(LocalDateTime.now());
    profile.setWalletId("0x12340006");

    String jsonProfile = objectMapper.writeValueAsString(profile);

    this.mockMvc
        .perform(put(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(jsonProfile))
        .andDo(print())
        .andExpect(jsonPath("$.timestamp", is(notNullValue())))
        .andExpect(jsonPath("$.status", is(200)))
        .andExpect(jsonPath("$.data.id", is(3)))
        .andExpect(jsonPath("$.data.username", is(profile.getUsername())))
        .andExpect(jsonPath("$.data.password", is(profile.getPassword())))
        .andExpect(jsonPath("$.data.firstName", is(profile.getFirstName())))
        .andExpect(jsonPath("$.data.lastName", is(profile.getLastName())))
        .andExpect(jsonPath("$.data.email", is(profile.getEmail())))
        .andExpect(jsonPath("$.data.phone", is(profile.getPhone())))
        // Need to remove trailing zeros of LocalDateTime value when converting to String
        .andExpect(
            jsonPath(
                "$.data.registrationDate", is(trimTrailingZeros(profile.getRegistrationDate()))))
        .andExpect(jsonPath("$.data.userType", is(profile.getUserType())))
        .andExpect(jsonPath("$.data.walletId", is(profile.getWalletId())));
  }

  @Test
  @Order(9)
  @Rollback(value = false)
  public void given_RemoveById_when_GetById_then_ReturnNothing() throws Exception {

    this.mockMvc
        .perform(delete(BASE_URI + "id/5").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.timestamp", is(notNullValue())))
        .andExpect(jsonPath("$.status", is(200)));

    this.mockMvc
        .perform(get(BASE_URI + "id/5").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.timestamp", is(notNullValue())))
        .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @Test
  @Order(10)
  public void given_UsernameIsNull_when_Create_then_ReturnBadRequestStatus() throws Exception {

    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setUsername(null);

    String jsonProfile = objectMapper.writeValueAsString(profile);

    this.mockMvc
        .perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(jsonProfile))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp", is(notNullValue())))
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.message", is("username must not be blank")))
        .andExpect(jsonPath("$.data").value(IsNull.nullValue()));
  }

  @Test
  @Order(11)
  public void given_UsernameIsEmpty_when_Create_then_ReturnBadRequestStatus() throws Exception {

    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setUsername("");

    String jsonProfile = objectMapper.writeValueAsString(profile);

    ArrayList<String> messages = new ArrayList<>();
    messages.add("username must not be blank");
    messages.add("username must contain between 6 to 12 characters");
    messages.add("username must not contain special characters");

    this.mockMvc
        .perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(jsonProfile))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp", is(notNullValue())))
        .andExpect(jsonPath("$.status", is(400)))

        // username as an empty string will trigger multiple constraint violations
        .andExpect(jsonPath("$.message").value(containsString("username must not be blank")))
        .andExpect(jsonPath("$.data").value(IsNull.nullValue()));
  }

  @Test
  @Order(12)
  public void given_UsernameIsLessThan6Characters_when_Create_then_ReturnBadRequestStatus()
      throws Exception {

    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setUsername("12345");

    String jsonProfile = objectMapper.writeValueAsString(profile);

    this.mockMvc
        .perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(jsonProfile))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp", is(notNullValue())))
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.message", is("username must contain between 6 to 12 characters")))
        .andExpect(jsonPath("$.data").value(IsNull.nullValue()));
  }

  @Test
  @Order(13)
  public void given_UsernameIsMoreThan12Characters_when_Create_then_ReturnBadRequestStatus()
      throws Exception {

    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setUsername("1234567890123");

    String jsonProfile = objectMapper.writeValueAsString(profile);

    this.mockMvc
        .perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(jsonProfile))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp", is(notNullValue())))
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.message", is("username must contain between 6 to 12 characters")))
        .andExpect(jsonPath("$.data").value(IsNull.nullValue()));
  }

  @Test
  @Order(14)
  public void given_UsernameContainsSpecialCharacters_when_Create_then_ReturnBadRequestStatus()
      throws Exception {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setUsername("123456*#$");

    String jsonProfile = objectMapper.writeValueAsString(profile);

    this.mockMvc
        .perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(jsonProfile))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp", is(notNullValue())))
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.message", is("username must not contain special characters")))
        .andExpect(jsonPath("$.data", nullValue()));
  }

  @Test
  @Order(15)
  public void given_UsernameIsNotUnique_when_Create_then_ReturnInternalServerErrorStatus()
      throws Exception {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    String jsonProfile = objectMapper.writeValueAsString(profile);

    this.mockMvc
        .perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(jsonProfile))
        .andDo(print())
        // The username not unique exception is thrown at the database level, hence a status 500
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.timestamp", is(notNullValue())))
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message").value(containsString("ConstraintViolationException")))
        .andExpect(jsonPath("$.data", nullValue()));
  }

  @Test
  @Order(16)
  @Rollback(value = false)
  public void given_RemoveAll_when_GetAll_then_ReturnEmptyArray() throws Exception {
    this.mockMvc
        .perform(delete(BASE_URI).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.timestamp", is(notNullValue())))
        .andExpect(jsonPath("$.status", is(200)));

    this.mockMvc
        .perform(get(BASE_URI).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.timestamp", is(notNullValue())))
        .andExpect(jsonPath("$.status", is(200)))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data").isEmpty());
  }
}
