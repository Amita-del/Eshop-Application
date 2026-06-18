package com.ecommerce.controller.login;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecommerce.entity.login.User;
import com.ecommerce.service.UserService;

@Controller
@RequestMapping("/register")
public class RegisterController 
{
	private UserService userService;
	
	public RegisterController(UserService userService) 
	{
		this.userService = userService;
	}

	@GetMapping
	public String getRegister(Model model)
	{
		System.out.println("Register!");
		model.addAttribute("user", new User());
	
		return "auth/register";
	}
	
	@PostMapping
	public String register(@ModelAttribute User user, Model model)
	{
	    try 
	    {
	        userService.register(user);
	        return "redirect:/login";
	    } 
	    catch (Exception e) 
	    {
	        model.addAttribute("error", e.getMessage());
	        return "auth/register";
	    }
	}
}
