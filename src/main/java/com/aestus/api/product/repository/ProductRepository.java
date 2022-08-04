package com.aestus.api.product.repository;

import com.aestus.api.product.model.Product;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 * The ProductRepository extends the {@code CrudRepository} for basic CRUD operations on a RDBMS via
 * Hibernate.
 */
public interface ProductRepository extends CrudRepository<Product, Integer> {

  /**
   * Find all products by {@code productId}
   *
   * @param profileId the profile id
   * @return the list of products
   */
  List<Product> findByProfileId(int profileId);
}
