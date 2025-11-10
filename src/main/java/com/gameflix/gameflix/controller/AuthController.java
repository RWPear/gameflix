package com.gameflix.gameflix.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gameflix.gameflix.login.LoginRequest;
import com.gameflix.gameflix.login.RegisterRequest;
import com.gameflix.gameflix.service.UserService;

@RestController
@RequestMapping("/api/auth")

public class AuthController {
    
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    // GET endpoints for browser testing (development only)
    @GetMapping("/register")
    public String registerInfo() {
        return "Registration endpoint. Send POST request with JSON: {\"username\":\"your_username\", \"password\":\"your_password\"}";
    }
    
    @GetMapping("/login")
    public String loginInfo() {
        return "Login endpoint. Send POST request with JSON: {\"username\":\"your_username\", \"password\":\"your_password\"}";
    }
    
    @GetMapping("/status")
    public Map<String, String> status() {
        Map<String, String> response = new HashMap<>();

        response.put("status", "GameFlix Auth API is running");
        response.put("endpoints", "POST /api/auth/register, POST /api/auth/login");
        return response;
    }
    



    @PostMapping("/register")
    public Map<String, String> register(@RequestBody RegisterRequest req) {
        Map<String, String> res = new HashMap<>();

        if (req.getUsername() == null || req.getPassword() == null) {
            res.put("message", "Username and password are required");
            return res;
        }

        if (userService.usernameExists(req.getUsername())) {
            res.put("message", "Username already exists");
            return res;
        }

        userService.register(req.getUsername(), req.getPassword());

        res.put("message", "User registered successfully");
        return res;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest req) {
        Map<String, String> res = new HashMap<>();
        boolean ok = userService.login(req.getUsername(), req.getPassword());

        res.put("message", ok ? "Login successful" : "Invalid username or password");
        return res;
    }
}