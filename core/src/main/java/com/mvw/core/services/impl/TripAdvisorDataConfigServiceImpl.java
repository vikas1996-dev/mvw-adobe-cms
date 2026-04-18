package com.mvw.core.services.impl;

import lombok.Getter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Component(service = TripAdvisorDataConfigServiceImpl.class, immediate = true)
@Designate(ocd = TripAdvisorDataConfigServiceImpl.Config.class)
public class TripAdvisorDataConfigServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(TripAdvisorDataConfigServiceImpl.class);

    /**
     * -- GETTER --
     *  Getter method
     */
    private String cfStoredPath;

    /**
     * OSGi Configuration Definition
     */
    @ObjectClassDefinition(name = "TripAdvisor API Data Configuration",
                            description = "Configuration for specifying the storage path for TripAdvisor API Data to be store as CFs.")
    public @interface Config {

        @AttributeDefinition(name = "CFs Content Path",
            description = "Configuration for TripAdvisor Content Fragment storage path, path till direct parent folder should be there in AEM.")
        String cfStoredPath() default "";
    }

    /**
     * Activate method reads OSGi config
     */
    @Activate
    protected void activate(Config config) {
        this.cfStoredPath = config.cfStoredPath();
        LOGGER.info("TripAdvisor CF Path configured as: {}", cfStoredPath);
    }
}
