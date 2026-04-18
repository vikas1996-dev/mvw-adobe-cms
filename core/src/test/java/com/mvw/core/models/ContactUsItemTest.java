package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContactUsItemTest {

    private ContactUsItem contactUsItem;

    @BeforeEach
    void setUp() {
        contactUsItem = new ContactUsItem();

        setField(contactUsItem, "contactUsCopy", "Contact Us");
        setField(contactUsItem, "contactUsUrl", "/content/mvw/contact");
        setField(contactUsItem, "contactUsTab", "_self");
        setField(contactUsItem, "contactCtaStyle", "primary");
        setField(contactUsItem, "contactCtaAlignment", "center");
        setField(contactUsItem, "contactCtaSize", "medium");
        setField(contactUsItem, "contactUsIcon", "fa-phone");
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
    void testGetContactUsCopy() {
        assertEquals("Contact Us", contactUsItem.getContactUsCopy());
    }

    @Test
    void testGetContactUsUrl() {
        assertEquals("/content/mvw/contact", contactUsItem.getContactUsUrl());
    }

    @Test
    void testGetContactUsTab() {
        assertEquals("_self", contactUsItem.getContactUsTab());
    }

    @Test
    void testGetContactCtaStyle() {
        assertEquals("primary", contactUsItem.getContactCtaStyle());
    }

    @Test
    void testGetContactCtaAlignment() {
        assertEquals("center", contactUsItem.getContactCtaAlignment());
    }

    @Test
    void testGetContactCtaSize() {
        assertEquals("medium", contactUsItem.getContactCtaSize());
    }

    @Test
    void testGetContactUsIcon() {
        assertEquals("fa-phone", contactUsItem.getContactUsIcon());
    }

    @Test
    void testContactUsTabBlank() {
        setField(contactUsItem, "contactUsTab", "_blank");
        assertEquals("_blank", contactUsItem.getContactUsTab());
    }

    @Test
    void testContactCtaStyleSecondary() {
        setField(contactUsItem, "contactCtaStyle", "secondary");
        assertEquals("secondary", contactUsItem.getContactCtaStyle());
    }

    @Test
    void testNullValues() {
        ContactUsItem emptyItem = new ContactUsItem();
        assertNull(emptyItem.getContactUsCopy());
        assertNull(emptyItem.getContactUsUrl());
        assertNull(emptyItem.getContactUsTab());
        assertNull(emptyItem.getContactCtaStyle());
        assertNull(emptyItem.getContactCtaAlignment());
        assertNull(emptyItem.getContactCtaSize());
        assertNull(emptyItem.getContactUsIcon());
    }
}
