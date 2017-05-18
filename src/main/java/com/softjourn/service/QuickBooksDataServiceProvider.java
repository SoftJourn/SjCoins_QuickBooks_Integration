package com.softjourn.service;

import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.security.IAuthorizer;
import com.intuit.ipp.security.OAuthAuthorizer;
import com.intuit.ipp.services.DataService;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class QuickBooksDataServiceProvider {
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;
    private String realmId;


    public DataService getDataService() throws FMSException {
        return new DataService(getContext());
    }

    private Context getContext() throws FMSException {
        IAuthorizer authorizer = new OAuthAuthorizer(
                consumerKey,
                consumerSecret,
                accessToken,
                accessTokenSecret
        );

        return new Context(authorizer, ServiceType.QBO, realmId);
    }
}
