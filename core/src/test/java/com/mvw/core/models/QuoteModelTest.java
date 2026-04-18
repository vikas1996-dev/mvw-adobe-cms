package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuoteModelTest {

    private QuoteModel quoteModel;

    @BeforeEach
    void setUp() {
        quoteModel = new QuoteModel();
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
    void testGetHeadlineText() {
        setField(quoteModel, "headlineText", "Quote Section");
        assertEquals("Quote Section", quoteModel.getHeadlineText());
    }

    @Test
    void testGetHeadlineType() {
        setField(quoteModel, "headlineType", "heading2");
        assertEquals("heading2", quoteModel.getHeadlineType());
    }

    @Test
    void testGetShortDescription() {
        setField(quoteModel, "shortDescription", "A short description");
        assertEquals("A short description", quoteModel.getShortDescription());
    }

    @Test
    void testGetCopySectionAlignment() {
        setField(quoteModel, "copySectionAlignment", "center");
        assertEquals("center", quoteModel.getCopySectionAlignment());
    }

    @Test
    void testGetCopySectionBgColor() {
        setField(quoteModel, "copySectionBgColor", "white");
        assertEquals("white", quoteModel.getCopySectionBgColor());
    }

    @Test
    void testGetImage() {
        setField(quoteModel, "image", "/content/dam/image.jpg");
        assertEquals("/content/dam/image.jpg", quoteModel.getImage());
    }

    @Test
    void testGetBgImageAlt() {
        setField(quoteModel, "bgImageAlt", "Background image alt text");
        assertEquals("Background image alt text", quoteModel.getBgImageAlt());
    }

    @Test
    void testGetBgImageTopMargin() {
        setField(quoteModel, "bgImageTopMargin", "20px");
        assertEquals("20px", quoteModel.getBgImageTopMargin());
    }

    @Test
    void testGetBgImageAlignmentLeft() {
        setField(quoteModel, "bgImageAlignmentLeft", "left");
        assertEquals("left", quoteModel.getBgImageAlignmentLeft());
    }

    @Test
    void testGetBgImageAlignmentRight() {
        setField(quoteModel, "bgImageAlignmentRight", "right");
        assertEquals("right", quoteModel.getBgImageAlignmentRight());
    }

    @Test
    void testGetBgImageJustification() {
        setField(quoteModel, "bgImageJustification", "center");
        assertEquals("center", quoteModel.getBgImageJustification());
    }

    @Test
    void testGetQuote() {
        setField(quoteModel, "quote", "This is a great quote.");
        assertEquals("This is a great quote.", quoteModel.getQuote());
    }

    @Test
    void testGetQuoteHeadlineType() {
        setField(quoteModel, "quoteHeadlineType", "heading3");
        assertEquals("heading3", quoteModel.getQuoteHeadlineType());
    }

    @Test
    void testGetQuoteJustification() {
        setField(quoteModel, "quoteJustification", "left");
        assertEquals("left", quoteModel.getQuoteJustification());
    }

    @Test
    void testGetAuthor() {
        setField(quoteModel, "author", "John Doe");
        assertEquals("John Doe", quoteModel.getAuthor());
    }

    @Test
    void testGetMobileBgImageTopMargin() {
        setField(quoteModel, "mobileBgImageTopMargin", "10px");
        assertEquals("10px", quoteModel.getMobileBgImageTopMargin());
    }

    @Test
    void testGetBrandIconImageBgColor() {
        setField(quoteModel, "brandIconImageBgColor", "blue");
        assertEquals("blue", quoteModel.getBrandIconImageBgColor());
    }

    @Test
    void testGetCopyAlignment() {
        setField(quoteModel, "copyAlignment", "right");
        assertEquals("right", quoteModel.getCopyAlignment());
    }

    @Test
    void testGetHeadlineTagWithHeading1() {
        setField(quoteModel, "headlineType", "heading1");
        assertEquals("h1", quoteModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading2() {
        setField(quoteModel, "headlineType", "heading2");
        assertEquals("h2", quoteModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading3() {
        setField(quoteModel, "headlineType", "heading3");
        assertEquals("h3", quoteModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading4() {
        setField(quoteModel, "headlineType", "heading4");
        assertEquals("h4", quoteModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading5() {
        setField(quoteModel, "headlineType", "heading5");
        assertEquals("h5", quoteModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading6() {
        setField(quoteModel, "headlineType", "heading6");
        assertEquals("h6", quoteModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithNullType() {
        setField(quoteModel, "headlineType", null);
        assertEquals("h2", quoteModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithInvalidType() {
        setField(quoteModel, "headlineType", "paragraph");
        assertEquals("h2", quoteModel.getHeadlineTag());
    }

    @Test
    void testGetQuoteHeadlineTagWithHeading1() {
        setField(quoteModel, "quoteHeadlineType", "heading1");
        assertEquals("h1", quoteModel.getQuoteHeadlineTag());
    }

    @Test
    void testGetQuoteHeadlineTagWithHeading2() {
        setField(quoteModel, "quoteHeadlineType", "heading2");
        assertEquals("h2", quoteModel.getQuoteHeadlineTag());
    }

    @Test
    void testGetQuoteHeadlineTagWithHeading3() {
        setField(quoteModel, "quoteHeadlineType", "heading3");
        assertEquals("h3", quoteModel.getQuoteHeadlineTag());
    }

    @Test
    void testGetQuoteHeadlineTagWithHeading4() {
        setField(quoteModel, "quoteHeadlineType", "heading4");
        assertEquals("h4", quoteModel.getQuoteHeadlineTag());
    }

    @Test
    void testGetQuoteHeadlineTagWithHeading5() {
        setField(quoteModel, "quoteHeadlineType", "heading5");
        assertEquals("h5", quoteModel.getQuoteHeadlineTag());
    }

    @Test
    void testGetQuoteHeadlineTagWithHeading6() {
        setField(quoteModel, "quoteHeadlineType", "heading6");
        assertEquals("h6", quoteModel.getQuoteHeadlineTag());
    }

    @Test
    void testGetQuoteHeadlineTagWithNullType() {
        setField(quoteModel, "quoteHeadlineType", null);
        assertEquals("h2", quoteModel.getQuoteHeadlineTag());
    }

    @Test
    void testGetQuoteHeadlineTagWithInvalidType() {
        setField(quoteModel, "quoteHeadlineType", "text");
        assertEquals("h2", quoteModel.getQuoteHeadlineTag());
    }

    @Test
    void testNullValues() {
        assertNull(quoteModel.getHeadlineText());
        assertNull(quoteModel.getHeadlineType());
        assertNull(quoteModel.getShortDescription());
        assertNull(quoteModel.getCopySectionAlignment());
        assertNull(quoteModel.getCopySectionBgColor());
        assertNull(quoteModel.getImage());
        assertNull(quoteModel.getBgImageAlt());
        assertNull(quoteModel.getBgImageTopMargin());
        assertNull(quoteModel.getBgImageAlignmentLeft());
        assertNull(quoteModel.getBgImageAlignmentRight());
        assertNull(quoteModel.getBgImageJustification());
        assertNull(quoteModel.getQuote());
        assertNull(quoteModel.getQuoteHeadlineType());
        assertNull(quoteModel.getQuoteJustification());
        assertNull(quoteModel.getAuthor());
        assertNull(quoteModel.getMobileBgImageTopMargin());
        assertNull(quoteModel.getBrandIconImageBgColor());
        assertNull(quoteModel.getCopyAlignment());
    }
}
