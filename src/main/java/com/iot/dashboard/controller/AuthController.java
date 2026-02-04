package com.iot.dashboard.controller;

import com.iot.dashboard.entity.User;
import com.iot.dashboard.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // ROOT PAGE - ALWAYS GO TO LOGIN
    @GetMapping("/")
    public String root(HttpServletRequest request) {
        // Force clear any existing session when accessing root
        request.getSession().invalidate();
        return "redirect:/login";
    }

    // Login page
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        @RequestParam(value = "expired", required = false) String expired,
                        HttpServletRequest request,
                        Model model) {

        // Force clear session when accessing login page
        request.getSession().invalidate();

        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        if (expired != null) {
            model.addAttribute("message", "Your session has expired. Please login again.");
        }
        return "login";
    }

    // Dashboard page (after login)
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = auth.getName();
        model.addAttribute("username", username);

        // REMOVED the automatic user creation logic
        // Spring Security in-memory users don't need to be in database
        return "dashboard";
    }

    // Get current user info (for your frontend)
    @GetMapping("/api/auth/user")
    @ResponseBody
    public Map<String, Object> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> userInfo = new HashMap<>();

        if (auth != null && auth.isAuthenticated()) {
            userInfo.put("username", auth.getName());
            userInfo.put("authenticated", true);
            userInfo.put("role", auth.getAuthorities().stream()
                    .findFirst()
                    .map(g -> g.getAuthority().replace("ROLE_", ""))
                    .orElse("USER"));
        } else {
            userInfo.put("authenticated", false);
        }

        return userInfo;
    }

    // Add a logout endpoint that forces session clear
    @GetMapping("/force-logout")
    public String forceLogout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login?logout=true";
    }
}