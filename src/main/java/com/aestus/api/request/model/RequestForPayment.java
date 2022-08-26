package com.aestus.api.request.model;

import com.aestus.api.profile.model.UserProfile;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

/** The type Request for payment. */
@Data
@NoArgsConstructor
public class RequestForPayment extends Request {

  /**
   * Instantiates a new Request for payment.
   *
   * @param id the id
   * @param fromProfileId the from profile id
   * @param toProfileId the to profile id
   * @param nftId the nft id
   * @param amount the amount
   * @param createdTimestamp the created timestamp
   * @param nftMetadata the nft metadata
   * @param fromProfile the from user profile
   * @param toProfile the to user profile
   */
  public RequestForPayment(
      Integer id,
      Integer fromProfileId,
      Integer toProfileId,
      Integer nftId,
      Long amount,
      LocalDateTime createdTimestamp,
      String nftMetadata,
      UserProfile fromProfile,
      UserProfile toProfile) {
    super(
        id,
        fromProfileId,
        toProfileId,
        nftId,
        null,
        Request.TYPE_RPY,
        Request.STATUS_OPEN,
        null,
        amount,
        0L,
        nftMetadata,
        createdTimestamp,
        fromProfile,
        toProfile);
  }

  /**
   * Instantiates a new request for payment from a generic {@code Request} instance.
   *
   * @param request the generic request instance
   */
  public RequestForPayment(Request request) {
    this(
        request.getId(),
        request.getFromProfileId(),
        request.getToProfileId(),
        request.getRequestId(),
        request.getCost(),
        request.getCreatedTimestamp(),
        request.getSpecifications(),
        null,
        null);
  }

  /**
   * Gets NFT token id.
   *
   * @return the NFT token id
   */
  public Integer getNftTokenId() {
    return this.getRequestId();
  }

  /**
   * Sets NFT token id.
   *
   * @param tokenId the token id
   */
  public void setNftTokenId(Integer tokenId) {
    this.setRequestId(tokenId);
  }

  /**
   * Gets NFT metadata.
   *
   * @return the nft metadata
   */
  public String getNftMetadata() {
    return this.getSpecifications();
  }

  /**
   * Sets NFT metadata.
   *
   * @param metadata the metadata
   */
  public void setNftMetadata(String metadata) {
    this.setSpecifications(metadata);
  }
}
