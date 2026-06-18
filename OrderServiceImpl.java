package com.ecommerce.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.entity.cart.Cart;
import com.ecommerce.entity.cart.CartItem;
import com.ecommerce.entity.login.User;
import com.ecommerce.entity.order.Order;
import com.ecommerce.entity.order.OrderItem;
import com.ecommerce.entity.order.OrderStatus;
import com.ecommerce.entity.product.Product;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService 
{
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private ProductRepository productRepository;

	// ==============================
	// ⚡ BUY NOW (Single Product - Payment Based)
	// ==============================
	@Transactional
	public Order createPendingSingleProductOrder(User user, Product product, int quantity) 
	{
		// 1️⃣ Validate quantity
		if (quantity <= 0) 
		{
			throw new RuntimeException("Invalid quantity selected.");
		}

		// 2️⃣ Validate stock (without deduction)
		if (product.getStock() < quantity) 
		{
			throw new RuntimeException("Only " + product.getStock() + " items available.");
		}

		// 3️⃣ Calculate total price
		BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(quantity));

		// 4️⃣ Create Order with PENDING status
		Order order = Order.builder()
				.orderDate(LocalDateTime.now())
				.status(OrderStatus.PENDING) // Important: not PAID
				.user(user)
				.totalAmount(totalAmount)
				.build();

		// 5️⃣ Create OrderItem (price snapshot)
		OrderItem orderItem = OrderItem.builder()
				.order(order)
				.product(product)
				.quantity(quantity)
				.price(product.getPrice())
				.build();

		order.setItems(Set.of(orderItem));

		// 6️⃣ Save order (Cascade saves order item)
		return orderRepository.save(order);
	}

	// ==============================
	// 📦 GET ORDERS
	// ==============================
	@Override
	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public Order getOrderById(Long id) {
		return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
	}

	@Override
	public List<Order> getOrdersByUser(User user) {
		return orderRepository.findByUserOrderByOrderDateDesc(user);
	}

	@Override
	public long countOrders() {
		return orderRepository.count();
	}

	public BigDecimal getTotalRevenue() {
		return orderRepository.calculateTotalRevenue();
	}

	// ==============================
	// 🔄 UPDATE ORDER STATUS
	// ==============================
	@Transactional
	public void updateOrderStatus(Long orderId, OrderStatus newStatus) {

		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

		OrderStatus currentStatus = order.getStatus();

		// ❌ Prevent modification if delivered
		if (currentStatus == OrderStatus.DELIVERED) {
			throw new RuntimeException("Delivered order cannot be modified.");
		}

		// ✅ Deduct stock when payment succeeds
		if (newStatus == OrderStatus.PAID && currentStatus == OrderStatus.PENDING) {

			for (OrderItem item : order.getItems()) {

				Product product = item.getProduct();

				if (product.getStock() < item.getQuantity()) {
					throw new RuntimeException(product.getName() + " out of stock");
				}

				product.setStock(product.getStock() - item.getQuantity());
				productRepository.save(product);
			}
		}

		// ✅ Restore stock if paid order is cancelled
		if (newStatus == OrderStatus.CANCELLED && currentStatus == OrderStatus.PAID) {

			for (OrderItem item : order.getItems()) {

				Product product = item.getProduct();
				product.setStock(product.getStock() + item.getQuantity());
				productRepository.save(product);
			}
		}
		order.setStatus(newStatus);
	}

	@Transactional
	public Order getOrderByIdAndUser(Long id, User user) {
		return orderRepository.findByIdAndUserWithItems(id, user)
				.orElseThrow(() -> new RuntimeException("Order not found"));
	}

	@Transactional
	public Map<String, Object> createPendingOrder(User user) 
	{
		Cart cart = cartRepository.findByUserWithItems(user).orElseThrow(() -> new RuntimeException("Cart not found"));
		if (cart.getItems().isEmpty()) 
		{
			throw new RuntimeException("Cart is empty");
		}

		Order order = Order.builder()
				.orderDate(LocalDateTime.now())
				.status(OrderStatus.PENDING)
				.user(user)
				.totalAmount(cart.getTotalAmount())
				.build();

		Set<OrderItem> orderItems = new HashSet<>();

		for (CartItem cartItem : cart.getItems()) 
		{
			Product product = cartItem.getProduct();
			if (product.getStock() < cartItem.getQuantity()) 
			{
				throw new RuntimeException(product.getName() + " out of stock");
			}
			OrderItem orderItem = OrderItem.builder()
					.order(order)
					.product(product)
					.quantity(cartItem.getQuantity())
					.price(product.getPrice())
					.build();
			orderItems.add(orderItem);
		}
		order.setItems(orderItems);
		orderRepository.save(order);
		return Map.of("order", order, "cart", cart);
	}
	@Transactional
	public void handleCartPaymentSuccess(Long orderId) 
	{
	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("Order not found"));

	    if (order.getStatus() != OrderStatus.PENDING) {
	        throw new RuntimeException("Invalid order state.");
	    }

	    // ✅ Deduct stock
	    for (OrderItem item : order.getItems()) 
	    {
	        Product product = item.getProduct();
	        if (product.getStock() < item.getQuantity()) 
	        {
	            throw new RuntimeException(product.getName() + " out of stock");
	        }

	        product.setStock(product.getStock() - item.getQuantity());
	        productRepository.save(product);
	    }

	    // ✅ Clear cart
	    Cart cart = cartRepository
	            .findByUserWithItems(order.getUser())
	            .orElse(null);

	    if (cart != null) 
	    {
	        cart.getItems().clear();
	        cartRepository.save(cart);
	    }
	    order.setStatus(OrderStatus.PAID);
	}
	
	@Transactional
	public void handleBuyNowPaymentSuccess(Long orderId) 
	{

	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("Order not found"));

	    if (order.getStatus() != OrderStatus.PENDING) 
	    {
	        throw new RuntimeException("Invalid order state.");
	    }

	    // ✅ Deduct stock
	    for (OrderItem item : order.getItems()) 
	    {
	        Product product = item.getProduct();
	        if (product.getStock() < item.getQuantity()) 
	        {
	            throw new RuntimeException(product.getName() + " out of stock");
	        }

	        product.setStock(product.getStock() - item.getQuantity());
	        productRepository.save(product);
	    }
	    order.setStatus(OrderStatus.PAID);
	}
}