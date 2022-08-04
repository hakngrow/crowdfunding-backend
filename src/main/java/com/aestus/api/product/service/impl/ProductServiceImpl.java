package com.aestus.api.product.service.impl;

import com.aestus.api.product.repository.ProductRepository;
import com.aestus.api.product.service.ProductService;
import com.aestus.api.product.model.Product;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The implementation of the Product service. This implementation uses a {@code CrudRepository} for
 * persistence to a RDBMS via Hibernate.
 */
@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;

  public Iterable<Product> getAllProducts() {
    return productRepository.findAll();
  }

  public Iterable<Product> getProductsByProfileId(int profileId) {
    return productRepository.findByProfileId(profileId);
  }

  public Optional<Product> getProductById(int id) {
    return productRepository.findById(id);
  }

  public Product createProduct(Product product) {
    return productRepository.save(product);
  }

  public Product updateProduct(Product product) {
    return productRepository.save(product);
  }

  public void deleteProductById(int id) {
    productRepository.deleteById(id);
  }

  public void deleteAllProducts() {
    productRepository.deleteAll();
  }
}
