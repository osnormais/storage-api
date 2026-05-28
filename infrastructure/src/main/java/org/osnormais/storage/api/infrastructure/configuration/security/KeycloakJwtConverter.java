package org.osnormais.storage.api.infrastructure.configuration.security;

import java.util.Collection;

import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class KeycloakJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final KeycloakAuthoritiesConverter authoritiesConverter;

    public KeycloakJwtConverter() {
        this.authoritiesConverter = new KeycloakAuthoritiesConverter();
    }

    @Override
    public AbstractAuthenticationToken convert(@NonNull final Jwt jwt) {
        return new JwtAuthenticationToken(jwt, extractAuthorities(jwt), extractPrincipal(jwt));
    }

    private String extractPrincipal(final Jwt jwt) {
        return jwt.getClaimAsString(JwtClaimNames.SUB);
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(final Jwt jwt) {
        return this.authoritiesConverter.convert(jwt);
    }

}
