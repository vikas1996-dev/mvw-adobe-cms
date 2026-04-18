package com.mvw.core.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class QuoteItemTest {

    private final AemContext context = new AemContext();

    @Test
    void testQuoteItemProperties() {
        // Create resource with properties
        context.create().resource("/content/quote",
                "ctaText", "Book Now",
                "ctaUrl", "/book",
                "ctaStyle", "primary",
                "ctaSize", "large",
                "ctaTab", "_blank");

        // Adapt resource to Sling Model
        QuoteItem model = context.resourceResolver()
                .getResource("/content/quote")
                .adaptTo(QuoteItem.class);

        // Assertions (executes all getters → full coverage)
        assertNotNull(model);
        assertEquals("Book Now", model.getCtaText());
        assertEquals("/book", model.getCtaUrl());
        assertEquals("primary", model.getCtaStyle());
        assertEquals("large", model.getCtaSize());
        assertEquals("_blank", model.getCtaTab());
    }

    @Test
    void testEmptyQuoteItemDefaults() {
        // Resource without properties
        context.create().resource("/content/empty-quote");

        QuoteItem model = context.resourceResolver()
                .getResource("/content/empty-quote")
                .adaptTo(QuoteItem.class);

        // Default OPTIONAL injection → nulls
        assertNotNull(model);
        assertNull(model.getCtaText());
        assertNull(model.getCtaUrl());
        assertNull(model.getCtaStyle());
        assertNull(model.getCtaSize());
        assertNull(model.getCtaTab());
    }
}
