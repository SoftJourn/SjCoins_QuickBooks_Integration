package com.softjourn.service;

import com.softjourn.dto.AccessTokenDTO;


public interface SjCoinsOAuthService {
    AccessTokenDTO getAccessTokenDTO(String username, String password);
}
