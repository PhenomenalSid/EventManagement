package com.example.eventmanagement.service;

import com.example.eventmanagement.dto.AuthDTO;
import com.example.eventmanagement.dto.EventDTO;
import com.example.eventmanagement.dto.UserDTO;
import com.example.eventmanagement.enums.RSVPStatus;
import com.example.eventmanagement.enums.Role;
import com.example.eventmanagement.exception.AuthException.AdminRoleNotAllowedException;
import com.example.eventmanagement.exception.AuthException.TokenNotValidException;
import com.example.eventmanagement.exception.AuthException.UserNotLoggedInException;
import com.example.eventmanagement.exception.UserException.UserAlreadyExistsException;
import com.example.eventmanagement.exception.UserException.UserNotFoundException;
import com.example.eventmanagement.model.Event;
import com.example.eventmanagement.model.User;
import com.example.eventmanagement.repository.EventRepository;
import com.example.eventmanagement.repository.UserRepository;
import com.example.eventmanagement.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImplementation userDetailsServiceImplementation;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private JwtTokenBlacklistService jwtTokenBlacklistService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CacheService cacheService;

    @Transactional
    public UserDTO createUser(AuthDTO authDTO) {
        if (userRepository.findByUsername(authDTO.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User with username " + authDTO.getUsername() + " already exists!");
        }

        if (authDTO.getRole().equalsIgnoreCase("ADMIN")) {
            throw new AdminRoleNotAllowedException("Only admins can create users with admin roles!");
        }

        User user = AuthDTO.toEntity(authDTO);
        User savedUser = userRepository.save(user);
        UserDTO userDTO = User.toDTO(savedUser);
        cacheService.invalidateCacheForAllResources(UserDTO.class);
        return userDTO;
    }

    public UserDTO getUserById(Long userId) throws RuntimeException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found!"));
        UserDTO userDTO = User.toDTO(user);
        cacheService.cacheResource(userDTO);
        return userDTO;
    }

    public UserDTO updateUserById(AuthDTO authDTO, Long userId, String username) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found!"));
        if (!Objects.equals(user.getUsername(), username)) {
            throw new AccessDeniedException("You are not allowed to update other user!");
        }

        Role newRole = authDTO.getRole() != null ? Role.fromString(authDTO.getRole()) : user.getRole();

        if (user.getRole().equals(Role.ORGANIZER) && newRole.equals(Role.PARTICIPANT)) {
            List<Event> events = eventRepository.findByOrganizerId(user.getId());

            events.forEach(event -> {
                cacheService.invalidateCacheForResource(event.getId(), EventDTO.class);
                cacheService.invalidateCacheForAllResources(EventDTO.class);
                event.getRsvps().forEach(rsvp -> {
                    if (rsvp.getStatus().equals(RSVPStatus.ACCEPTED)) {
                        emailService.sendEventCancellation(rsvp.getUser().getEmail(), event.getName());
                    }
                });
            });

            eventRepository.deleteAll(events);
            user.setEvents(new ArrayList<>());
        }

        user.setRole(authDTO.getRole() != null ? Role.fromString(authDTO.getRole()) : user.getRole());
        user.setUsername(authDTO.getUsername() != null ? authDTO.getUsername() : user.getUsername());
        user.setPassword(authDTO.getPassword() != null ? passwordEncoder.encode(authDTO.getPassword()) : user.getPassword());

        User savedUser = userRepository.save(user);
        UserDTO userDTO = User.toDTO(savedUser);

        cacheService.invalidateCacheForAllResources(UserDTO.class);

        return userDTO;
    }

    public void deleteUserById(Long userId, String username) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with userId " + userId + " not found!"));
        if (!Objects.equals(user.getUsername(), username)) {
            throw new AccessDeniedException("You are not allowed to delete other user!");
        }

        if (user.getRole().equals(Role.ORGANIZER)) {
            List<Event> events = eventRepository.findByOrganizerId(user.getId());

            events.forEach(event -> {
                cacheService.invalidateCacheForResource(event.getId(), EventDTO.class);
                cacheService.invalidateCacheForAllResources(EventDTO.class);
                event.getRsvps().forEach(rsvp -> {
                    if (rsvp.getStatus().equals(RSVPStatus.ACCEPTED)) {
                        emailService.sendEventCancellation(rsvp.getUser().getEmail(), event.getName());
                    }
                });
            });

            eventRepository.deleteAll(events);
            user.setEvents(new ArrayList<>());
        }

        cacheService.invalidateCacheForAllResources(UserDTO.class);
        userRepository.delete(user);
    }

    public String loginUser(AuthDTO authDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword()));
            UserDetails userDetails = userDetailsServiceImplementation.loadUserByUsername(authDTO.getUsername());
            return jwtUtil.generateToken(userDetails.getUsername());
        } catch (Exception e) {
            throw new RuntimeException("Incorrect username or password!");
        }
    }

    public void logoutUser(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new UserNotLoggedInException("You need to log in first.");
        }

        String jwtToken = token.substring(7);

        if (jwtTokenBlacklistService.isTokenBlacklisted(jwtToken)) {
            throw new TokenNotValidException("Token is already invalid.");
        }

        jwtTokenBlacklistService.blacklistToken(jwtToken, 5);
    }
}
