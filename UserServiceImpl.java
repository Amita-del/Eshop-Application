package com.ecommerce.service.impl;

import com.ecommerce.entity.login.Role;
import com.ecommerce.entity.login.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(User user)
    {
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail()))
        {
            throw new RuntimeException("Email already registered!");
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Default role = CUSTOMER
        if (user.getRole() == null)
        {
            user.setRole(Role.CUSTOMER);
        }

        return userRepository.save(user);
    }

    @Override
    public User login(String email, String password)
    {
    	User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email address does not exist"));

        if (!passwordEncoder.matches(password, user.getPassword()))
        {
            throw new RuntimeException("Password is wrong");
        }

        return user;
    }

    @Override
    public User findByEmail(String email)
    {
        return userRepository.findByEmail(email)
                .orElse(null);
    }
}