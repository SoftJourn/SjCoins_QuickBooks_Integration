package com.softjourn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softjourn.dto.ProductDTO;
import com.softjourn.dto.PurchaseDTO;
import com.softjourn.dto.SjCoinsCredentials;
import com.softjourn.dto.UserDTO;
import com.softjourn.service.QuickBooksService;
import com.softjourn.service.SjCoinsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(IndexController.class)
public class IndexControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SjCoinsService sjCoinsService;

    @MockBean
    private QuickBooksService quickBooksService;

    private SjCoinsCredentials sjCoinsCredentials;

    @Before
    public void setUp() throws Exception {
        sjCoinsCredentials = new SjCoinsCredentials("ironman", "incorrect");

        final UserDTO user1 = new UserDTO(
                "antman",
                "Ant Man",
                "antman@softjourn.com");

        final UserDTO user2 = new UserDTO(
                "superman",
                "Super Man",
                "superman@softjourn.com");

        final ProductDTO product1 = new ProductDTO(
                "Pepsi",
                "Great drink",
                new BigDecimal("155"));
        final ProductDTO product2 = new ProductDTO(
                "Coca-Cola",
                "Great drink",
                new BigDecimal("200"));
        final ProductDTO product3 = new ProductDTO(
                "Snickers",
                "Great snack",
                new BigDecimal("250"));

        final PurchaseDTO purchase1 = new PurchaseDTO(
                "antman",
                new Date(), "Pepsi",
                new BigDecimal("155"));
        final PurchaseDTO purchase2 = new PurchaseDTO(
                "superman",
                new Date(), "Coca-Cola",
                new BigDecimal("200"));

        when(sjCoinsService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));
        when(sjCoinsService.getAllProducts()).thenReturn(Arrays.asList(product1, product2, product3));
        when(sjCoinsService.getAllPurchases()).thenReturn(Arrays.asList(purchase1, purchase2));

        doNothing().when(quickBooksService).addCustomers(any());
        doNothing().when(quickBooksService).addItems(any());
        doNothing().when(quickBooksService).addSalesReceipts(any(), any());
    }

    @Test
    public void importData() throws Exception {
        Map<String, String> response = Collections.singletonMap("response", "SUCCESS");

        mockMvc.perform(post("/import")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sjCoinsCredentials)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}
