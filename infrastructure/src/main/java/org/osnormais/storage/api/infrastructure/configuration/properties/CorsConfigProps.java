package org.osnormais.storage.api.infrastructure.configuration.properties;

import java.util.List;

public record CorsConfigProps(
        String pattern,
        List<String> allowedOrigins,
        List<String> allowedMethods,
        List<String> allowedHeaders,
        Boolean allowCredentials) {

}
