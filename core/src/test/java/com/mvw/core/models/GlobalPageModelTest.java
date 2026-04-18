package com.mvw.core.models;

import com.adobe.cq.wcm.core.components.models.Page;
import com.mvw.core.services.LocDataService;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class GlobalPageModelTest {

    private final AemContext context = new AemContext();

    @Mock
    private LocDataService locDataService;

    @Mock
    private Page delegatePage;

    @Mock
    private SlingHttpServletResponse response;

    @Mock
    private ModelFactory modelFactory;

    private GlobalPageModel globalPageModel;

    @BeforeEach
    void setUp() throws Exception {
        context.create().resource("/content/test/page", "jcr:primaryType", "cq:Page");
        context.currentResource("/content/test/page");

        globalPageModel = new GlobalPageModel();

        // Inject fields via reflection
        setField(globalPageModel, "request", context.request());
        setField(globalPageModel, "response", response);
        setField(globalPageModel, "locDataService", locDataService);
        setField(globalPageModel, "delegate", delegatePage);
        setField(globalPageModel, "modelFactory", modelFactory);
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void invokeInit(GlobalPageModel model) {
        try {
            Method initMethod = GlobalPageModel.class.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testInit_CallsLocDataService() {
        invokeInit(globalPageModel);
        verify(locDataService).processLocData(context.request(), response);
    }

    @Test
    void testDelegatedPageMethods() {
        when(delegatePage.getTitle()).thenReturn("Test Title");
        when(delegatePage.getDescription()).thenReturn("Test Description");
        when(delegatePage.getLanguage()).thenReturn("en");
        when(delegatePage.getKeywords()).thenReturn(new String[]{"key1", "key2"});
        when(delegatePage.getId()).thenReturn("page-id-123");
        when(delegatePage.getCanonicalLink()).thenReturn("https://example.com/page");
        when(delegatePage.getBrandSlug()).thenReturn("test-brand");

        assertEquals("Test Title", globalPageModel.getTitle());
        assertEquals("Test Description", globalPageModel.getDescription());
        assertEquals("en", globalPageModel.getLanguage());
        assertArrayEquals(new String[]{"key1", "key2"}, globalPageModel.getKeywords());
        assertEquals("page-id-123", globalPageModel.getId());
        assertEquals("https://example.com/page", globalPageModel.getCanonicalLink());
        assertEquals("test-brand", globalPageModel.getBrandSlug());
    }

    
   
    @Test
    void testGetExportedItemsOrder_Empty() {
        String[] order = globalPageModel.getExportedItemsOrder();
        assertNotNull(order);
        assertEquals(0, order.length);
    }

    @Test
    void testModelInstantiation() {
        assertNotNull(globalPageModel);
    }
}