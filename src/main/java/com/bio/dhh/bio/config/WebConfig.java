package com.bio.dhh.bio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Áp dụng cho tất cả các đường dẫn bắt đầu bằng /api/
                .allowedOrigins(
                        "http://localhost:3000",                  // Dành cho môi trường phát triển (dev)
                        "https://bio-link-frontend.vercel.app"  // Dành cho môi trường sản phẩm (production)
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")// Các phương thức cho phép
                .allowedHeaders("*")    // Cho phép tất cả các header
                .allowCredentials(true); // Cho phép gửi cookie (nếu cần sau này)
    }
}