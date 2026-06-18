package com.ecommerce.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.entity.product.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService
{
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() 
    {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) 
    {
        return productRepository.findById(id).orElseThrow();
    }

    public void saveProduct(Product product) 
    {
        productRepository.save(product);
    }

    public void deleteProduct(Long id) 
    {
        productRepository.deleteById(id);
    }

    public long countProducts() 
    {
        return productRepository.count();
    }
}