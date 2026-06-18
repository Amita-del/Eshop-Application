package com.ecommerce.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.entity.login.User;
import com.ecommerce.entity.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long>
{
	List<Order> findByUserOrderByOrderDateDesc(User user);
	
	@Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id AND o.user = :user")
	Optional<Order> findByIdAndUserWithItems(@Param("id") Long id, @Param("user") User user);
	
	@Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
	           "WHERE o.status IN ('PAID','SHIPPED','DELIVERED')")
	BigDecimal calculateTotalRevenue();
	
	Optional<Order> findById(Long id);
}