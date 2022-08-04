package com.aestus.api.product.service;

import com.aestus.api.product.model.Product;

import java.util.Optional;

/**
 * The ProductService interface provides access to application functionality and features for
 * products. It acts as a proxy or endpoint to the service. The specific implementation can be found
 * in the {@code ProductServiceImpl} class in the {@code impl} package.
 */
public interface ProductService {

  /**
   * Gets all products.
   *
   * @return all products
   */
  Iterable<Product> getAllProducts();

  /**
   * Gets products by {@code profileId}.
   *
   * @param profileId the profile id
   * @return the list of transactions
   */
  Iterable<Product> getProductsByProfileId(int profileId);

  /**
   * Gets product by id.
   *
   * @param id the product id
   * @return the product if found
   */
  Optional<Product> getProductById(int id);

  /**
   * Creates a product.
   *
   * @param product the product
   * @return the product with generated id
   */
  Product createProduct(Product product);

  /**
   * Updates the product.
   *
   * @param product the product to be updated
   * @return the updated product
   */
  Product updateProduct(Product product);

  /**
   * Delete product by id.
   *
   * @param id the id
   */
  void deleteProductById(int id);

  /** Delete all products. */
  void deleteAllProducts();
}
