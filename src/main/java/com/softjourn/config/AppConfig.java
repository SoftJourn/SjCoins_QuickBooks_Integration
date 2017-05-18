package com.softjourn.config;

import com.softjourn.service.QuickBooksDataServiceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;


@Configuration
@PropertySource("classpath:credentials.properties")
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public QuickBooksDataServiceProvider quickBooksDataServiceProvider(
            @Value("${qb.consumer.key}") String consumerKey,
            @Value("${qb.consumer.secret}") String consumerSecret,
            @Value("${qb.access.token}") String accessToken,
            @Value("${qb.acess.token.secret}") String accessTokenSecret,
            @Value("${qb.app.realm.id}") String realmId) {
        return new QuickBooksDataServiceProvider(
                consumerKey, consumerSecret, accessToken, accessTokenSecret, realmId);
    }
}
