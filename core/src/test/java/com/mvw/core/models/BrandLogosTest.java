package com.mvw.core.models;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class BrandLogosTest {

    @Test
    void testGetLogos() {
        BrandLogos brandLogos = new BrandLogos();

        LogoItem logo1 = new LogoItem();
        LogoItem logo2 = new LogoItem();
        List<LogoItem> logos = Arrays.asList(logo1, logo2);

        setField(brandLogos, "logos", logos);

        List<LogoItem> result = brandLogos.getLogos();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(logo1, result.get(0));
        assertSame(logo2, result.get(1));
    }

    // Reflection helper
    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
