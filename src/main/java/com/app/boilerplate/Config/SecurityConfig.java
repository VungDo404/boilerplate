package com.app.boilerplate.Config;

import com.app.boilerplate.Security.AuthenticationUserDetailsService;
import com.app.boilerplate.Security.JwtAuthenticationConverter;
import com.app.boilerplate.Security.PreAuthenticationChecker;
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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@Configuration
public class SecurityConfig {
    public static final String[] POST_PUBLIC_URL = {
            "/account/register",
            "/auth/authenticate",
            "/auth/refresh-token"
    };
    public static final String[] GET_PUBLIC_URL = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars" + "/**",
            "/swagger-ui.html"

    };

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http,
                                                          AuthenticationSuccessHandler successHandler) throws Exception {
        http
                .securityMatcher("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger/login")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger/login")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .formLogin(form -> form
                        .loginPage("/swagger/login")
                        .successHandler(successHandler)
                        .failureUrl("/swagger/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/swagger/logout")
                        .logoutSuccessUrl("/swagger/login?logout=true"))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain restApiSecurityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder,
                                                          JwtAuthenticationConverter authenticationConverter) throws Exception {
        http
                .anonymous(anonymous -> anonymous
                        .principal("anonymousUser")
                        .authorities("ROLE_AUTHENTICATION_ANONYMOUS")
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest()
                        .permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(authenticationConverter)
                        )
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(AuthenticationUserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder,
                                                            PreAuthenticationChecker preAuthenticationChecker) {
        final var authProvider = new DaoAuthenticationProvider();
        authProvider.setPreAuthenticationChecks(preAuthenticationChecker);
        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

}
