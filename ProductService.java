package com.ecommerce.service;

import java.util.List;

import com.ecommerce.entity.product.Product;

public interface ProductService 
{
	List<Product> getAllProducts();
    Product getProductById(Long id);
    void saveProduct(Product product);
    void deleteProduct(Long id);
    long countProducts();
}