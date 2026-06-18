package com.ecommerce.service;

import com.ecommerce.entity.login.User;

public interface UserService
{
    User register(User user);
    User login(String email, String password);
    User findByEmail(String email);
}