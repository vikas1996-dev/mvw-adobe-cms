package com.mvw.core.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.mvw.core.services.GlobalConfigs;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GlobalConfigsServiceImplTest {

    private GlobalConfigsServiceImpl service;

    @Mock
    private GlobalConfigs config;

    @BeforeEach
    void setUp() {
        service = new GlobalConfigsServiceImpl();
        
        when(config.site_id()).thenReturn("tmvc");
        when(config.adobe_launch_url()).thenReturn("https://assets.adobedtm.com/launch.js");
        when(config.gtm_container_id()).thenReturn("GTM-XXXXX");
        when(config.cookie_consent_script()).thenReturn("<script>cookie consent</script>");
        when(config.privacy_notice()).thenReturn("Privacy Notice Text");
        
        service.activate(config);
    }

    @Test
    void testGetAdobeLaunchConfig() {
        String result = service.getAdobeLaunchConfig();
        assertEquals("https://assets.adobedtm.com/launch.js", result);
    }

    @Test
    void testGetGtmContainerId() {
        String result = service.getGtmContainerId();
        assertTrue(result.contains("GTM-XXXXX"));
        assertTrue(result.contains("<script"));
        assertTrue(result.contains("googletagmanager.com"));
    }

    @Test
    void testGetCookieConsentScript() {
        String result = service.getCookieConsentScript();
        assertEquals("<script>cookie consent</script>", result);
    }

    @Test
    void testGetSiteId() {
        String result = service.getSiteId();
        assertEquals("tmvc", result);
    }

    @Test
    void testGetPrivacyNotice() {
        String result = service.getPrivacyNotice();
        assertEquals("Privacy Notice Text", result);
    }

    @Test
    void testGetConfigForSite() {
        GlobalConfigs result = service.getConfigForSite("tmvc");
        assertNotNull(result);
        assertEquals(config, result);
    }

    @Test
    void testGetConfigForSite_NotFound() {
        GlobalConfigs result = service.getConfigForSite("nonexistent");
        assertNull(result);
    }

    @Test
    void testMultipleSiteConfigs() {
        GlobalConfigsServiceImpl service2 = new GlobalConfigsServiceImpl();
        GlobalConfigs config2 = mock(GlobalConfigs.class);
        when(config2.site_id()).thenReturn("newclub");
        when(config2.adobe_launch_url()).thenReturn("https://newclub.launch.js");
        
        service2.activate(config2);
        
        // Original service should still have tmvc config
        assertEquals(config, service.getConfigForSite("tmvc"));
        
        // New config should be accessible via the static map
        GlobalConfigs newclubConfig = service.getConfigForSite("newclub");
        assertNotNull(newclubConfig);
    }

    @Test
    void testModifiedConfig() {
        GlobalConfigs modifiedConfig = mock(GlobalConfigs.class);
        when(modifiedConfig.site_id()).thenReturn("tmvc");
        when(modifiedConfig.adobe_launch_url()).thenReturn("https://modified.launch.js");
        
        // Simulate modified callback
        service.activate(modifiedConfig);
        
        assertEquals("https://modified.launch.js", service.getAdobeLaunchConfig());
    }
}
