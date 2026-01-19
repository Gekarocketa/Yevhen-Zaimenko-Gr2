package com.example.demo.config;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashSet;

@Configuration
public class DataInit {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Create Roles
            Role adminRole = createRoleIfNotFound(roleRepository, "admin");
            Role tutorRole = createRoleIfNotFound(roleRepository, "tutor");
            Role userRole = createRoleIfNotFound(roleRepository, "user");

            // Create Users
            createUserIfNotFound(userRepository, "admin@gmail.com", "Admin", "password", adminRole, passwordEncoder);
            createUserIfNotFound(userRepository, "tutor@gmail.com", "Tutor", "password", tutorRole, passwordEncoder);
            createUserIfNotFound(userRepository, "user@gmail.com", "User", "password", userRole, passwordEncoder);
        };
    }

    private Role createRoleIfNotFound(RoleRepository roleRepository, String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            return roleRepository.save(role);
        });
    }

    private void createUserIfNotFound(UserRepository userRepository, String email, String name, String password,
            Role role, PasswordEncoder passwordEncoder) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(new HashSet<>(Collections.singletonList(role)));
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }
}
