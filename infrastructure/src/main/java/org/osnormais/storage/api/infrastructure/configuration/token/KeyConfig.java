package org.osnormais.storage.api.infrastructure.configuration.token;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyConfig {

    private static final Pattern PEM_CONTENT_PATTERN = Pattern
            .compile("(?s)-----BEGIN [A-Z0-9 ]+-----\\s*(.*?)\\s*-----END [A-Z0-9 ]+-----");

    @Bean
    @ConditionalOnProperty(name = "application.token.signature.public-key.source", havingValue = "env")
    byte[] sysEnvKey() {
        return Base64.getDecoder().decode(System.getenv("TOKEN_SIGNATURE_PUBLIC_KEY_VALUE"));
    }

    @Bean
    @ConditionalOnProperty(name = "application.token.signature.public-key.source", havingValue = "file-pem")
    byte[] fileKey(@Value("${token.signature.public-key.file-path}") Path pemKeyPath)
            throws Exception {
        final String pem = Files.readString(pemKeyPath);
        var matcher = PEM_CONTENT_PATTERN.matcher(pem);

        if (matcher.find())
            return Base64.getDecoder().decode(matcher.group(1).replaceAll("\\s", ""));
        else
            throw new IllegalArgumentException("Invalid PEM format");

    }

}
