package com.ecommerce.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ecommerce.login.interceptor.AuthInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) 
    {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**")           // protect all routes
                .excludePathPatterns("/login", "/register", "/images/**", "/css/**", "/js/**");
    }
}