package com.iot.dashboard.controller;

import com.iot.dashboard.entity.User;
import com.iot.dashboard.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Login page
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }

    // Dashboard page (after login) - FIXED VERSION
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // FIX: Check if user exists in database, if not create one
        Optional<User> existingUser = userService.findByUsername(username);
        if (existingUser.isPresent()) {
            // Update last login for existing user
            userService.updateLastLogin(username);
        } else {
            // Create a new user entry for Spring Security in-memory user
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEnabled(true);
            newUser.setRole("USER"); // Default role
            userService.saveUser(newUser); // Save to database
        }

        model.addAttribute("username", username);
        return "dashboard";
    }

    // Home page
    @GetMapping("/")
    public String home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() &&
                !(auth.getPrincipal() instanceof String && auth.getPrincipal().equals("anonymousUser"))) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    // Get current user info (for your frontend)
    @GetMapping("/api/auth/user")
    @ResponseBody
    public Map<String, Object> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> userInfo = new HashMap<>();

        if (auth != null && auth.isAuthenticated() &&
                !(auth.getPrincipal() instanceof String && auth.getPrincipal().equals("anonymousUser"))) {
            userInfo.put("username", auth.getName());
            userInfo.put("authenticated", true);
            // Get user role from database if exists
            Optional<User> user = userService.findByUsername(auth.getName());
            user.ifPresent(u -> userInfo.put("role", u.getRole()));
        } else {
            userInfo.put("authenticated", false);
        }

        return userInfo;
    }
}