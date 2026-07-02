package org.osnormais.storage.api.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ConfigurationPropertiesScan
@EnableScheduling
@ComponentScan("org.osnormais.storage.api")
public class WebServerConfig {

}
