package com.mvw.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "CDN Purge Service Configuration",
        description = "OSGi configuration for CDN purge keys")
public @interface CdnPurgeConfig {

    @AttributeDefinition(name = "CDN Purge Key 1", description = "Primary CDN purge key")
    String cdnPurgeKey1() default "";

    @AttributeDefinition(name = "CDN Purge Key 2", description = "Secondary CDN purge key")
    String cdnPurgeKey2() default "";

    @AttributeDefinition(name = "CDN Purge Domain", description = "Domain to be used in CDN cache purging in Publishing workflow.")
    String cdnPurgeDomain() default "";
}
