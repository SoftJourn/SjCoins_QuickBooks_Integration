package com.softjourn.service;

import com.softjourn.dto.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SjCoinsServiceTest {

    @Autowired
    private SjCoinsService sjCoinsService;

    @MockBean
    private RestTemplate restTemplate;

    @Value("${auth.server.url}")
    private String authServerUrl;

    @Value("${vending.server.url}")
    private String vendingServerUrl;

    @MockBean
    private SjCoinsOAuthService sjCoinsOAuthService;

    @Before
    public void setUp() throws Exception {
        String usersUrl = String.format("%s%s", authServerUrl, "/v1/users");
        String productsUrl = String.format("%s%s", vendingServerUrl, "/v1/products");
        String purchasesUrl = String.format("%s/v1/purchases/filter?page=%d&size=%d", vendingServerUrl, 0, 100);

        final UserDTO user1 = new UserDTO(
                "antman",
                "Ant Man",
                "antman@softjourn.com");

        final UserDTO user2 = new UserDTO(
                "superman",
                "Super Man",
                "superman@softjourn.com");

        final AccessTokenDTO accessTokenDTO = new AccessTokenDTO(
                "asd123rt",
                "Bearer",
                "123456",
                "read",
                "123werwr313");

        final ProductDTO product1 = new ProductDTO("Pepsi", "desc", new BigDecimal("100"));
        final ProductDTO product2 = new ProductDTO("Coca-Cola", "desc", new BigDecimal("110"));
        final PurchaseDTO purchase1 = new PurchaseDTO(
                "antman",
                new Date(),
                "Pepsi",
                new BigDecimal("110"));
        final PurchaseDTO purchase2 = new PurchaseDTO(
                "superman",
                new Date(),
                "Coca-Cola",
                new BigDecimal("150"));

        final Page<PurchaseDTO> page = new Page<>(
                0,
                10,
                2,
                1,
                2,
                true,
                true,
                Arrays.asList(purchase1, purchase2));

        when(restTemplate.exchange(
                eq(usersUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<List<UserDTO>>() {}))
        ).thenReturn(new ResponseEntity<>(Arrays.asList(user1, user2), HttpStatus.OK));

        sjCoinsService.setSjCoinsCredentials(new SjCoinsCredentials("test", "pass"));

        when(sjCoinsOAuthService.getAccessTokenDTO(anyString(), anyString())).thenReturn(accessTokenDTO);

        when(restTemplate.exchange(
                eq(productsUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<List<ProductDTO>>() {}))
        ).thenReturn(new ResponseEntity<>(Arrays.asList(product1, product2), HttpStatus.OK));

        when(restTemplate.exchange(
                eq(purchasesUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<Page<PurchaseDTO>>() {}))
        ).thenReturn(new ResponseEntity<>(page, HttpStatus.OK));
    }

    @Test
    public void getUsersFromSjCoins() throws Exception {
        List<UserDTO> users = sjCoinsService.getAllUsers();

        assertTrue("List can't be empty", !users.isEmpty());
        assertNotNull("User ldapId can't be null", users.get(0).getLdapId());
    }

    @Test
    public void getAllProducts() throws Exception {
        List<ProductDTO> products = sjCoinsService.getAllProducts();

        assertNotNull("Products can't be null",products);
        assertTrue("Products can't be empty", !products.isEmpty());
    }

    @Test
    public void getAllPurchases() throws Exception {
        List<PurchaseDTO> purchases = sjCoinsService.getAllPurchases();

        assertNotNull("Purchases can't be null", purchases);
        assertTrue("Purchases can't be empty", !purchases.isEmpty());
    }
}
