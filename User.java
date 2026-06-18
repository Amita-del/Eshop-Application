package com.ecommerce.entity.login;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

import com.ecommerce.entity.cart.Cart;
import com.ecommerce.entity.order.Order;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true)
    private String email;

    private String password;

    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;   // ADMIN or CUSTOMER

    @OneToMany(mappedBy = "user")
    private Set<Order> orders;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;
}