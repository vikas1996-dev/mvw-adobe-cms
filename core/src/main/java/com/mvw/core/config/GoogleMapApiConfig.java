package com.mvw.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "GoogleMap Key Configuration",
        description = "Configuration for GoogleMap Key"
)
public @interface GoogleMapApiConfig {

    @AttributeDefinition(
            name = "Google Map Key",
            description = "Key for GoogleMapAPI"
    )
    String googleMapApiKey();

}