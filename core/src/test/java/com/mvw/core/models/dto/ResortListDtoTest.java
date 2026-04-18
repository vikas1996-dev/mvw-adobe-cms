package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResortListDtoTest {

    private ResortListDto resortListDto;

    @BeforeEach
    void setUp() {
        resortListDto = new ResortListDto();
    }

    @Test
    void testGettersAndSetters() {
        resortListDto.setName("Beach Resort");
        resortListDto.setContinent("North America");
        resortListDto.setCountry("USA");
        resortListDto.setRegion("Southeast");
        resortListDto.setState("Florida");
        resortListDto.setSlug("beach-resort");

        assertEquals("Beach Resort", resortListDto.getName());
        assertEquals("North America", resortListDto.getContinent());
        assertEquals("USA", resortListDto.getCountry());
        assertEquals("Southeast", resortListDto.getRegion());
        assertEquals("Florida", resortListDto.getState());
        assertEquals("beach-resort", resortListDto.getSlug());
    }

    @Test
    void testGetCity() {
        // City has no setter - use reflection
        setField(resortListDto, "city", "Miami");
        assertEquals("Miami", resortListDto.getCity());
    }

    @Test
    void testSetAndGetDcmBrand() {
        Tags dcmBrand = new Tags();
        dcmBrand.setName("Marriott");
        
        resortListDto.setDcmBrand(dcmBrand);
        
        assertEquals(dcmBrand, resortListDto.getDcmBrand());
        assertEquals("Marriott", resortListDto.getDcmBrand().getName());
    }

    @Test
    void testSetAndGetLocale() {
        ResortListDto.LocaleInfo localeInfo = new ResortListDto.LocaleInfo();
        localeInfo.setName("en-US");
        localeInfo.setPriority("1");
        
        resortListDto.setLocale(localeInfo);
        
        assertEquals("en-US", resortListDto.getLocale().getName());
        assertEquals("1", resortListDto.getLocale().getPriority());
    }

    @Test
    void testSetAndGetSubLocale() {
        ResortListDto.LocaleInfo subLocaleInfo = new ResortListDto.LocaleInfo();
        subLocaleInfo.setName("en-GB");
        subLocaleInfo.setPriority("2");
        
        resortListDto.setSubLocale(subLocaleInfo);
        
        assertEquals("en-GB", resortListDto.getSubLocale().getName());
        assertEquals("2", resortListDto.getSubLocale().getPriority());
    }

    @Test
    void testLocaleInfoInnerClass() {
        ResortListDto.LocaleInfo localeInfo = new ResortListDto.LocaleInfo();
        
        localeInfo.setName("Test Locale");
        localeInfo.setPriority("5");
        
        assertEquals("Test Locale", localeInfo.getName());
        assertEquals("5", localeInfo.getPriority());
    }

    @Test
    void testNullValues() {
        assertNull(resortListDto.getName());
        assertNull(resortListDto.getContinent());
        assertNull(resortListDto.getCountry());
        assertNull(resortListDto.getRegion());
        assertNull(resortListDto.getState());
        assertNull(resortListDto.getCity());
        assertNull(resortListDto.getSlug());
        assertNull(resortListDto.getDcmBrand());
        assertNull(resortListDto.getLocale());
        assertNull(resortListDto.getSubLocale());
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
