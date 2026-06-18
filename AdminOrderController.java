package com.ecommerce.controller.admin;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecommerce.entity.order.Order;
import com.ecommerce.entity.order.OrderStatus;
import com.ecommerce.service.OrderService;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController
{
    private OrderService orderService;

    public AdminOrderController(OrderService orderService) 
    {
		this.orderService = orderService;
	}
	
    @GetMapping
    public String manageOrders(Model model) 
    {
        List<Order> orders = orderService.getAllOrders();
        
        Map<OrderStatus, String> statusClassMap = Map.of(
            OrderStatus.PENDING, "PENDING",
            OrderStatus.PAID, "PAID",
            OrderStatus.SHIPPED, "SHIPPED",
            OrderStatus.DELIVERED, "DELIVERED",
            OrderStatus.CANCELLED, "CANCELLED"
        );

        model.addAttribute("orders", orders);
        model.addAttribute("statusClassMap", statusClassMap);
        return "admin/orders";
    }
    
    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id, Model model) 
    {
        model.addAttribute("order", orderService.getOrderById(id));
        
        return "admin/order-details";
    }
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) 
    {
        orderService.updateOrderStatus(id, status);
    
        return "redirect:/admin/orders/" + id;
    }
}
