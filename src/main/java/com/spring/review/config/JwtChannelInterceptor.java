package com.spring.review.config;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    public static final String PRINCIPAL_ATTR = "ws_principal";

    @Override
    public Message<?> preSend(
            @NonNull Message<?> message,
            @NonNull MessageChannel channel
    ) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );

        if (accessor != null
                && StompCommand.CONNECT.equals(accessor.getCommand())) {

            Map<String, Object> sessionAttributes =
                    accessor.getSessionAttributes();

            if (sessionAttributes != null) {
                Object principal =
                        sessionAttributes.get(PRINCIPAL_ATTR);

                if (principal instanceof Principal p) {
                    accessor.setUser(p);
                }
            }
        }

        return message;
    }
}
