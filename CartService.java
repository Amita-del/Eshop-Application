package com.ecommerce.service;

import com.ecommerce.entity.cart.Cart;
import com.ecommerce.entity.cart.CartItem;
import com.ecommerce.entity.login.User;

public interface CartService 
{

    void addToCart(User user, Long productId, Integer quantity);
	Cart getCartByUser(User user);
	void updateCartItem(User user, Long itemId, Integer quantity);
	void removeCartItem(User user, Long itemId);
	CartItem getCartItems(Long id);
}