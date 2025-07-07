package com.app.boilerplate.Config;

import com.app.boilerplate.Security.*;
import com.app.boilerplate.Util.AppConsts;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@Configuration
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger/login")
            .authorizeHttpRequests(auth -> auth.requestMatchers("/swagger/login")
                .permitAll()
                .anyRequest()
                .authenticated())
            .formLogin(form -> form.loginPage("/swagger/login")
                .failureUrl("/swagger/login?error=true")
                .permitAll())
            .logout(logout -> logout.logoutUrl("/swagger/logout")
                .logoutSuccessUrl("/swagger/login?logout=true"))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain restApiSecurityFilterChain(
        HttpSecurity http,
        JwtDecoder jwtDecoder,
        JwtAuthenticationConverter authenticationConverter,
        ClientRegistrationRepository repository,
        OAuth2AuthenticationSuccessHandler successHandler,
        OAuth2UserService oAuth2UserService) throws Exception {
        http
            .anonymous(anonymous -> anonymous
                .principal(AppConsts.ANONYMOUS_USER)
                .authorities("ROLE_AUTHENTICATION_ANONYMOUS"))
            .authorizeHttpRequests(auth -> auth.anyRequest()
                .permitAll())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt.decoder(jwtDecoder)
                        .jwtAuthenticationConverter(authenticationConverter))
                    .authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
            .oauth2Login(oauth -> oauth
                .userInfoEndpoint(info -> info.userService(oAuth2UserService))
                .authorizationEndpoint(aep -> aep.authorizationRequestResolver(authorizationRequestResolver(repository)))
                .successHandler(successHandler));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
        AuthenticationUserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder,
        PreAuthenticationChecker preAuthenticationChecker) {
        final var authProvider = new DaoAuthenticationProvider();
        authProvider.setPreAuthenticationChecks(preAuthenticationChecker);
        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver(
        ClientRegistrationRepository clientRegistrationRepository) {
        OAuth2AuthorizationRequestResolver defaultAuthorizationRequestResolver =
            new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository,
                OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
        return new AuthorizationRequestResolver(defaultAuthorizationRequestResolver);
    }

}
