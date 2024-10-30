package com.example.eventmanagement.dto;

import com.example.eventmanagement.enums.Role;
import com.example.eventmanagement.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private String username;
    private String email;
    private String password;
    private String role;

    public static User toEntity(AuthDTO authDTO){
        User user = new User();
        user.setUsername(authDTO.getUsername());
        user.setPassword(passwordEncoder.encode(authDTO.getPassword()));
        user.setRole(Role.fromString(authDTO.getRole()));
        user.setEmail(authDTO.getEmail());
        return user;
    }
}
