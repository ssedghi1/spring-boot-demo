package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class HelloControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void helloEndpointRequiresAuthentication() {
        webTestClient.get()
                .uri("/hello")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void helloEndpointReturnsHelloWorldWhenAuthenticated() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        webTestClient.mutateWith(
                SecurityMockServerConfigurers.mockJwt()
                        .jwt(jwt -> jwt
                                .claim("iss", "https://login.microsoftonline.com/{tenant-id}/v2.0")
                                .claim("roles", "demo.read"))
                        .authorities(authoritiesConverter))
                .get()
                .uri("/hello")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello World!");
    }
}
