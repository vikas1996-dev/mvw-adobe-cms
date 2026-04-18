package com.mvw.core.config;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.AttributeDefinition;

@ObjectClassDefinition(name = "Jahia API Service Configuration", description = "OSGi Configuration for Jahia API Service")
public @interface JahiaApiConfig {

    @AttributeDefinition(name = "Endpoint", description = "The URI endpoint for Jahia API service")
    String endpoint();

    @AttributeDefinition(name = "Authorization Token", description = "Bearer token for authorization")
    String authToken();

    @AttributeDefinition(name = "Image Path", description = "Endpoint for Image Path")
    String imagePath();

}
