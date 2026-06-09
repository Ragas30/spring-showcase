package com.spring.review.controller;


import com.spring.review.common.ErrorCode;
import com.spring.review.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public TestController(PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @GetMapping("/test")
    public String test() {

        throw new com.spring.review.exception.BusinessException(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "Ini test exception"
        );
    }

    @GetMapping("/password")
    public String generatePassword() {
        return passwordEncoder.encode("admin123");
    }

    @GetMapping("/token")
    public String token() {
        return jwtService.generateToken("admin");
    }

    @GetMapping("/token/username")
    public String username() {

        String token = "...";

        return jwtService.extractUsername(token);
    }
}