package com.ecommerce.global;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.ecommerce.entity.cart.Cart;
import com.ecommerce.entity.cart.CartItem;
import com.ecommerce.entity.login.User;
import com.ecommerce.service.*;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes 
{
    private final CartService cartService;

    @ModelAttribute("cartCount")
    public Integer cartCount(HttpSession session) 
    {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) 
        {
            return 0;
        }

        if (!user.getRole().name().equals("CUSTOMER")) 
        {
            return 0;
        }
        Cart cart = cartService.getCartByUser(user);
        if (cart == null) 
        {
            return 0;
        }

        return cart.getItems()
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}