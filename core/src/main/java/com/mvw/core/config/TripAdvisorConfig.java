package com.mvw.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "Tripadvisor API Configuration",
        description = "Configuration for Tripadvisor Content API"
)
public @interface TripAdvisorConfig {

    @AttributeDefinition(
            name = "API URL",
            description = "URL endpoint for Tripadvisor Content API"
    )
    String apiUrl();

    @AttributeDefinition(
            name = "API Key",
            description = "Tripadvisor API Key"
    )
   String apiKey();
}