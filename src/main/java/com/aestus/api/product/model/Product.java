package com.aestus.api.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

/** The type Product. */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "products",
    indexes = {@Index(name = "idx_profileId_name", columnList = "profileId, name")})
public class Product {

  /** The Id. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  /** The profile id of the solution provider. */
  @NotNull(message = "profileId must not be Null")
  @Column(nullable = false)
  Integer profileId;

  /** The product name. */
  @NotBlank(message = "name must not be blank")
  @Size(min = 1, max = 300, message = "name must contain between 1 to 300 characters")
  @Column(nullable = false)
  String name;

  /** The product type. */
  @NotBlank(message = "type must not be blank")
  @Size(min = 1, max = 10, message = "type must contain between 1 to 10 characters")
  @Column(nullable = false)
  String type;

  /** The product description. */
  @NotBlank(message = "description must not be blank")
  @Size(min = 1, message = "description must contain at least 1 character")
  @Column(nullable = false, columnDefinition = "text")
  String description;

  /** The product image url. */
  @URL(protocol = "http")
  @NotBlank(message = "imageUrl must not be blank")
  @Size(min = 1, message = "imageUrl must contain at least 1 character")
  @Column(nullable = false, columnDefinition = "text")
  String imageUrl;

  /** The product price. */
  @NotNull(message = "price must not be null")
  @Positive(message = "price must be positive")
  @Min(value = 1, message = "price must be at least 1")
  @Column(nullable = false)
  Long price;

  /** The product specification. */
  @NotBlank(message = "specifications must not be blank")
  @Size(min = 1, message = "specifications must contain at least 1 character")
  @Column(nullable = false, columnDefinition = "text")
  String specifications;

  /** The created timestamp. */
  @NotNull(message = "createdTimestamp must not be null")
  @Column(nullable = false)
  LocalDateTime createdTimestamp;

  public Product(
      Integer profileId,
      String name,
      String type,
      String description,
      String imageUrl,
      Long price,
      String specifications,
      LocalDateTime createdTimestamp) {
    this.profileId = profileId;
    this.name = name;
    this.type = type;
    this.description = description;
    this.imageUrl = imageUrl;
    this.price = price;
    this.specifications = specifications;
    this.createdTimestamp = createdTimestamp;
  }
}
