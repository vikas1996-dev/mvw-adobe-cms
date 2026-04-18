package com.mvw.core.config;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.AttributeDefinition;

@ObjectClassDefinition(name = "Mule API Configuration", description = "Configuration for Mule Soft Apis")
public @interface MuleApiConfig {

    @AttributeDefinition(name = "Country API URL", description = "Base URL for Location")
    String apiUrl();

    @AttributeDefinition(name = "AXIS-AUTH", description = "Authentication TOken")
    String jwtToken();
    
    @AttributeDefinition(name = "Case API URL", description = "Base URL for Case")
    String caseUrl();

    @AttributeDefinition(name = "Lead API URL", description = "Base URL for Lead")
    String leadUrl();
}
