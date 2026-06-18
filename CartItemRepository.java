package com.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.entity.cart.Cart;
import com.ecommerce.entity.cart.CartItem;
import com.ecommerce.entity.product.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Long>
{

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}