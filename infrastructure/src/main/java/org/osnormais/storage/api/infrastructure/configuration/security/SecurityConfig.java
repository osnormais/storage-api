package org.osnormais.storage.api.infrastructure.configuration.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osnormais.storage.api.infrastructure.configuration.properties.CorsConfigProps;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            final HttpSecurity http,
            @Qualifier("corsConfigurationSource") final CorsConfigurationSource corsConfigurationSource)
            throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakJwtConverter())))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(final Set<CorsConfigProps> corsConfigsProps) {

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        corsConfigsProps.forEach(corsConfigProps -> source.registerCorsConfiguration(
                corsConfigProps.pattern(),
                corsConfiguration(
                        corsConfigProps.allowedOrigins(),
                        corsConfigProps.allowedMethods(),
                        corsConfigProps.allowedHeaders(),
                        corsConfigProps.allowCredentials())));

        return source;
    }

    static CorsConfiguration corsConfiguration(
            final List<String> allowedOriginsPatterns,
            final List<String> allowedMethods,
            final List<String> allowedHeaders,
            final boolean allowCredentials) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(allowedOriginsPatterns);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setAllowCredentials(allowCredentials);
        return configuration;
    }

    @Bean
    @ConfigurationProperties("security.cors")
    Set<CorsConfigProps> corsConfigurationProperties() {
        return new HashSet<>();
    }

}
