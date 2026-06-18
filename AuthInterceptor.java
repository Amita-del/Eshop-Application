package com.ecommerce.login.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import com.ecommerce.entity.login.Role;
import com.ecommerce.entity.login.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthInterceptor implements HandlerInterceptor 
{
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		System.out.println("PreHandle!");
	    HttpSession session = request.getSession(false);
	    if (session == null) 
	    {
	        response.sendRedirect("/login");
	        return false;
	    }

	    User user = (User) session.getAttribute("loggedUser");

	    if (user == null)
	    {
	        response.sendRedirect("/login");
	        return false;
	    }

	    String uri = request.getRequestURI();

	    // Admin module protection
	    if (uri.startsWith("/admin") && user.getRole() != Role.ADMIN) 
	    {
	        response.sendRedirect("/login/access-denied");
	        return false;
	    }

	    // Customer module protection
	    if (uri.startsWith("/customer") && user.getRole() != Role.CUSTOMER)
	    {
	        response.sendRedirect("/access-denied");
	        return false;
	    }
	    return true;
	}
}