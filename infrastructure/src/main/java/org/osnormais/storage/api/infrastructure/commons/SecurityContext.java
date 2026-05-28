package org.osnormais.storage.api.infrastructure.commons;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public final class SecurityContext {

    private static final String IDENTIFIER_CLAIM_NAME = "identifier";

    private SecurityContext() {
    }

    public static UUID getAuthenticatedUserId() {
        return Optional
                .ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getCredentials)
                .filter(Jwt.class::isInstance)
                .map(Jwt.class::cast)
                .filter(jwt -> jwt.hasClaim(IDENTIFIER_CLAIM_NAME))
                .map(jwt -> jwt.getClaimAsString(IDENTIFIER_CLAIM_NAME))
                .map(UUID::fromString)
                .orElseThrow();
    }

}
