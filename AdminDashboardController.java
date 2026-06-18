package com.ecommerce.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/dashboard")
public class AdminDashboardController
{
    private ProductService productService;
    private OrderService orderService;

    public AdminDashboardController(ProductService productService, OrderService orderService) 
    {
		this.productService = productService;
		this.orderService = orderService;
	}
	
    @GetMapping
    public String dashboard(Model model, HttpSession session) 
    {
        model.addAttribute("totalProducts", productService.countProducts());
        model.addAttribute("totalOrders", orderService.countOrders());
        model.addAttribute("totalRevenue", orderService.getTotalRevenue()); // calculate properly later
        model.addAttribute("recentOrders",orderService.getAllOrders().stream().limit(5).toList());

        return "admin/dashboard";
    }
}
