package com.softjourn.service;


import com.softjourn.dto.ProductDTO;
import com.softjourn.dto.PurchaseDTO;
import com.softjourn.dto.SjCoinsCredentials;
import com.softjourn.dto.UserDTO;

import java.util.List;


public interface SjCoinsService {
    List<UserDTO> getAllUsers();

    void setSjCoinsCredentials(SjCoinsCredentials sjCoinsCredentials);

    SjCoinsCredentials getSjCoinsCredentials();

    List<ProductDTO> getAllProducts();

    List<PurchaseDTO> getAllPurchases();
}
