package com.example.bloogy.configration;

import com.example.bloogy.service.UserService;
import com.example.bloogy.model.User;
import com.example.bloogy.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final UserRepository userRepository;
    private final UserService userService;
    private final String googleClientId;
    private final String googleClientSecret;
    private final String googleRedirectUri;

    public SecurityConfig(
            UserRepository userRepository,
            UserService userService,
            @Value("${app.oauth.google.client-id}") String googleClientId,
            @Value("${app.oauth.google.client-secret}") String googleClientSecret,
            @Value("${app.oauth.google.redirect-uri}") String googleRedirectUri) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.googleClientId = googleClientId;
        this.googleClientSecret = googleClientSecret;
        this.googleRedirectUri = googleRedirectUri;
    }

    // /api/** istekleri icin 401 JSON dondurir (redirect etmez)
    @Bean
    public AuthenticationEntryPoint apiAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(401);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Giris yapmaniz gerekiyor\"}");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().and()
                .sessionManagement()
                // Session bazli auth (cookie ile calisir)
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .exceptionHandling()
                // API istekleri 401 alir, diger istekler login sayfasina gider
                .defaultAuthenticationEntryPointFor(
                        apiAuthenticationEntryPoint(),
                        request -> request.getRequestURI().startsWith("/api/")
                )
                .and()
                .authorizeRequests()
                .antMatchers("/login", "/oauth2/**", "/login/oauth2/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                // Login basarili → FE ana sayfasina yonlendir
                .defaultSuccessUrl("http://localhost:18080", true)
                .clientRegistrationRepository(clientRegistrationRepository())
                // Login basarisiz → FE login sayfasina yonlendir
                .failureUrl("http://localhost:18080/login?error=true")
                .userInfoEndpoint()
                .userService(oAuth2UserService())
                .and()
                .and()
                .logout()
                .logoutUrl("/logout")
                // Cikis → FE login sayfasina yonlendir
                .logoutSuccessUrl("http://localhost:18080/login?logout=true");

        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return userRequest -> {
            DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
            OAuth2User oAuth2User = delegate.loadUser(userRequest);
            User user = userService.getOrCreateUser(userRequest, oAuth2User);
            return new DefaultOAuth2User(
                    Collections.singleton(() -> user.getRole()),
                    oAuth2User.getAttributes(),
                    "name"
            );
        };
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration registration = ClientRegistration.withRegistrationId("google")
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .redirectUri(googleRedirectUri)
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://openidconnect.googleapis.com/v1/userinfo")
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .userNameAttributeName("sub")
                .clientName("Google")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("openid", "email", "profile")
                .build();

        return new InMemoryClientRegistrationRepository(registration);
    }
}
