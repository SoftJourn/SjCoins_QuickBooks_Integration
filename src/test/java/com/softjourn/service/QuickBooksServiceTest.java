package com.softjourn.service;

import com.intuit.ipp.data.Account;
import com.intuit.ipp.data.Customer;
import com.intuit.ipp.data.Item;
import com.intuit.ipp.data.SalesReceipt;
import com.intuit.ipp.services.DataService;
import com.softjourn.dto.ProductDTO;
import com.softjourn.dto.PurchaseDTO;
import com.softjourn.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class QuickBooksServiceTest {

    @Autowired
    private QuickBooksService qbService;

    @MockBean
    private SjCoinsService sjCoinsService;

    @MockBean
    private SjCoinsOAuthService sjCoinsOAuthService;

    @MockBean
    private QuickBooksDataServiceProvider dataServiceProvider;

    @Mock
    private DataService dataService;

    @Before
    public void setUp() throws Exception {
        final UserDTO user1 = new UserDTO(
                "antman",
                "Ant Man",
                "antman@softjourn.com");

        final UserDTO user2 = new UserDTO(
                "superman",
                "Super Man",
                "superman@softjourn.com");

        final ProductDTO product1 = new ProductDTO("Pepsi", "Great drink", new BigDecimal("155"));
        final ProductDTO product2 = new ProductDTO("Coca-Cola", "Great drink", new BigDecimal("200"));
        final ProductDTO product3 = new ProductDTO("Snickers", "Great snack", new BigDecimal("250"));

        final PurchaseDTO purchase1 = new PurchaseDTO(
                "antman",
                new Date(),
                "Pepsi",
                new BigDecimal("155"));
        final PurchaseDTO purchase2 = new PurchaseDTO(
                "superman",
                new Date(),
                "Coca-Cola",
                new BigDecimal("200"));

        final Item item1 = new Item();
        item1.setName("Item 1");
        item1.setUnitPrice(BigDecimal.ONE);

        final Item item2 = new Item();
        item2.setName("Item 2");
        item2.setUnitPrice(BigDecimal.ONE);

        final Customer customer1 = new Customer();
        customer1.setGivenName("Ant");
        customer1.setFamilyName("Man");
        customer1.setFullyQualifiedName("Ant Man");

        final Customer customer2 = new Customer();
        customer2.setGivenName("Super");
        customer2.setFamilyName("Man");
        customer2.setFullyQualifiedName("Super Man");

        final Account account1 = new Account();
        account1.setName("Inventory");

        final Account account2 = new Account();
        account2.setName("Sales");

        final SalesReceipt salesReceipt1 = new SalesReceipt();
        salesReceipt1.setId("123");

        final SalesReceipt salesReceipt2 = new SalesReceipt();
        salesReceipt2.setId("456");

        when(sjCoinsService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));
        when(sjCoinsService.getAllProducts()).thenReturn(Arrays.asList(product1, product2, product3));
        when(sjCoinsService.getAllPurchases()).thenReturn(Arrays.asList(purchase1, purchase2));

        when(dataServiceProvider.getDataService()).thenReturn(dataService);

        doNothing().when(dataService).executeBatch(any());
        when(dataService.findAll(eq(new Item()))).thenReturn(Arrays.asList(item1, item2));
        when(dataService.findAll(eq(new Customer()))).thenReturn(Arrays.asList(customer1, customer2));
        when(dataService.findAll(eq(new Account()))).thenReturn(Arrays.asList(account1, account2));
        when(dataService.findAll(eq(new SalesReceipt()))).thenReturn(Arrays.asList(salesReceipt1, salesReceipt2));
        when(dataService.add(any(Account.class))).thenReturn(account1);
    }

    @Test
    public void createCustomer() throws Exception {
        qbService.addCustomers(sjCoinsService.getAllUsers());

        assertTrue(!qbService.getAllCustomers().isEmpty());
    }

    @Test
    public void addItems() throws Exception {
        List<ProductDTO> products = sjCoinsService.getAllProducts();

        qbService.addItems(products);

        List<Item> items = qbService.getAllItems();

        assertNotNull("Items can't be null", items);
        assertTrue("Items can't be empty", !items.isEmpty());
    }

    @Test
    public void getAllAccounts() throws Exception {
        List<Account> accounts = qbService.getAllAccounts();

        assertNotNull("Accounts can't be null", accounts);
        assertTrue("Accounts can't be empty", !accounts.isEmpty());
    }

    @Test
    public void addSalesReceipts() throws Exception {
        List<PurchaseDTO> purchases = sjCoinsService.getAllPurchases();
        List<UserDTO> accounts = sjCoinsService.getAllUsers();

        qbService.addSalesReceipts(purchases, accounts);

        List<SalesReceipt> salesReceipts = qbService.getAllSalesReceipts();

        assertNotNull("Sales can't be empty", salesReceipts);
        assertTrue("Sales can't be empty", !salesReceipts.isEmpty());
    }
}
