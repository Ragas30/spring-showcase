package com.spring.review.config;

import java.security.Principal;

public class WebSocketPrincipal implements Principal {

    private final String username;

    private final String role;

    public WebSocketPrincipal(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public String toString() {
        return username;
    }
}
