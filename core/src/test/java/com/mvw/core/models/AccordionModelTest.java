package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccordionModelTest {

    private AccordionModel accordionModel;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Resource resource1;

    @Mock
    private Resource resource2;

    @Mock
    private ValueMap valueMap1;

    @Mock
    private ValueMap valueMap2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accordionModel = new AccordionModel();
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
    void testGetHeadlineType() {
        setField(accordionModel, "headlineType", "heading2");
        assertEquals("heading2", accordionModel.getHeadlineType());
    }

    @Test
    void testGetBackgroundColor() {
        setField(accordionModel, "backgroundColor", "white");
        assertEquals("white", accordionModel.getBackgroundColor());
    }

    @Test
    void testGetHeadlineText() {
        setField(accordionModel, "headlineText", "FAQ Section");
        assertEquals("FAQ Section", accordionModel.getHeadlineText());
    }

    @Test
    void testGetHeadlineStyledBorder() {
        setField(accordionModel, "headlineStyledBorder", "true");
        assertEquals("true", accordionModel.getHeadlineStyledBorder());
    }

    @Test
    void testGetGenerateFaqSchema() {
        setField(accordionModel, "generateFaqSchema", "true");
        assertEquals("true", accordionModel.getGenerateFaqSchema());
    }

    @Test
    void testGetHeadlineTagWithHeading1() {
        setField(accordionModel, "headlineType", "heading1");
        assertEquals("h1", accordionModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading2() {
        setField(accordionModel, "headlineType", "heading2");
        assertEquals("h2", accordionModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading3() {
        setField(accordionModel, "headlineType", "heading3");
        assertEquals("h3", accordionModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading4() {
        setField(accordionModel, "headlineType", "heading4");
        assertEquals("h4", accordionModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading5() {
        setField(accordionModel, "headlineType", "heading5");
        assertEquals("h5", accordionModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading6() {
        setField(accordionModel, "headlineType", "heading6");
        assertEquals("h6", accordionModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithNullType() {
        setField(accordionModel, "headlineType", null);
        assertEquals("h2", accordionModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithInvalidType() {
        setField(accordionModel, "headlineType", "paragraph");
        assertEquals("h2", accordionModel.getHeadlineTag());
    }

    @Test
    void testGetCtasWithNullCtaList() {
        setField(accordionModel, "ctaList", null);
        List<AccordionModel.CtaItem> result = accordionModel.getCtas();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetCtasWithEmptyCtaList() {
        setField(accordionModel, "ctaList", new ArrayList<>());
        List<AccordionModel.CtaItem> result = accordionModel.getCtas();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testInitWithNullCtas() {
        setField(accordionModel, "ctas", null);
        invokeInit();
        
        List<AccordionModel.CtaItem> result = accordionModel.getCtas();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testInitWithCtas() throws Exception {
        List<Resource> ctaResources = new ArrayList<>();

        // Mock resolver
        ResourceResolver mockResolver = mock(ResourceResolver.class);
        when(mockResolver.map(anyString()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        when(resource1.getValueMap()).thenReturn(valueMap1);
        when(valueMap1.get("ctaText", String.class)).thenReturn("Learn More");
        when(valueMap1.get("ctaDestination", String.class)).thenReturn("/page1");
        when(valueMap1.get("ctaTab", "sameTab")).thenReturn("newTab");
        when(valueMap1.get("ctaStyle", "tertiary")).thenReturn("primary");
        when(valueMap1.get("ctaPlacement", "none")).thenReturn("left");
        when(valueMap1.get("fontAwesomeIcon", String.class)).thenReturn("fa-arrow-right");

        ctaResources.add(resource1);
        setField(accordionModel, "ctas", ctaResources);

        invokeInit();

        List<AccordionModel.CtaItem> result = accordionModel.getCtas();
        assertNotNull(result);
        assertEquals(1, result.size());

        AccordionModel.CtaItem item = result.get(0);

        //  Inject resolver into CtaItem via reflection
        java.lang.reflect.Field resolverField =
            AccordionModel.CtaItem.class.getDeclaredField("resolver");
        resolverField.setAccessible(true);
        resolverField.set(item, mockResolver);

        assertEquals("Learn More", item.getCtaText());
        assertEquals("/page1", item.getCtaDestination());
        assertEquals("newTab", item.getCtaTab());
        assertEquals("primary", item.getCtaStyle());
        assertEquals("left", item.getCtaPlacement());
        assertEquals("fa-arrow-right", item.getFontAwesomeIcon());
    }

    @Test
    void testInitWithMultipleCtas() {
        List<Resource> ctaResources = new ArrayList<>();
        
        when(resource1.getValueMap()).thenReturn(valueMap1);
        when(valueMap1.get("ctaText", String.class)).thenReturn("CTA 1");
        when(valueMap1.get("ctaDestination", String.class)).thenReturn("/page1");
        when(valueMap1.get("ctaTab", "sameTab")).thenReturn("sameTab");
        when(valueMap1.get("ctaStyle", "tertiary")).thenReturn("tertiary");
        when(valueMap1.get("ctaPlacement", "none")).thenReturn("none");
        when(valueMap1.get("fontAwesomeIcon", String.class)).thenReturn(null);
        
        when(resource2.getValueMap()).thenReturn(valueMap2);
        when(valueMap2.get("ctaText", String.class)).thenReturn("CTA 2");
        when(valueMap2.get("ctaDestination", String.class)).thenReturn("/page2");
        when(valueMap2.get("ctaTab", "sameTab")).thenReturn("newTab");
        when(valueMap2.get("ctaStyle", "tertiary")).thenReturn("secondary");
        when(valueMap2.get("ctaPlacement", "none")).thenReturn("right");
        when(valueMap2.get("fontAwesomeIcon", String.class)).thenReturn("fa-phone");
        
        ctaResources.add(resource1);
        ctaResources.add(resource2);
        setField(accordionModel, "ctas", ctaResources);
        
        invokeInit();
        
        List<AccordionModel.CtaItem> result = accordionModel.getCtas();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testCtaItemWithNullResource() {
        AccordionModel.CtaItem ctaItem = new AccordionModel.CtaItem(null);
        assertNull(ctaItem.getCtaText());
        assertNull(ctaItem.getCtaDestination());
        assertNull(ctaItem.getCtaTab());
        assertNull(ctaItem.getCtaStyle());
        assertNull(ctaItem.getCtaPlacement());
        assertNull(ctaItem.getFontAwesomeIcon());
    }

    @Test
    void testCtaItemWithValidResource() throws Exception {
        when(resource1.getValueMap()).thenReturn(valueMap1);
        when(valueMap1.get("ctaText", String.class)).thenReturn("Click Here");
        when(valueMap1.get("ctaDestination", String.class)).thenReturn("/link");
        when(valueMap1.get("ctaTab", "sameTab")).thenReturn("sameTab");
        when(valueMap1.get("ctaStyle", "tertiary")).thenReturn("tertiary");
        when(valueMap1.get("ctaPlacement", "none")).thenReturn("none");
        when(valueMap1.get("fontAwesomeIcon", String.class)).thenReturn("fa-check");

        AccordionModel.CtaItem ctaItem = new AccordionModel.CtaItem(resource1);

        // Inject mock resolver
        ResourceResolver mockResolver = mock(ResourceResolver.class);
        when(mockResolver.map(anyString()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        java.lang.reflect.Field resolverField =
            AccordionModel.CtaItem.class.getDeclaredField("resolver");
        resolverField.setAccessible(true);
        resolverField.set(ctaItem, mockResolver);

        assertEquals("Click Here", ctaItem.getCtaText());
        assertEquals("/link", ctaItem.getCtaDestination());
        assertEquals("sameTab", ctaItem.getCtaTab());
        assertEquals("tertiary", ctaItem.getCtaStyle());
        assertEquals("none", ctaItem.getCtaPlacement());
        assertEquals("fa-check", ctaItem.getFontAwesomeIcon());
    }

    @Test
    void testNullValues() {
        assertNull(accordionModel.getHeadlineType());
        assertNull(accordionModel.getBackgroundColor());
        assertNull(accordionModel.getHeadlineText());
        assertNull(accordionModel.getHeadlineStyledBorder());
        assertNull(accordionModel.getGenerateFaqSchema());
    }

    private void invokeInit() {
        try {
            java.lang.reflect.Method initMethod = AccordionModel.class.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(accordionModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
