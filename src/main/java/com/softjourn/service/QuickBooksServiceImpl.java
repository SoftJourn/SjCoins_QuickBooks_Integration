package com.softjourn.service;

import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.BatchOperation;
import com.intuit.ipp.services.DataService;
import com.softjourn.dto.ProductDTO;
import com.softjourn.dto.PurchaseDTO;
import com.softjourn.dto.UserDTO;
import com.softjourn.exception.AccountCreationException;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.rmi.server.UID;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuickBooksServiceImpl implements QuickBooksService {

    private static final int CHUNK_LENGTH = 30;

    private final QuickBooksDataServiceProvider dataServiceProvider;

    public QuickBooksServiceImpl(QuickBooksDataServiceProvider dataServiceProvider) {
        this.dataServiceProvider = dataServiceProvider;
    }

    @Override
    public void addCustomers(List<UserDTO> users) throws FMSException {
        DataService dataService = dataServiceProvider.getDataService();

        List<BatchOperation> batchOperations = new ArrayList<>();

        List<List<UserDTO>> chunksOfUsers = StreamEx.ofSubLists(users, CHUNK_LENGTH).toList();

        chunksOfUsers.forEach(chunk -> chunk.forEach(user -> {
            BatchOperation batchOperation = new BatchOperation();
            batchOperation.addEntity(
                    convertToCustomer(user),
                    OperationEnum.CREATE,
                    getBId());

            batchOperations.add(batchOperation);
        }));

        List<BatchOperation> resultBatchOperations = batchOperations.stream()
                .map(batchOperation -> {
                    try {
                        dataService.executeBatch(batchOperation);
                    } catch (FMSException e) {
                        log.error("Exception happened during customer creation", e);
                    }

                    return batchOperation;
                })
                .collect(Collectors.toList());
    }

    private String getBId() {
        return new UID().toString().replaceAll("\\:.*\\:", "");
    }

    private Customer convertToCustomer(UserDTO user) {
        EmailAddress email = new EmailAddress();
        email.setAddress(user.getEmail());
        email.setDefault(true);

        Customer customer = new Customer();
        customer.setFullyQualifiedName(user.getFullName());

        if (user.getFullName().matches(".*\\s.*")) {
            customer.setGivenName(user.getFullName().split("\\s")[0]);
            customer.setFamilyName(user.getFullName().split("\\s")[1]);
        }

        customer.setPrimaryEmailAddr(email);
        customer.setUserId(user.getLdapId());

        return customer;
    }

    @Override
    public List<Customer> getAllCustomers() throws FMSException {
        DataService dataService = dataServiceProvider.getDataService();

        return Optional.ofNullable(dataService.findAll(new Customer())).orElse(Collections.emptyList());
    }

    @Override
    public List<Account> getAllAccounts() throws FMSException {
        DataService dataService = dataServiceProvider.getDataService();

        return Optional.ofNullable(dataService.findAll(new Account())).orElse(Collections.emptyList());
    }

    @Override
    public List<Item> getAllItems() throws FMSException {
        DataService dataService = dataServiceProvider.getDataService();

        return Optional.ofNullable(dataService.findAll(new Item())).orElse(Collections.emptyList());
    }

    @Override
    public void addItems(List<ProductDTO> products) throws FMSException {
        DataService dataService = dataServiceProvider.getDataService();

        List<BatchOperation> batchOperations = new ArrayList<>();

        List<List<ProductDTO>> chunksOfProducts = StreamEx.ofSubLists(products, CHUNK_LENGTH).toList();

        List<Account> accounts = getAllAccounts();

        String accountCreationExceptionMsg = "Exception happened during account creation";

        final Account assetAccount = accounts.stream()
                .filter(account -> account.getName().equals("SJ Inventory Asset"))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseGet(() -> {
                    Account newAssetAccount = new Account();
                    newAssetAccount.setAccountType(AccountTypeEnum.OTHER_CURRENT_ASSET);
                    newAssetAccount.setName("SJ Inventory Asset");
                    newAssetAccount.setAccountSubType("Inventory");

                    try {
                        return dataService.add(newAssetAccount);
                    } catch (FMSException e) {
                        throw new AccountCreationException(accountCreationExceptionMsg, e);
                    }
                });

        final Account expenseAccount = accounts.stream()
                .filter(account -> account.getName().equals("SJ Cost of Goods Sold"))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseGet(() -> {
                    Account newExpenseAccount = new Account();
                    newExpenseAccount.setAccountType(AccountTypeEnum.COST_OF_GOODS_SOLD);
                    newExpenseAccount.setName("SJ Cost of Goods Sold");
                    newExpenseAccount.setAccountSubType("SuppliesMaterialsCogs");

                    try {
                        return dataService.add(newExpenseAccount);
                    } catch (FMSException e) {
                        throw new AccountCreationException(accountCreationExceptionMsg, e);
                    }
                });

        final Account incomeAccount = accounts.stream()
                .filter(account -> account.getName().equals("Sales"))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseGet(() -> {
                    Account newIncomeAccount = new Account();
                    newIncomeAccount.setAccountType(AccountTypeEnum.INCOME);
                    newIncomeAccount.setName("Sales");

                    try {
                        return dataService.add(newIncomeAccount);
                    } catch (FMSException e) {
                        throw new AccountCreationException(accountCreationExceptionMsg, e);
                    }
                });

        chunksOfProducts.forEach(chunk -> {
            BatchOperation batchOperation = new BatchOperation();

            chunk.forEach(product -> batchOperation.addEntity(
                    convertToItem(product, assetAccount, expenseAccount, incomeAccount),
                    OperationEnum.CREATE,
                    getBId()));

            batchOperations.add(batchOperation);
        });

        List<BatchOperation> resultBatchOperations = batchOperations.stream()
                .map(batchOperation -> {
                    try {
                        dataService.executeBatch(batchOperation);
                    } catch (FMSException e) {
                        log.error("Exception happened during item creation", e);
                    }

                    return batchOperation;
                })
                .collect(Collectors.toList());
    }

    private Item convertToItem(ProductDTO product,
                               Account assetAccount,
                               Account expenseAccount,
                               Account incomeAccount) {

        ReferenceType assetAccountRef = new ReferenceType();
        assetAccountRef.setName(assetAccount.getName());
        assetAccountRef.setValue(assetAccount.getId());

        ReferenceType expenseAccountRef = new ReferenceType();
        expenseAccountRef.setName(expenseAccount.getName());
        expenseAccountRef.setValue(expenseAccount.getId());

        ReferenceType incomeAccountRef = new ReferenceType();
        incomeAccountRef.setName(incomeAccount.getName());
        incomeAccountRef.setValue(incomeAccount.getId());

        Item item = new Item();
        item.setType(ItemTypeEnum.INVENTORY);
        item.setName(product.getName());
        item.setDescription(product.getDescription());
        item.setUnitPrice(product.getPrice());
        item.setPurchaseCost(product.getPrice());
        item.setQtyOnHand(BigDecimal.ZERO);
        item.setTrackQtyOnHand(true);
        item.setTaxable(false);
        item.setInvStartDate(new Date());
        item.setAssetAccountRef(assetAccountRef);
        item.setExpenseAccountRef(expenseAccountRef);
        item.setIncomeAccountRef(incomeAccountRef);

        return item;
    }

    @Override
    public void addSalesReceipts(List<PurchaseDTO> purchases, List<UserDTO> accounts) throws FMSException {
        DataService dataService = dataServiceProvider.getDataService();

        List<Customer> allCustomers = getAllCustomers();
        List<Item> allItems = getAllItems();

        List<BatchOperation> batchOperations = new ArrayList<>();

        List<List<PurchaseDTO>> chunksOfPurchases = StreamEx.ofSubLists(purchases, CHUNK_LENGTH).toList();

        chunksOfPurchases.forEach(chunk -> {
            BatchOperation batchOperation = new BatchOperation();

            chunk.forEach(purchase -> {

                ArrayList<Line> lineItems = new ArrayList<>();

                Line lineItem = new Line();
                lineItem.setDetailType(LineDetailTypeEnum.SALES_ITEM_LINE_DETAIL);

                allItems.stream()
                        .filter(item -> item.getName().equals(purchase.getProduct()))
                        .filter(Objects::nonNull)
                        .findFirst()
                        .ifPresent(item -> {
                            ReferenceType itemRef = new ReferenceType();
                            itemRef.setName(item.getName());
                            itemRef.setValue(item.getId());

                            SalesItemLineDetail lineDetail = new SalesItemLineDetail();
                            lineDetail.setItemRef(itemRef);
                            lineDetail.setQty(BigDecimal.ONE);
                            lineDetail.setUnitPrice(purchase.getPrice());

                            lineItem.setSalesItemLineDetail(lineDetail);
                        });

                lineItem.setAmount(purchase.getPrice());
                lineItem.setDescription(String.format("Buying the %s", purchase.getProduct()));

                lineItems.add(lineItem);

                SalesReceipt salesReceipt = new SalesReceipt();
                salesReceipt.setLine(lineItems);

                Optional<Customer> purchaseCustomer = accounts.stream()
                        .filter(user -> user.getLdapId().equals(purchase.getAccount()))
                        .flatMap(user -> allCustomers.stream()
                                .filter(customer -> customer.getFullyQualifiedName().equals(user.getFullName())))
                        .filter(Objects::nonNull)
                        .findFirst();

                if (purchaseCustomer.isPresent()) {
                    ReferenceType customerRef = new ReferenceType();
                    customerRef.setName(purchaseCustomer.get().getFullyQualifiedName());
                    customerRef.setValue(purchaseCustomer.get().getId());

                    salesReceipt.setCustomerRef(customerRef);
                }

                batchOperation.addEntity(salesReceipt, OperationEnum.CREATE, getBId());
            });

            batchOperations.add(batchOperation);
        });

        List<BatchOperation> resultBatchOperations = batchOperations.stream()
                .map(batchOperation -> {
                    try {
                        dataService.executeBatch(batchOperation);
                    } catch (FMSException e) {
                        log.error("Exception happened during sales receipt creation", e);
                    }

                    return batchOperation;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<SalesReceipt> getAllSalesReceipts() throws FMSException {
        DataService dataService = dataServiceProvider.getDataService();

        return dataService.findAll(new SalesReceipt());
    }
}
