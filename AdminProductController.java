package com.ecommerce.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecommerce.entity.product.Product;
import com.ecommerce.service.ProductService;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController
{
    private ProductService productService;

    public AdminProductController(ProductService productService) 
    {
		this.productService = productService;
	}
	
    @GetMapping
    public String products(Model model) 
    {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }
    @GetMapping("/new")
    public String newProduct(Model model) 
    {
        model.addAttribute("product", new Product());
        return "admin/product-form";
    }

    // ✅ Edit Product
    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) 
    {
        model.addAttribute("product", productService.getProductById(id));
        return "admin/product-form";
    }

    // ✅ Save Product (Create + Update)
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product) 
    {
        productService.saveProduct(product);
        return "redirect:/admin/products";
    }

    // ✅ Delete Product
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) 
    {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }
}
