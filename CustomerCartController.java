package com.ecommerce.controller.customer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ecommerce.entity.cart.Cart;
import com.ecommerce.entity.login.User;
import com.ecommerce.service.CartService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer/cart")
public class CustomerCartController 
{
	private CartService cartService;
	
	public CustomerCartController(CartService cartService) 
	{
		this.cartService = cartService;
	}

	// ✅ 3️⃣ View Cart Page
	@GetMapping
	public String viewCart(Model model, HttpSession session) 
	{
		User user = (User) session.getAttribute("loggedUser");
		Cart cart = cartService.getCartByUser(user);
		model.addAttribute("cart", cart);
		if (cart != null)
			model.addAttribute("cartTotal", cart.getTotalAmount());
		else
			model.addAttribute("cartTotal", 0);
		return "customer/cart/cart";
	}

	@PostMapping("/add/{productId}")
	public String addToCart(@PathVariable Long productId, @RequestParam Integer quantity, HttpSession session, RedirectAttributes redirectAttributes) 
	{
		User user = (User) session.getAttribute("loggedUser");
		try 
		{
			cartService.addToCart(user, productId, quantity);
			redirectAttributes.addFlashAttribute("success", "Product added to cart!");
		} 
		catch (RuntimeException e) 
		{
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/customer/products";
	}

	@PostMapping("/update/{itemId}")
	public String updateCartItem(@PathVariable Long itemId, @RequestParam("quantity") Integer quantity, HttpSession session, RedirectAttributes redirectAttributes) 
	{	
		User user = (User) session.getAttribute("loggedUser");
		try 
		{
			cartService.updateCartItem(user, itemId, quantity);
			redirectAttributes.addFlashAttribute("success", "Cart updated successfully!");
		} 
		catch (Exception e) 
		{
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/customer/cart";
	}

	@GetMapping("/remove/{itemId}")
	public String removeCartItem(@PathVariable Long itemId, HttpSession session, RedirectAttributes redirectAttributes) 
	{
		User user = (User) session.getAttribute("loggedUser");
		try 
		{
			cartService.removeCartItem(user, itemId);
			redirectAttributes.addFlashAttribute("success", "Item removed from cart!");
		} 
		catch (Exception e) 
		{
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/customer/cart";
	}
}
