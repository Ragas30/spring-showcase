package com.spring.review.config;

import com.spring.review.service.JwtService;
import com.spring.review.service.UserAuthService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private static final String TOKEN_ATTR = "ws_token";
    private static final String PRINCIPAL_ATTR = "ws_principal";

    private final JwtService jwtService;

    private final UserAuthService userAuthService;

    @Override
    public boolean beforeHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            @NonNull Map<String, Object> attributes
    ) {

        MultiValueMap<String, String> query =
                UriComponentsBuilder
                        .fromUri(request.getURI())
                        .build()
                        .getQueryParams();

        String token = query.getFirst("token");

        if (token == null) {
            return false;
        }

        String username;

        try {
            username = jwtService.extractUsername(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }

        if (username == null
                || !jwtService.isTokenValid(token, username)
                || !userAuthService.userExists(username)) {
            return false;
        }

        String role = jwtService.extractRole(token);

        attributes.put(TOKEN_ATTR, token);
        attributes.put(
                PRINCIPAL_ATTR,
                new WebSocketPrincipal(username, role)
        );

        return true;
    }

    @Override
    public void afterHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            Exception exception
    ) {
        // no-op
    }
}
