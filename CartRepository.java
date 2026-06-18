package com.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ecommerce.entity.cart.Cart;
import com.ecommerce.entity.login.User;

public interface CartRepository extends JpaRepository<Cart, Long> 
{
	@Query("""
		       SELECT c FROM Cart c
		       LEFT JOIN FETCH c.items i
		       LEFT JOIN FETCH i.product
		       WHERE c.user = :user
		       """)
		Optional<Cart> findByUserWithItems(@Param("user") User user);
}