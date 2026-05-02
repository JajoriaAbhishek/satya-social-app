package com.satyasocial.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Slf4j
public class PanVerificationService {

    private final WebClient webClient;

    @Value("${setu.pan.client-id}")
    private String clientId;

    @Value("${setu.pan.client-secret}")
    private String clientSecret;

    @Value("${setu.pan.product-instance-id}")
    private String productInstanceId;

    public PanVerificationService(WebClient.Builder webClientBuilder,
                                  @Value("${setu.pan.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public String verifyAndGetName(String pan) {
        try {
            log.info("Calling Setu PAN API with clientId: {} productInstanceId: {}",
                    clientId, productInstanceId);

            Map response = webClient.post()
                    .uri("/api/verify/pan")
                    .header("x-client-id", clientId)
                    .header("x-client-secret", clientSecret)
                    .header("x-product-instance-id", productInstanceId)
                    .header("Content-Type", "application/json")
                    .bodyValue(Map.of(
                            "pan", pan,
                            "consent", "Y",
                            "reason", "Account verification for SatyaSocial platform"
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("Empty response from PAN verification");
            }
            log.info("Full Setu response: {}", response);


            log.info("PAN verification response: {}", response);

            Map data = (Map) response.get("data");
            if (data == null) {
                throw new RuntimeException("Invalid PAN number");
            }

            String fullName = (String) data.get("full_name");
            if (fullName == null || fullName.isEmpty()) {
                throw new RuntimeException("Could not retrieve name from PAN");
            }

            return fullName;

        } catch (Exception e) {
            log.error("PAN verification failed: {}", e.getMessage());
            throw new RuntimeException("PAN verification failed: " + e.getMessage());
        }
    }
}