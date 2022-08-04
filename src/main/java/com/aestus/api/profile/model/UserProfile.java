package com.aestus.api.profile.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import javax.validation.constraints.*;

import java.time.LocalDateTime;

/** The type User profile. */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "profiles")
public class UserProfile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  /** The Username. */
  @NotBlank(message = "username must not be blank")
  @Size(min = 6, max = 20, message = "username must contain between 6 to 20 characters")
  @Pattern(regexp = "[a-zA-Z0-9]+", message = "username must not contain special characters")
  @Column(unique = true, nullable = false)
  protected String username;

  /** The Password. */
  @NotBlank(message = "password must not be blank")
  @Size(min = 8, max = 80, message = "password must contain between 8 to 80 characters")
  @Column(nullable = false)
  protected String password;

  /** The First name. */
  @NotBlank(message = "firstName must not be blank")
  @Size(min = 2, max = 30, message = "firstName must contain between 2 to 30 characters")
  @Column(nullable = false)
  protected String firstName;

  /** The Last name. */
  @NotBlank(message = "lastName must not be blank")
  @Size(min = 2, max = 30, message = "firstName must contain between 2 to 30 characters")
  @Column(nullable = false)
  protected String lastName;

  /** The Email. */
  @NotBlank(message = "email must not be blank")
  @Email(message = "email must be a valid format")
  @Column(unique = true, nullable = false)
  protected String email;

  /** The Phone. */
  @Size(min = 7, max = 15, message = "phone must contain between 7 to 15 characters")
  @Pattern(regexp = "[0-9]+", message = "phone must contain only digits")
  protected String phone;

  /** The User type. */
  @NotBlank(message = "userType must not be blank")
  @Size(min = 1, max = 1, message = "userType must contain exactly 1 character")
  @Column(nullable = false)
  protected String userType;

  @NotNull(message = "registrationDate must not be Null")
  @Column(nullable = false)
  private LocalDateTime registrationDate; // e.g. 2022-05-07T07:53:46.343+00:00

  @Size(min = 10, max = 100, message = "walletId must contain between 10 to 100 characters")
  @Pattern(regexp = "[a-zA-Z0-9]+", message = "walletId must not contain special characters")
  private String walletId;
  /**
   * Instantiates a new User profile.
   *
   * @param username the username
   * @param password the password
   * @param firstName the first name
   * @param lastName the last name
   * @param email the email
   * @param phone the phone
   * @param userType the user type
   * @param registrationDate the registration date
   * @param walletId the wallet id
   */
  public UserProfile(
      String username,
      String password,
      String firstName,
      String lastName,
      String email,
      String phone,
      String userType,
      LocalDateTime registrationDate,
      String walletId) {
    this.username = username;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phone = phone;
    this.userType = userType;
    this.registrationDate = registrationDate;
    this.walletId = walletId;
  }
}
