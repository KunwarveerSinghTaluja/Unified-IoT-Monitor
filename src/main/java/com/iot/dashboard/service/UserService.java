package com.iot.dashboard.service;

import com.iot.dashboard.entity.User;
import com.iot.dashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User saveUser(User user) {
        // Set default values if not set
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            // Generate a random password for Spring Security in-memory users
            user.setPassword(passwordEncoder.encode("default123"));
        }
        return userRepository.save(user);
    }

    public void updateLastLogin(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        } else {
            // Create user if doesn't exist (for Spring Security in-memory users)
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEnabled(true);
            newUser.setRole("USER");
            newUser.setLastLogin(LocalDateTime.now());
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setPassword(passwordEncoder.encode("default123"));
            userRepository.save(newUser);
        }
    }

    // ... keep your existing methods ...
}