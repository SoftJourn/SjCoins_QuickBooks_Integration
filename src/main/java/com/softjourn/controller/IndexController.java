package com.softjourn.controller;

import com.intuit.ipp.exception.FMSException;
import com.softjourn.dto.ProductDTO;
import com.softjourn.dto.PurchaseDTO;
import com.softjourn.dto.SjCoinsCredentials;
import com.softjourn.dto.UserDTO;
import com.softjourn.service.QuickBooksService;
import com.softjourn.service.SjCoinsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@Controller
public class IndexController {

    private SjCoinsService sjCoinsService;
    private QuickBooksService qbService;

    public IndexController(SjCoinsService sjCoinsService, QuickBooksService qbService) {
        this.sjCoinsService = sjCoinsService;
        this.qbService = qbService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @ResponseBody
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public Map<String, String> importData(@RequestBody SjCoinsCredentials credentials) throws FMSException {
        sjCoinsService.setSjCoinsCredentials(credentials);

        List<UserDTO> users = sjCoinsService.getAllUsers();
        List<ProductDTO> products = sjCoinsService.getAllProducts();
        List<PurchaseDTO> purchases = sjCoinsService.getAllPurchases();

        qbService.addCustomers(users);
        qbService.addItems(products);
        qbService.addSalesReceipts(purchases, users);

        return Collections.singletonMap("response", "SUCCESS");
    }
}
