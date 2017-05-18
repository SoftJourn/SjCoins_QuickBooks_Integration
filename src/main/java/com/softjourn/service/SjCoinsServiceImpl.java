package com.softjourn.service;

import com.softjourn.dto.*;
import com.softjourn.exception.SjCoinsCredentialsMissingException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
public class SjCoinsServiceImpl implements SjCoinsService {
    private RestTemplate restTemplate;
    private SjCoinsOAuthService sjCoinsOAuthService;
    private String authServerUrl;
    private String vendingServerUrl;

    @Getter
    @Setter
    private SjCoinsCredentials sjCoinsCredentials;

    public SjCoinsServiceImpl(RestTemplate restTemplate,
                              SjCoinsOAuthService sjCoinsOAuthService,
                              @Value("${auth.server.url}") String authServerUrl,
                              @Value("${vending.server.url}") String vendingServerUrl) {
        this.restTemplate = restTemplate;
        this.sjCoinsOAuthService = sjCoinsOAuthService;
        this.authServerUrl = authServerUrl;
        this.vendingServerUrl = vendingServerUrl;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        String url = String.format("%s%s", authServerUrl, "/v1/users");

        HttpEntity<HttpHeaders> request = new HttpEntity<>(getHeaders());

        ResponseEntity<List<UserDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<UserDTO>>() {});

        return Optional.of(response.getBody()).orElse(Collections.emptyList());
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        String url = String.format("%s%s", vendingServerUrl, "/v1/products");

        HttpEntity<HttpHeaders> request = new HttpEntity<>(getHeaders());

        ResponseEntity<List<ProductDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<ProductDTO>>() {});

        return Optional.of(response.getBody()).orElse(Collections.emptyList());
    }

    @Override
    public List<PurchaseDTO> getAllPurchases() {
        List<PurchaseDTO> purchases = new ArrayList<>();

        int page = 0;
        int size = 100;
        boolean isLastPage = false;

        do {
            String url = String.format("%s/v1/purchases/filter?page=%d&size=%d", vendingServerUrl, page, size);

            PurchasesFilter body = new PurchasesFilter(-1, "Any", -180, "", "");

            HttpEntity<PurchasesFilter> request = new HttpEntity<>(body, getHeaders());

            ResponseEntity<Page<PurchaseDTO>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Page<PurchaseDTO>>() {});

            if (responseEntity.hasBody()) {
                purchases.addAll(responseEntity.getBody().getContent());
                isLastPage = responseEntity.getBody().isLast();
            }

            page += 1;
        } while (!isLastPage);

        return purchases;
    }

    private HttpHeaders getHeaders() {
        if (Objects.isNull(sjCoinsCredentials)) {
            throw new SjCoinsCredentialsMissingException("Credentials to SjCoins servers is missing");
        }

        String authHeaderValue = String.format("Bearer %s",
                sjCoinsOAuthService.getAccessTokenDTO(
                        sjCoinsCredentials.getUsername(),
                        sjCoinsCredentials.getPassword()
                ).getAccessToken()
        );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, authHeaderValue);

        return httpHeaders;
    }
}
