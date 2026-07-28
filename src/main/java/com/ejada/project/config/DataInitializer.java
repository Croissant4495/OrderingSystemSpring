package com.ejada.project.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ejada.project.enums.RoleName;
import com.ejada.project.model.Category;
import com.ejada.project.model.Product;
import com.ejada.project.model.Role;
import com.ejada.project.model.User;
import com.ejada.project.repository.CategoryRepository;
import com.ejada.project.repository.ProductRepository;
import com.ejada.project.repository.RoleRepository;
import com.ejada.project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            RoleRepository roleRepository,
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (roleRepository.count() == 0) {

                Role adminRole = new Role();
                adminRole.setName(RoleName.ADMIN);

                Role userRole = new Role();
                userRole.setName(RoleName.USER);

                roleRepository.save(adminRole);
                roleRepository.save(userRole);
            }

            if (categoryRepository.count() == 0) {

                Category electronics = new Category();
                electronics.setName("ELECTRONICS");

                Category books = new Category();
                books.setName("BOOKS");

                Category clothing = new Category();
                clothing.setName("CLOTHING");

                categoryRepository.saveAll(List.of(
                        electronics,
                        books,
                        clothing
                ));
            }

            if (productRepository.count() == 0) {

                Category electronics =
                        categoryRepository.findByName("ELECTRONICS").orElseThrow();

                Category books =
                        categoryRepository.findByName("BOOKS").orElseThrow();

                Product laptop = new Product();
                laptop.setName("Dell XPS 15");
                laptop.setDescription("15-inch Laptop");
                laptop.setPrice(new BigDecimal("45000"));
                laptop.setStockQuantity(10);
                laptop.getCategories().add(electronics);

                Product mouse = new Product();
                mouse.setName("Logitech G502");
                mouse.setDescription("Gaming Mouse");
                mouse.setPrice(new BigDecimal("1500"));
                mouse.setStockQuantity(40);
                mouse.getCategories().add(electronics);

                Product springBook = new Product();
                springBook.setName("Spring Boot Guide");
                springBook.setDescription("Programming Book");
                springBook.setPrice(new BigDecimal("750"));
                springBook.setStockQuantity(25);
                springBook.getCategories().add(books);

                productRepository.saveAll(List.of(
                        laptop,
                        mouse,
                        springBook
                ));
            }

            if (userRepository.count() == 0) {

                Role adminRole =
                        roleRepository.findByName(RoleName.ADMIN).orElseThrow();

                Role userRole =
                        roleRepository.findByName(RoleName.USER).orElseThrow();

                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@shop.com");
                admin.setFirstName("System");
                admin.setLastName("Admin");
                admin.getRoles().add(adminRole);
                admin.getRoles().add(userRole);

                User user = new User();
                user.setUsername("john");
                user.setPassword(passwordEncoder.encode("123456"));
                user.setEmail("john@gmail.com");
                user.setFirstName("John");
                user.setLastName("Doe");
                user.getRoles().add(userRole);

                userRepository.save(admin);
                userRepository.save(user);
            }

        };
    }
}