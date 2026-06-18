package com.ecommerce.controller.customer;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.entity.cart.Cart;
import com.ecommerce.entity.login.User;
import com.ecommerce.entity.order.Order;
import com.ecommerce.entity.order.OrderStatus;
import com.ecommerce.service.CartService;
import com.ecommerce.service.OrderService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer/payment")
public class PaymentController 
{
    private OrderService orderService;
    private CartService cartService;

    public PaymentController(OrderService orderService, CartService cartService) 
    {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    // ===============================
    // SHOW PAYMENT PAGE
    // ===============================
    @GetMapping("/{orderId}")
    public String showPaymentPage(@PathVariable Long orderId, Model model) 
    {
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "customer/payment/buynow-payment-page";
    }

    // ===============================
    // ⚡ BUY NOW PAYMENT PROCESS
    // ===============================
    @PostMapping("/process/buynow")
    public String processBuyNowPayment(@RequestParam Long orderId, @RequestParam boolean success, RedirectAttributes redirectAttributes)
    {
    	System.out.println("b1");
        try 
        {
            if (success) 
            {
            	System.out.println("b-success");
                orderService.handleBuyNowPaymentSuccess(orderId);
                redirectAttributes.addFlashAttribute("successMessage", "Payment successful!");
            } 
            else 
            {
            	System.out.println("b-cancel");
                orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
                redirectAttributes.addFlashAttribute("error", "Payment failed!");
            }

        } 
        catch (Exception e)
        {
        	System.out.println("b-exception");
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/payment/" + orderId;
        }
        return "redirect:/customer/payment/result/" + orderId + "?success=" + success;
    }

    // ===============================
    // 🛒 CART CHECKOUT PAGE
    // ===============================
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) 
    {
        User user = (User) session.getAttribute("loggedUser");

        Cart cart = cartService.getCartByUser(user);
        if (cart == null || cart.getItems().isEmpty()) 
        {
            return "redirect:/customer/cart";
        }

        model.addAttribute("user", user);
        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", cart.getTotalAmount());

        return "customer/cart/checkout";
    }

    // ===============================
    // CREATE PENDING ORDER (CART)
    // ===============================
    @PostMapping("/checkout-payment")
    public String paymentPage(HttpSession session, RedirectAttributes redirectAttributes, Model model)
    {
        User user = (User) session.getAttribute("loggedUser");
        try 
        {
        	Map<String, Object> result = orderService.createPendingOrder(user);
            model.addAttribute("cart", result.get("cart"));
            model.addAttribute("order", result.get("order"));
            return "customer/payment/cart-payment-page";
        } 
        catch (Exception e) 
        {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/cart";
        }
    }

    // ===============================
    // 🛒 CART PAYMENT PROCESS
    // ===============================
    @PostMapping("/process/cart")
    public String processCartPayment(@RequestParam Long orderId, @RequestParam boolean success, RedirectAttributes redirectAttributes) 
    {
    	try 
        {
            if (success) 
            {
            	System.out.println("b-success");
                // ✅ Correct method
                orderService.handleCartPaymentSuccess(orderId);
                redirectAttributes.addFlashAttribute("successMessage", "Payment successful!");
            } 
            else 
            {
            	System.out.println("b-cancel");
                orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
                redirectAttributes.addFlashAttribute("error", "Payment failed!");
            }

        } 
        catch (Exception e) 
        {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/payment/" + orderId;
        }

        return "redirect:/customer/payment/result/" + orderId + "?success=" + success;
    }

    // ===============================
    // PAYMENT RESULT PAGE
    // ===============================
    @GetMapping("/result/{orderId}")
    public String paymentResult(@PathVariable Long orderId, @RequestParam boolean success, Model model) 
    {
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        model.addAttribute("success", success);

        return "customer/payment/payment-result";
    }
}