package com.spring.review.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.review.entity.WebhookSubscriptionEntity;
import com.spring.review.entity.WebhookLogEntity;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookDeliveryService {

    private final EntityManager em;

    private final WebhookService webhookService;

    private final RestTemplateBuilder restTemplateBuilder;

    private final ObjectMapper objectMapper;

    @Async
    @Transactional
    public void deliverEvent(
            String eventName,
            Object payload
    ) {
        List<WebhookSubscriptionEntity> subscriptions =
                em.createQuery(
                        "SELECT w FROM WebhookSubscriptionEntity w WHERE w.isActive = true",
                        WebhookSubscriptionEntity.class
                ).getResultList();

        for (WebhookSubscriptionEntity subscription : subscriptions) {
            try {
                deliverToSubscription(subscription, eventName, payload);
            } catch (Exception e) {
                log.error(
                        "Failed to deliver webhook to {}: {}",
                        subscription.getUrl(),
                        e.getMessage()
                );
                webhookService.recordLog(
                        subscription.getId(),
                        eventName,
                        serializePayload(payload),
                        e.getMessage(),
                        "FAILED"
                );
            }
        }
    }

    private void deliverToSubscription(
            WebhookSubscriptionEntity subscription,
            String eventName,
            Object payload
    ) {
        String payloadJson = serializePayload(payload);
        String signature = generateSignature(
                payloadJson,
                subscription.getSecretKey()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Webhook-Event", eventName);
        headers.set("X-Webhook-Signature", signature);

        HttpEntity<String> request = new HttpEntity<>(
                payloadJson,
                headers
        );

        RestTemplate restTemplate = restTemplateBuilder.build();

        ResponseEntity<String> response = restTemplate.postForEntity(
                subscription.getUrl(),
                request,
                String.class
        );

        String status = response.getStatusCode().is2xxSuccessful()
                ? "SUCCESS"
                : "FAILED";

        webhookService.recordLog(
                subscription.getId(),
                eventName,
                payloadJson,
                response.getBody(),
                status
        );

        log.info(
                "Webhook delivered to {} - event: {} - status: {}",
                subscription.getUrl(),
                eventName,
                status
        );
    }

    private String generateSignature(
            String payload,
            String secretKey
    ) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(
                            secretKey.getBytes(StandardCharsets.UTF_8),
                            "HmacSHA256"
                    );
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(
                    payload.getBytes(StandardCharsets.UTF_8)
            );
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            log.error("Failed to generate signature: {}", e.getMessage());
            throw new RuntimeException("Failed to generate webhook signature", e);
        }
    }

    private String serializePayload(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return "{}";
        }
    }
}
