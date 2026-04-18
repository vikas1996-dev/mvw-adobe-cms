package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LegalHeroBannerTest {

    private LegalHeroBanner legalHeroBanner;

    @BeforeEach
    void setUp() {
        legalHeroBanner = new LegalHeroBanner();
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
    void testGetBannerImageFileReference() {
        setField(legalHeroBanner, "bannerImageFileReference", "/content/dam/image.jpg");
        assertEquals("/content/dam/image.jpg", legalHeroBanner.getBannerImageFileReference());
    }

    @Test
    void testGetCopyText() {
        setField(legalHeroBanner, "copyText", "<p>Test copy</p>");
        assertEquals("<p>Test copy</p>", legalHeroBanner.getCopyText());
    }

    @Test
    void testGetDecorationImage() {
        setField(legalHeroBanner, "decorationImage", "/content/dam/decoration.png");
        assertEquals("/content/dam/decoration.png", legalHeroBanner.getDecorationImage());
    }

    @Test
    void testGetPlainCopyTextNull() {
        setField(legalHeroBanner, "copyText", null);
        assertEquals("", legalHeroBanner.getPlainCopyText());
    }

    @Test
    void testGetPlainCopyTextBlank() {
        setField(legalHeroBanner, "copyText", "   ");
        assertEquals("", legalHeroBanner.getPlainCopyText());
    }

    @Test
    void testGetPlainCopyTextEmpty() {
        setField(legalHeroBanner, "copyText", "");
        assertEquals("", legalHeroBanner.getPlainCopyText());
    }

    @Test
    void testGetPlainCopyTextWithBrTag() {
        setField(legalHeroBanner, "copyText", "Line 1<br>Line 2<br/>Line 3<BR />Line 4");
        String result = legalHeroBanner.getPlainCopyText();
        assertTrue(result.contains("Line 1"));
        assertTrue(result.contains("Line 2"));
        assertTrue(result.contains("Line 3"));
        assertTrue(result.contains("Line 4"));
    }

    @Test
    void testGetPlainCopyTextWithPTag() {
        setField(legalHeroBanner, "copyText", "<p>Paragraph 1</p><p>Paragraph 2</p>");
        String result = legalHeroBanner.getPlainCopyText();
        assertTrue(result.contains("Paragraph 1"));
        assertTrue(result.contains("Paragraph 2"));
    }

    @Test
    void testGetPlainCopyTextWithDivTag() {
        setField(legalHeroBanner, "copyText", "<div>Div 1</div><div>Div 2</div>");
        String result = legalHeroBanner.getPlainCopyText();
        assertTrue(result.contains("Div 1"));
        assertTrue(result.contains("Div 2"));
    }

    @Test
    void testGetPlainCopyTextWithLiTag() {
        setField(legalHeroBanner, "copyText", "<ul><li>Item 1</li><li>Item 2</li></ul>");
        String result = legalHeroBanner.getPlainCopyText();
        assertTrue(result.contains("Item 1"));
        assertTrue(result.contains("Item 2"));
    }

    @Test
    void testGetPlainCopyTextWithHeadingTags() {
        setField(legalHeroBanner, "copyText", "<h1>Heading 1</h1><h2>Heading 2</h2><h3>Heading 3</h3>");
        String result = legalHeroBanner.getPlainCopyText();
        assertTrue(result.contains("Heading 1"));
        assertTrue(result.contains("Heading 2"));
        assertTrue(result.contains("Heading 3"));
    }

    @Test
    void testGetPlainCopyTextRemovesOtherTags() {
        setField(legalHeroBanner, "copyText", "<span>Text</span><a href='link'>Link</a><strong>Bold</strong>");
        String result = legalHeroBanner.getPlainCopyText();
        assertTrue(result.contains("Text"));
        assertTrue(result.contains("Link"));
        assertTrue(result.contains("Bold"));
        assertFalse(result.contains("<span>"));
        assertFalse(result.contains("<a"));
        assertFalse(result.contains("<strong>"));
    }

    @Test
    void testGetPlainCopyTextDecodesHtmlEntities() {
        setField(legalHeroBanner, "copyText", "Hello&nbsp;World&amp;Test&lt;Value&gt;Quote&quot;Apostrophe&#39;");
        String result = legalHeroBanner.getPlainCopyText();
        assertTrue(result.contains("Hello World"));
        assertTrue(result.contains("&Test"));
        assertTrue(result.contains("<Value>"));
        assertTrue(result.contains("Quote\""));
        assertTrue(result.contains("Apostrophe'"));
    }

    @Test
    void testGetPlainCopyTextNormalizesWhitespace() {
        setField(legalHeroBanner, "copyText", "Text\t\t\twith\f\ftabs\r\rand\r\nwhitespace");
        String result = legalHeroBanner.getPlainCopyText();
        assertFalse(result.contains("\t"));
        assertFalse(result.contains("\f"));
    }

    @Test
    void testGetPlainCopyTextCollapsesExcessiveNewlines() {
        setField(legalHeroBanner, "copyText", "Line 1\n\n\n\n\nLine 2");
        String result = legalHeroBanner.getPlainCopyText();
        // Should collapse to at most 2 consecutive newlines
        assertFalse(result.contains("\n\n\n"));
    }

    @Test
    void testGetPlainCopyTextTrimsResult() {
        setField(legalHeroBanner, "copyText", "   Trimmed text   ");
        String result = legalHeroBanner.getPlainCopyText();
        assertEquals("Trimmed text", result);
    }

    @Test
    void testGetPlainCopyTextComplexHtml() {
        setField(legalHeroBanner, "copyText", 
            "<div><h1>Title</h1><p>Paragraph with <strong>bold</strong> and <em>italic</em>.</p>" +
            "<ul><li>Item&nbsp;1</li><li>Item 2</li></ul></div>");
        String result = legalHeroBanner.getPlainCopyText();
        assertTrue(result.contains("Title"));
        assertTrue(result.contains("Paragraph"));
        assertTrue(result.contains("bold"));
        assertTrue(result.contains("italic"));
        assertTrue(result.contains("Item 1"));
        assertTrue(result.contains("Item 2"));
    }

    @Test
    void testNullValues() {
        assertNull(legalHeroBanner.getBannerImageFileReference());
        assertNull(legalHeroBanner.getCopyText());
        assertNull(legalHeroBanner.getDecorationImage());
    }
}
