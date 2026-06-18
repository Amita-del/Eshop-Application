package com.ecommerce.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.ecommerce.entity.cart.Cart;
import com.ecommerce.entity.cart.CartItem;
import com.ecommerce.entity.login.User;
import com.ecommerce.entity.product.Product;
import com.ecommerce.repository.*;
import com.ecommerce.service.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService 
{

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    @Override
    public void addToCart(User user, Long productId, Integer quantity) 
    {
        Product product = productService.getProductById(productId);

        if (product == null) 
        {
            throw new RuntimeException("Product not found");
        }
        if (quantity > product.getStock()) 
        {
            throw new RuntimeException("Not enough stock available");
        }
        
        //Getting cart for customer. If  not exists then creates one 
        Cart cart = cartRepository.findByUserWithItems(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
        
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) 
        {
            CartItem item = existingItem.get();
            int newQty = item.getQuantity() + quantity;

            if (newQty > product.getStock()) 
            {
                throw new RuntimeException("Cannot add more than stock");
            }
            item.setQuantity(newQty);

        } 
        else 
        {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart); // VERY IMPORTANT
            cart.addItem(cartItem); // maintain relationship
        }
        cartRepository.save(cart);
    }

	@Override
	public Cart getCartByUser(User user)
	{
		return cartRepository.findByUserWithItems(user).orElse(null);
	}

	@Transactional
	public void updateCartItem(User user, Long itemId, Integer quantity)
	{
	    Cart cart = cartRepository.findByUserWithItems(user)
	            .orElseThrow(() -> new RuntimeException("Cart not found"));

	    CartItem item = cart.getItems().stream()
	            .filter(i -> i.getId().equals(itemId))
	            .findFirst()
	            .orElseThrow(() -> new RuntimeException("Item not found"));

	    if (quantity <= 0) {
	        throw new RuntimeException("Quantity must be greater than 0");
	    }

	    if (item.getProduct().getStock() < quantity) {
	        throw new RuntimeException("Not enough stock available");
	    }
	    item.setQuantity(quantity);
	}

	@Transactional
	public void removeCartItem(User user, Long itemId) 
	{
	    Cart cart = cartRepository.findByUserWithItems(user)
	            .orElseThrow(() -> new RuntimeException("Cart not found"));

	    CartItem item = cart.getItems().stream()
	            .filter(i -> i.getId().equals(itemId))
	            .findFirst()
	            .orElseThrow(() -> new RuntimeException("Item not found"));

	    cart.getItems().remove(item);
	    cartItemRepository.delete(item);
	}

	public CartItem getCartItems(Long id) 
	{
		return null;
	}
}