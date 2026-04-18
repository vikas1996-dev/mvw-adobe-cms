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

import com.mvw.core.config.CountryRegionConfig;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CountryRegionServiceImplTest {

    private CountryRegionServiceImpl service;

    @Mock
    private CountryRegionConfig config;

    @BeforeEach
    void setUp() {
        service = new CountryRegionServiceImpl();
        
        when(config.ap_countries()).thenReturn(new String[]{"China", "Japan", "Korea"});
        when(config.au_countries()).thenReturn(new String[]{"Australia", "New Zealand"});
        when(config.eu_countries()).thenReturn(new String[]{"France", "Germany", "UK"});
        when(config.japan_countries()).thenReturn(new String[]{"Japan"});
        when(config.latam_countries()).thenReturn(new String[]{"Mexico", "Brazil", "Argentina"});
        when(config.me_countries()).thenReturn(new String[]{"UAE", "Saudi Arabia"});
        when(config.other_countries()).thenReturn(new String[]{"India", "South Africa"});
        when(config.lead_submission_countries()).thenReturn(new String[]{"Canada"});
        
        service.activate(config);
    }

    @Test
    void testGetAPCountries() {
        String[] countries = service.getAPCountries();
        assertNotNull(countries);
        assertEquals(3, countries.length);
        assertEquals("China", countries[0]);
    }

    @Test
    void testGetAUCountries() {
        String[] countries = service.getAUCountries();
        assertNotNull(countries);
        assertEquals(2, countries.length);
        assertEquals("Australia", countries[0]);
    }

    @Test
    void testGetEUCountries() {
        String[] countries = service.getEUCountries();
        assertNotNull(countries);
        assertEquals(3, countries.length);
    }

    @Test
    void testGetJapanCountries() {
        String[] countries = service.getJapanCountries();
        assertNotNull(countries);
        assertEquals(1, countries.length);
        assertEquals("Japan", countries[0]);
    }

    @Test
    void testGetLATAMCountries() {
        String[] countries = service.getLATAMCountries();
        assertNotNull(countries);
        assertEquals(3, countries.length);
    }

    @Test
    void testGetLeadSubmissionCountries() {
        String[] countries = service.getLeadSubmissionCountries();
        assertNotNull(countries);
        assertEquals(1, countries.length);
    }

    @Test
    void testGetMECountries() {
        String[] countries = service.getMECountries();
        assertNotNull(countries);
        assertEquals(2, countries.length);
    }

    @Test
    void testGetOtherCountries() {
        String[] countries = service.getOtherCountries();
        assertNotNull(countries);
        assertEquals(2, countries.length);
    }

    @Test
    void testGetRegionByCountry_AP() {
        String region = service.getRegionByCountry("China");
        assertEquals("AP", region);
    }

    @Test
    void testGetRegionByCountry_AU() {
        String region = service.getRegionByCountry("Australia");
        assertEquals("AU", region);
    }

    @Test
    void testGetRegionByCountry_EU() {
        String region = service.getRegionByCountry("France");
        assertEquals("EU", region);
    }

    @Test
    void testGetRegionByCountry_Japan() {
        // Japan is in both AP and Japan lists, AP is checked first
        when(config.ap_countries()).thenReturn(new String[]{"China", "Korea"}); // Remove Japan from AP
        service.activate(config);
        String region = service.getRegionByCountry("Japan");
        assertEquals("Japan", region);
    }

    @Test
    void testGetRegionByCountry_LATAM() {
        String region = service.getRegionByCountry("Mexico");
        assertEquals("LATAM", region);
    }

    @Test
    void testGetRegionByCountry_ME() {
        String region = service.getRegionByCountry("UAE");
        assertEquals("ME", region);
    }

    @Test
    void testGetRegionByCountry_Other() {
        String region = service.getRegionByCountry("India");
        assertEquals("Other", region);
    }

    @Test
    void testGetRegionByCountry_LeadSubmission() {
        String region = service.getRegionByCountry("Canada");
        assertEquals("Lead Submission", region);
    }

    @Test
    void testGetRegionByCountry_NotFound() {
        String region = service.getRegionByCountry("Unknown Country");
        assertEquals("Other", region); // Default fallback
    }

    @Test
    void testGetRegionByCountry_Null() {
        String region = service.getRegionByCountry(null);
        assertNull(region);
    }

    @Test
    void testGetRegionByCountry_Empty() {
        String region = service.getRegionByCountry("");
        assertNull(region);
    }

    @Test
    void testGetRegionByCountry_CaseInsensitive() {
        String region = service.getRegionByCountry("china");
        assertEquals("AP", region);
    }

    @Test
    void testGetRegionByCountry_NullCountriesArray() {
        when(config.ap_countries()).thenReturn(null);
        when(config.au_countries()).thenReturn(null);
        when(config.eu_countries()).thenReturn(null);
        when(config.japan_countries()).thenReturn(null);
        when(config.latam_countries()).thenReturn(null);
        when(config.me_countries()).thenReturn(null);
        when(config.other_countries()).thenReturn(null);
        when(config.lead_submission_countries()).thenReturn(null);
        service.activate(config);

        String region = service.getRegionByCountry("USA");
        assertEquals("Other", region); // Default fallback
    }
}
