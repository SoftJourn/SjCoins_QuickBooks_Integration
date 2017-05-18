package com.softjourn.service;

import com.softjourn.dto.AccessTokenDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:creds.properties")
public class SjCoinsOAuthServiceTest {

    @Autowired
    private SjCoinsOAuthService sjCoinsOAuthService;

    @Value("${sjcoins.username}")
    private String username;

    @Value("${sjcoins.password}")
    private String password;


    @Test
    public void getAccessTokenWithClientCredentialsFlow() throws Exception {
        AccessTokenDTO tokenDTO = sjCoinsOAuthService.getAccessTokenDTO(username, password);

        assertNotNull("TokenDTO object can't be null", tokenDTO);
        assertNotNull("Access token can't be null", tokenDTO.getAccessToken());
    }
}
