package com.test;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@Configuration
//@EnableWebSecurity
public class OAuth2ClientSecurityConfig {

    public static final String CLIENT_REGISTRATION_ID = "keycloak";

//    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
//    private final ClientRegistrationRepository clientRegistrationRepository;


    @Bean
    ClientRegistrationRepository clientRegistrations(
            @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}") String token_uri,
            @Value("${spring.security.oauth2.client.registration.keycloak.client-id}") String client_id,
            @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}") String client_secret,
            @Value("${spring.security.oauth2.client.registration.keycloak.authorization-grant-type}") String authorizationGrantType

    ) {
        ClientRegistration registration = ClientRegistration
                .withRegistrationId("keycloak")
                .tokenUri(token_uri)
                .clientId(client_id)
                .clientSecret(client_secret)
                .authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
                .build();
        return new InMemoryClientRegistrationRepository(registration);
    }

    @Bean
    public OAuth2AuthorizedClientService auth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

//    public OAuth2ClientSecurityConfig(OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
//                            ClientRegistrationRepository clientRegistrationRepository) {
//        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
//        this.clientRegistrationRepository = clientRegistrationRepository;
//    }

    @Bean
    public RequestInterceptor requestInterceptor(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(CLIENT_REGISTRATION_ID);
        OAuthClientCredentialsFeignManager clientCredentialsFeignManager =
                new OAuthClientCredentialsFeignManager(authorizedClientManager(clientRegistrationRepository , oAuth2AuthorizedClientService), clientRegistration);
        return requestTemplate -> {
            requestTemplate.header("Authorization", "Bearer " + clientCredentialsFeignManager.getAccessToken());
        };
    }

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,OAuth2AuthorizedClientService oAuth2AuthorizedClientService ) {
        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }

}
