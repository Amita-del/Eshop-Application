package com.ecommerce.controller.login;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecommerce.entity.login.User;
import com.ecommerce.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController 
{
	private UserService userService;
	
	public LoginController(UserService userService) 
	{
		this.userService = userService;
	}
	
	@GetMapping
	public String getLogin(Model model, HttpSession session)
	{
		if(session.getAttribute("loggedUser") != null)
			return "auth/alreadyLoggedIn";
		return "auth/login";
	}
	
	@PostMapping
	public String getLogin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model)
	{
	    try 
	    {
	        User user = userService.login(username, password);
	        session.setAttribute("loggedUser", user);
	        session.setAttribute("userId", user.getId());

	        if (user.getRole().name().equals("ADMIN")) 
	        {
	            return "redirect:/admin/dashboard";
	        } 
	        else 
	        {
	            return "redirect:/customer/products";
	        }

	    } 
	    catch (Exception e) 
	    {
	    	System.out.println("Exception caught");
	    	model.addAttribute("email", username);
	        model.addAttribute("error", e.getMessage());
	   
	        return "auth/login";
	    }
	}

	@RequestMapping("/logout")
	public String logout(HttpSession session) 
	{
	   session.invalidate();   // destroys session
	
	   return "redirect:/login";
	}
	
	@GetMapping("/access-denied")
	public String accessDenied() 
	{
	    return "access-denied";
	}
}
