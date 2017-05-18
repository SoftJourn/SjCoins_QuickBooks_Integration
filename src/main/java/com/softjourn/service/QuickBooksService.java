package com.softjourn.service;

import com.intuit.ipp.data.Account;
import com.intuit.ipp.data.Customer;
import com.intuit.ipp.data.Item;
import com.intuit.ipp.data.SalesReceipt;
import com.intuit.ipp.exception.FMSException;
import com.softjourn.dto.ProductDTO;
import com.softjourn.dto.PurchaseDTO;
import com.softjourn.dto.UserDTO;

import java.util.List;


public interface QuickBooksService {
    void addCustomers(List<UserDTO> users) throws FMSException;

    List<Customer> getAllCustomers() throws FMSException;

    List<Account> getAllAccounts() throws FMSException;

    List<Item> getAllItems() throws FMSException;

    void addItems(List<ProductDTO> products) throws FMSException;

    void addSalesReceipts(List<PurchaseDTO> purchases, List<UserDTO> accounts) throws FMSException;

    List<SalesReceipt> getAllSalesReceipts() throws FMSException;
}
