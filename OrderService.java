package com.ecommerce.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ecommerce.entity.login.User;
import com.ecommerce.entity.order.Order;
import com.ecommerce.entity.order.OrderStatus;
import com.ecommerce.entity.product.Product;

public interface OrderService {

    List<Order> getAllOrders();
    Order getOrderById(Long id);
    long countOrders();
	List<Order> getOrdersByUser(User user);
	Order getOrderByIdAndUser(Long id, User user);
	BigDecimal getTotalRevenue();
	void updateOrderStatus(Long orderId, OrderStatus newStatus);
	Order createPendingSingleProductOrder(User user, Product product, int quantity);
	Map<String, Object> createPendingOrder(User user);
	void handleBuyNowPaymentSuccess(Long orderId);
	void handleCartPaymentSuccess(Long orderId);
}