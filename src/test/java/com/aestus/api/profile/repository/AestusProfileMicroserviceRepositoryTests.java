package com.aestus.api.profile.repository;

import com.aestus.api.profile.AestusProfileMicroserviceTestUtil;
import com.aestus.api.profile.model.UserProfile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import java.util.*;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AestusProfileMicroserviceRepositoryTests {
  private static String BASE_URI;
  private static ObjectMapper objectMapper;
  private static UserProfile[] profiles;

  /*
  Explore testing with test containers

  @Container
  MySQLContainer mySQLContainer = (MySQLContainer) new MySQLContainer("mysql:latest")
          .withDatabaseName("spring-reddit-test-db")
          .withUsername("testuser")
          .withPassword("pass");
   */
  @Autowired private ProfileRepository profileRepository;

  @Autowired private EntityManager entityManager;

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
  @Rollback(value = false)
  @Order(1)
  public void given_DeleteAll_when_FindAll_then_ReturnEmptyList() {

    profileRepository.deleteAll();

    ArrayList<UserProfile> list = new ArrayList<>();

    Iterator<UserProfile> iterator = profileRepository.findAll().iterator();

    iterator.forEachRemaining(list::add);

    assertThat(list.size()).isEqualTo(0);
  }

  @Test
  @Rollback(value = false)
  @Order(2)
  public void given_UserProfiles_when_Create_then_ReturnUserProfilesWithId() {

    UserProfile saved;

    for (UserProfile profile : lstProfiles) {

      saved = profileRepository.save(profile);

      assertThat(saved.getId()).isNotNull();
      assertThat(saved.getUsername()).isEqualTo(profile.getUsername());
      assertThat(saved.getPassword()).isEqualTo(profile.getPassword());
      assertThat(saved.getFirstName()).isEqualTo(profile.getFirstName());
      assertThat(saved.getLastName()).isEqualTo(profile.getLastName());
      assertThat(saved.getEmail()).isEqualTo(profile.getEmail());
      assertThat(saved.getPhone()).isEqualTo(profile.getPhone());
      assertThat(saved.getRegistrationDate()).isEqualTo(profile.getRegistrationDate());
      assertThat(saved.getUserType()).isEqualTo(profile.getUserType());
      assertThat(saved.getWalletId()).isEqualTo(profile.getWalletId());
    }
  }

  @Test
  @Order(3)
  public void given_Id_when_FindById_then_ReturnUserProfile() {

    Optional<UserProfile> optProfile = profileRepository.findById(1);

    assertThat(optProfile.isPresent()).isTrue();
    assertThat(optProfile.get().getUsername()).isEqualTo("superman");
  }

  @Test
  @Order(4)
  public void given_Username_when_FindByUsername_then_ReturnUserProfile() {

    Optional<UserProfile> optProfile = profileRepository.findByUsername("ironman");

    assertThat(optProfile.isPresent()).isTrue();
    assertThat(optProfile.get().getFirstName()).isEqualTo("tony");
    assertThat(optProfile.get().getLastName()).isEqualTo("starks");
  }

  @Test
  @Order(5)
  public void when_FindAll_then_ReturnAllUserProfiles() {

    UserProfile profile;

    for (UserProfile retrieved : profileRepository.findAll()) {

      profile = mapProfiles.get(retrieved.getUsername());

      assertThat(retrieved.getId()).isNotNull();
      assertThat(retrieved.getUsername()).isEqualTo(profile.getUsername());
      assertThat(retrieved.getPassword()).isEqualTo(profile.getPassword());
      assertThat(retrieved.getFirstName()).isEqualTo(profile.getFirstName());
      assertThat(retrieved.getLastName()).isEqualTo(profile.getLastName());
      assertThat(retrieved.getEmail()).isEqualTo(profile.getEmail());
      assertThat(retrieved.getPhone()).isEqualTo(profile.getPhone());

      assertThat(
              AestusProfileMicroserviceTestUtil.isEqual(
                  retrieved.getRegistrationDate(), profile.getRegistrationDate()))
          .isTrue();

      assertThat(retrieved.getUserType()).isEqualTo(profile.getUserType());
      assertThat(retrieved.getWalletId()).isEqualTo(profile.getWalletId());
    }
  }

  @Test
  @Order(6)
  @Rollback(value = false)
  public void given_UserProfileToModify_when_Update_then_ReturnUpdatedUserProfile() {
    Optional<UserProfile> optProfile = profileRepository.findById(3);

    assertThat(optProfile.isPresent()).isTrue();

    UserProfile profile = optProfile.get();

    int id = profile.getId();

    profile.setUsername("wolverine");
    profile.setPassword("66660606");
    profile.setFirstName("james");
    profile.setLastName("howlett");
    profile.setEmail("wolverine@gmail.com");
    profile.setPhone("98760006");
    profile.setUserType("I");
    profile.setRegistrationDate(LocalDateTime.now());
    profile.setWalletId("0x12340006");

    UserProfile updated = profileRepository.save(profile);

    assertThat(updated.getId()).isEqualTo(id);
    assertThat(updated.getUsername()).isEqualTo(profile.getUsername());
    assertThat(updated.getPassword()).isEqualTo(profile.getPassword());
    assertThat(updated.getFirstName()).isEqualTo(profile.getFirstName());
    assertThat(updated.getLastName()).isEqualTo(profile.getLastName());
    assertThat(updated.getEmail()).isEqualTo(profile.getEmail());
    assertThat(updated.getPhone()).isEqualTo(profile.getPhone());
    assertThat(updated.getRegistrationDate()).isEqualTo(profile.getRegistrationDate());
    assertThat(updated.getUserType()).isEqualTo(profile.getUserType());
    assertThat(updated.getWalletId()).isEqualTo(profile.getWalletId());
  }

  @Test
  @Order(7)
  @Rollback(value = false)
  public void given_DeleteById_when_FindById_then_ReturnOptionalWithNullValue() {

    profileRepository.deleteById(3);

    Optional<UserProfile> optProfile = profileRepository.findById(3);

    assertThat(optProfile.isPresent()).isFalse();
  }

  @Test
  @Order(8)
  public void given_UsernameIsNull_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {

    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setUsername(null);

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(9)
  public void given_UsernameIsEmptyString_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setUsername("");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(10)
  public void
      given_UsernameIsLessThan6Characters_when_Create_then_ThrowConstraintViolationException()
          throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setUsername("12345");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(11)
  public void
      given_UsernameIsMoreThan12Characters_when_Create_then_ThrowConstraintViolationException()
          throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setUsername("1234567890123");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(12)
  public void
      given_UsernameContainsSpecialCharacters_when_Create_then_ThrowConstraintViolationException()
          throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setUsername("123456*#$");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(13)
  public void given_UsernameIsNotUnique_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setId(null);

    assertThrows(
        PersistenceException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(14)
  public void given_PasswordIsNull_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {

    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setPassword(null);

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(15)
  public void given_PasswordIsEmptyString_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setPassword("");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(16)
  public void
      given_PasswordIsLessThan6Characters_when_Create_then_ThrowConstraintViolationException()
          throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setPassword("12345");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(17)
  public void
      given_PasswordIsMoreThan12Characters_when_Create_then_ThrowConstraintViolationException()
          throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setPassword("1234567890123");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(18)
  public void given_FirstNameIsNull_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {

    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setFirstName(null);

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(19)
  public void given_FirstNameIsEmptyString_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setFirstName("");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(20)
  public void
      given_FirstNameIsLessThan2Characters_when_Create_then_ThrowConstraintViolationException()
          throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setFirstName("1");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(21)
  public void
      given_FirstNameIsMoreThan30Characters_when_Create_then_ThrowConstraintViolationException()
          throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setFirstName("1234567890123456789012345678901");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(22)
  public void given_LastNameIsNull_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {

    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setLastName(null);

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(23)
  public void given_LastNameIsEmptyString_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setLastName("");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(24)
  public void
      given_LastNameIsLessThan2Characters_when_Create_then_ThrowConstraintViolationException()
          throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setLastName("1");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(25)
  public void
      given_LastNameIsMoreThan30Characters_when_Create_then_ThrowConstraintViolationException()
          throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setLastName("1234567890123456789012345678901");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(26)
  public void given_EmailIsNull_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {

    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setEmail(null);

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(27)
  public void given_EmailIsEmptyString_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setEmail("");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(28)
  public void given_EmailFormatIsInvalid_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setEmail("12345678");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(29)
  public void given_PhoneIsLessThan7Characters_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setPhone("123456");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(30)
  public void given_PhoneIsMoreThan15Characters_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setPhone("1234567890123456");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(31)
  public void
      given_PhoneContainCharactersOtherThanDigits_when_Create_then_ThrowConstraintViolationException()
          throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setPhone("123abc*$#");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(32)
  public void given_UserTypeIsNull_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {

    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setUserType(null);

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(33)
  public void given_UserTypeIsEmptyString_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setUserType("");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(34)
  public void
      given_UserTypeIsMoreThan1Character_when_Create_then_ThrowConstraintViolationException()
          throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setUserType("12");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(35)
  public void given_RegistrationDateIsNull_when_Create_then_ThrowConstraintViolationException()
      throws JsonProcessingException {

    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setRegistrationDate(null);

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(36)
  public void
      given_RegistrationDateIsInTheFuture_when_Create_then_ThrowConstraintViolationException()
          throws JsonProcessingException {

    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setRegistrationDate(LocalDateTime.now().plusDays(1));

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(37)
  public void
      given_WalletIdIsLessThan10Characters_when_Create_then_ThrowConstraintViolationException()
          throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setWalletId("123456789");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(38)
  public void
      given_WalletIdIsMoreThan100Characters_when_Create_then_ThrowConstraintViolationException()
          throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    String walletId =
        "12345678901234567890123456789012345678901234567890"
            + "123456789012345678901234567890123456789012345678901";

    profile.setWalletId(walletId);

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }

  @Test
  @Order(39)
  public void
      given_WalletIdContainSpecialCharacters_when_Create_then_ThrowConstraintViolationException()
          throws JsonProcessingException {
    UserProfile profile =
        objectMapper.readValue(
            objectMapper.writeValueAsString(lstProfiles.get(0)), UserProfile.class);

    profile.setWalletId("1234567890@#$%*");

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          profileRepository.save(profile);
          entityManager.flush();
        });
  }
}
