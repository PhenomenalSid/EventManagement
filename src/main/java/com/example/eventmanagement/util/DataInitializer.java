package com.example.eventmanagement.util;

import com.example.eventmanagement.enums.Role;
import com.example.eventmanagement.model.User;
import com.example.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String defaultAdminUsername;

    @Value("${admin.password}")
    private String defaultAdminPassword;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByRole(Role.ADMIN).isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername(defaultAdminUsername);
            adminUser.setPassword(passwordEncoder.encode(defaultAdminPassword));
            adminUser.setRole(Role.ADMIN);

            userRepository.save(adminUser);
        }
    }
}