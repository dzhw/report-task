package eu.dzhw.fdz.metadatamanagement.tasks.reporttask.config.oauth2.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * Configure and create all beans related to Spring Security OAuth2 Client logic.
 */
@Configuration
public class OAuth2ClientConfig {

  /**
   * The client registration which will be used to attempt to connect to the identity provider
   * to get an access token.
   *
   * @param issuerUri    The URI of the identity provider (if necessary, with an additional path)
   * @param clientId     The ID of the Client which will try to authenticate and authorize against
   *                     the identity provider
   * @param clientSecret The secret of the Client which will try to authenticate and authorize
   *                     against the identity provider
   * @return All information necessary for Spring Security to attempt to authenticate and authorize
   *         a user against the identity provider
   */
  @Bean
  public ClientRegistration dzhwClientRegistration(
          @Value("${spring.security.oauth2.client.provider.fdz.issuer-uri}")
          final String issuerUri,
          @Value("${spring.security.oauth2.client.registration.fdz.client-id}")
          final String clientId,
          @Value("${spring.security.oauth2.client.registration.fdz.client-secret}")
          final String clientSecret
  ) {
    return ClientRegistrations
            .fromIssuerLocation(issuerUri)
            .registrationId("fdz")
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .build();
  }

  /**
   * A Repository which stores the information needed to authenticate and authorize a client
   * against the identity provider.
   *
   * NOTE: Normally this would be added by Spring Boot, but since report-task does not have
   * a servlet this needs to be added manually.
   *
   * @param fdzClientRegistration information about a client registration
   * @return a repository which stores client registration info
   */
  @Bean
  public ClientRegistrationRepository clientRegistrationRepository(
          ClientRegistration fdzClientRegistration
  ) {
    return new InMemoryClientRegistrationRepository(fdzClientRegistration);
  }

  /**
   * A Service which manages OAuth2 authorized clients.
   *
   * NOTE: Normally this would be added by Spring Boot, but since report-task does not have
   * a servlet this needs to be added manually.
   *
   * @param clientRegistrationRepository the repository which stores client registration info
   * @return the service which will handle managing OAuth2 authorized client(s).
   */
  @Bean
  public OAuth2AuthorizedClientService authorizedClientService(
          ClientRegistrationRepository clientRegistrationRepository
  ) {
    return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
  }

  /**
   * The OAuth2 Client manager which handles the authentication and authorization attempts
   * of a client using stored client registration info.
   *
   * @param clientRegistrationRepository the repository which stores client registration info
   * @param authorizedClientService      the service which manages Oauth2 authorized clients
   * @return a manager which attempts to use the client registration info to authenticate and/or
   *         authorize an Oauth2 client.
   */
  @Bean
  public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager(
          ClientRegistrationRepository clientRegistrationRepository,
          OAuth2AuthorizedClientService authorizedClientService
  ) {
    OAuth2AuthorizedClientProvider authorizedClientProvider =
            OAuth2AuthorizedClientProviderBuilder.builder()
                    .clientCredentials()
                    .build();

    AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
            new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                    clientRegistrationRepository,
                    authorizedClientService
            );
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

    return authorizedClientManager;
  }
}
