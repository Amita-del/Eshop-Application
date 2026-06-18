package com.ecommerce.controller.customer;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ecommerce.entity.login.User;
import com.ecommerce.entity.order.Order;
import com.ecommerce.entity.order.OrderStatus;
import com.ecommerce.entity.product.Product;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer/orders")
public class CustomerOrderController 
{
	private OrderService orderService;
	private ProductService productService;

	//Constructor injection
	public CustomerOrderController(OrderService orderService, ProductService productService) 
	{
		this.orderService = orderService;
		this.productService = productService;
	}
	
	@GetMapping
    public String viewOrders(HttpSession session, Model model)
    {
        User user = (User) session.getAttribute("loggedUser");
        System.out.println("User: " + user);
        List<Order> orders = orderService.getOrdersByUser(user);
        System.out.println(orders.isEmpty());
        model.addAttribute("orders", orders);

        return "customer/order/orders";
    }
	
	@GetMapping("/success/{orderId}")
	public String orderSuccess(@PathVariable Long orderId, Model model)
	{
	    Order order = orderService.getOrderById(orderId);
	    model.addAttribute("order", order);
	    return "customer/order/order-success";
	}
    
    @GetMapping("/{productId}")
    public String orderDetails(@PathVariable Long productId, HttpSession session, Model model) 
    {
        User user = (User) session.getAttribute("loggedUser");
        Order order = orderService.getOrderByIdAndUser(productId, user);
        model.addAttribute("order", order);

        return "customer/order/order-details";
    }
    
    @PostMapping("/buy/{productId}")
    public String buyNow(@PathVariable Long productId, @RequestParam int quantity, HttpSession session, Model model)
    {
    	User user = (User) session.getAttribute("loggedUser");
    	Product product = productService.getProductById(productId);
    	model.addAttribute("user", user);
        model.addAttribute("product", product);
        model.addAttribute("quantity", quantity);
        
        //productService.buyNow(id, quantity, user);
        return "customer/order/buy-now";
    }
   
    @PostMapping("/buy/confirm")
    public String confirmBuyNow(@RequestParam Long productId, @RequestParam int quantity, HttpSession session, RedirectAttributes redirectAttributes)
    {
        User user = (User) session.getAttribute("loggedUser");
        Product product = productService.getProductById(productId);

        if (product.getStock() < quantity) {
            redirectAttributes.addFlashAttribute("error",
                "Only " + product.getStock() + " items available in stock!");
            return "redirect:/customer/products/" + productId;
        }

        // IMPORTANT: this should create order with status = PENDING
        Order order = orderService.createPendingSingleProductOrder(user, product, quantity);
        return "redirect:/customer/payment/" + order.getId();
    }
    
    @PostMapping("/cancel/{orderId}")
    public String cancelOrder(@PathVariable Long orderId, HttpSession session, RedirectAttributes redirectAttributes) 
    {
        User user = (User) session.getAttribute("loggedUser");
        Order order = orderService.getOrderById(orderId);

        // 🔒 Security check
        if (!order.getUser().getId().equals(user.getId())) 
        {
            return "redirect:/access-denied";
        }
        if (order.getStatus() != OrderStatus.PENDING) 
        {
            redirectAttributes.addFlashAttribute("error",
                    "Only pending orders can be cancelled.");
            return "redirect:/customer/orders/" + orderId;
        }

        orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
        redirectAttributes.addFlashAttribute("success", "Order cancelled successfully!");

        return "redirect:/customer/orders/" + orderId;
    }
}
