package com.aestus.api.transaction.model;

import com.google.common.hash.Hashing;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** The type Transaction. */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "transactions",
    indexes = {
      @Index(
          name = "idx_senderWalletId_createdTimestamp",
          columnList = "senderWalletId, createdTimestamp DESC"),
      @Index(
          name = "idx_receiverWalletId_createdTimestamp",
          columnList = "receiverWalletId, createdTimestamp DESC"),
    })
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "type must not be blank")
  @Size(min = 1, max = 10, message = "type must contain between 1 to 10 characters")
  @Column(nullable = false)
  private String type;

  @NotBlank(message = "senderWalletId must not be blank")
  @Size(min = 10, max = 100, message = "senderWalletId must contain between 10 to 100 characters")
  @Pattern(regexp = "[a-zA-Z0-9]+", message = "senderWalletId must not contain special characters")
  @Column(nullable = false)
  private String senderWalletId;

  @NotNull(message = "senderAmount must not be Null")
  @Column(nullable = false)
  private Long senderAmount;

  @NotNull(message = "senderBalance must not be Null")
  @Min(value = 0, message = "senderBalance must be >= 0")
  @Column(nullable = false)
  private Long senderBalance;

  @NotBlank(message = "receiverWalletId must not be blank")
  @Size(min = 10, max = 100, message = "receiverWalletId must contain between 10 to 100 characters")
  @Pattern(
      regexp = "[a-zA-Z0-9]+",
      message = "receiverWalletId must not contain special characters")
  @Column(nullable = false)
  private String receiverWalletId;

  @NotNull(message = "receiverAmount must not be Null")
  @Column(nullable = false)
  private Long receiverAmount;

  @NotNull(message = "receiverBalance must not be Null")
  @Min(value = 0, message = "receiverBalance must be >= 0")
  @Column(nullable = false)
  private Long receiverBalance;

  @NotNull(message = "createdTimestamp must not be Null")
  @Column(nullable = false)
  private LocalDateTime createdTimestamp;

  /** */
  @Column(nullable = false)
  private String hash;

  /**
   * Instantiates a new Transaction.
   *
   * @param type the type of transaction
   * @param receiverWalletId the receiver wallet id
   * @param senderWalletId the sender wallet id
   * @param receiverAmount the receiver transacted amount
   * @param senderAmount the sender transacted amount
   * @param receiverBalance the receiver new balance
   * @param senderBalance the sender new balance
   */
  public Transaction(
      String type,
      String senderWalletId,
      Long senderAmount,
      Long senderBalance,
      String receiverWalletId,
      Long receiverAmount,
      Long receiverBalance) {
    this.type = type;
    this.senderWalletId = senderWalletId;
    this.senderAmount = senderAmount;
    this.senderBalance = senderBalance;
    this.receiverWalletId = receiverWalletId;
    this.receiverAmount = receiverAmount;
    this.receiverBalance = receiverBalance;
    this.createdTimestamp = LocalDateTime.now();

    // Generate a hash based on the string concatenation the above variables
    this.setHash();
  }

  protected String getSignature() {
    return this.type
        + this.senderWalletId
        + this.senderAmount
        + this.senderBalance
        + this.receiverWalletId
        + this.receiverAmount
        + this.receiverBalance
        + this.createdTimestamp;
  }

  public void setHash() {
    this.hash = Hashing.sha256().hashString(getSignature(), StandardCharsets.UTF_8).toString();
  }
}
