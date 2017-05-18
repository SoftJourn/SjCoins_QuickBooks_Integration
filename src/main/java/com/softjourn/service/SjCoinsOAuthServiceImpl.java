package com.softjourn.service;

import com.softjourn.dto.AccessTokenDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
@PropertySource("classpath:credentials.properties")
public class SjCoinsOAuthServiceImpl implements SjCoinsOAuthService {
    private String authServerUrl;
    private String clientId;
    private String clientPassword;
    private RestTemplate restTemplate;

    public SjCoinsOAuthServiceImpl(@Value("${auth.server.url}") String authServerUrl,
                                   @Value("${client.id}") String clientId,
                                   @Value("${client.password}") String clientPassword,
                                   RestTemplate restTemplate) {
        this.authServerUrl = authServerUrl;
        this.clientId = clientId;
        this.clientPassword = clientPassword;
        this.restTemplate = restTemplate;
    }

    @Override
    public AccessTokenDTO getAccessTokenDTO(String username, String password) {
        String url = String.format("%s%s", authServerUrl, "/oauth/token");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("scope", "read write");
        body.add("username", username);
        body.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, getHeaders());
        ResponseEntity<AccessTokenDTO> response = restTemplate.postForEntity(url, request, AccessTokenDTO.class);

        return response.getBody();
    }

    private HttpHeaders getHeaders() {
        String authHeaderValue = String.format(
                "Basic %s",
                Base64Utils.encodeToString(String.format("%s:%s", clientId, clientPassword).getBytes()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.add(HttpHeaders.AUTHORIZATION, authHeaderValue);

        return httpHeaders;
    }
}
