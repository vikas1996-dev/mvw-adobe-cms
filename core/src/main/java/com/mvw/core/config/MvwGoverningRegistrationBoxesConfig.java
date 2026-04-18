package com.mvw.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "MVW Governing Registration Boxes",
        description = "OSGi configuration for governing registration notification recipients and sender")
public @interface MvwGoverningRegistrationBoxesConfig {

    @AttributeDefinition(name = "Marriott's Registration Box", description = "Email recipients for Marriott governing registration notifications")
    String marriotts_registration_box() default "";

    @AttributeDefinition(name = "Hyatt Registration Box", description = "Email recipients for Hyatt governing registration notifications")
    String hyatt_registration_box() default "";

    @AttributeDefinition(name = "From Address", description = "Sender email address for governing registration notifications")
    String from_address() default "";
}
