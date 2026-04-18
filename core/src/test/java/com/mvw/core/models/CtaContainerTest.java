package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CtaContainerTest {

    private CtaContainer ctaContainer;

    @BeforeEach
    void setUp() {
        ctaContainer = new CtaContainer();
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

    @Test
    void testGetCtasWithMultipleItems() {
        CTAItem cta1 = mock(CTAItem.class);
        CTAItem cta2 = mock(CTAItem.class);
        CTAItem cta3 = mock(CTAItem.class);

        setField(ctaContainer, "ctas", Arrays.asList(cta1, cta2, cta3));

        List<CTAItem> ctas = ctaContainer.getCtas();
        assertNotNull(ctas);
        assertEquals(3, ctas.size());
    }

    @Test
    void testGetCtasWithSingleItem() {
        CTAItem cta = mock(CTAItem.class);
        setField(ctaContainer, "ctas", Collections.singletonList(cta));

        List<CTAItem> ctas = ctaContainer.getCtas();
        assertNotNull(ctas);
        assertEquals(1, ctas.size());
    }

    @Test
    void testGetCtasWithEmptyList() {
        setField(ctaContainer, "ctas", Collections.emptyList());

        List<CTAItem> ctas = ctaContainer.getCtas();
        assertNotNull(ctas);
        assertTrue(ctas.isEmpty());
    }

    @Test
    void testGetCtasWithNullList() {
        setField(ctaContainer, "ctas", null);

        List<CTAItem> ctas = ctaContainer.getCtas();
        assertNull(ctas);
    }

    @Test
    void testDefaultContainerHasNullCtas() {
        CtaContainer emptyContainer = new CtaContainer();
        assertNull(emptyContainer.getCtas());
    }
}
