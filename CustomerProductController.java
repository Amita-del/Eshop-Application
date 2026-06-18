package com.ecommerce.controller.customer;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecommerce.entity.product.Product;
import com.ecommerce.service.ProductService;

@Controller
@RequestMapping("/customer/products")
public class CustomerProductController 
{
	private ProductService productService;
	//Constructor injection
	public CustomerProductController(ProductService productService) 
	{
		this.productService = productService;
	}
	
	// ✅ 1️⃣ Customer Dashboard = Products Page
    @GetMapping
    public String viewProducts(Model model)
    {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        
        return "customer/product/products";
    }
    // ✅ 2️⃣ Product Details Page
    @GetMapping("/{id}")
    public String productDetails(@PathVariable Long id, Model model) 
    {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);

        return "customer/product/product-details";
    }
}
