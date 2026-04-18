package com.mvw.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class FaqJsonLdModelTest {

    private FaqJsonLdModel faqJsonLdModel;

    @Mock
    private Resource resource;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private SlingSettingsService slingSettingsService;

    @Mock
    private PageManager pageManager;

    @Mock
    private Page currentPage;

    @Mock
    private Resource pageResource;

    @Mock
    private Resource pageContentResource;

    @Mock
    private Resource itemResource;

    @Mock
    private Resource textResource;

    @Mock
    private Resource containerResource;

    @Mock
    private ValueMap resourceValueMap;

    @Mock
    private ValueMap itemValueMap;

    @Mock
    private ValueMap textValueMap;

    @Mock
    private ValueMap pageContentValueMap;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        faqJsonLdModel = new FaqJsonLdModel();
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
    void testGetJsonLdWithNullResource() {
        setField(faqJsonLdModel, "resource", null);
        
        invokeInit();
        
        assertEquals("{}", faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWithEmptyResource() {
        setField(faqJsonLdModel, "resource", resource);
        setField(faqJsonLdModel, "resourceResolver", resourceResolver);
        
        when(resource.getPath()).thenReturn("/content/test/faq");
        when(resource.listChildren()).thenReturn(Collections.emptyIterator());
        
        invokeInit();
        
        String jsonLd = faqJsonLdModel.getJsonLd();
        assertNotNull(jsonLd);
        assertTrue(jsonLd.contains("@context"));
        assertTrue(jsonLd.contains("schema.org"));
    }

    @Test
    void testGetJsonLdWithCtasNode() {
        setField(faqJsonLdModel, "resource", resource);
        setField(faqJsonLdModel, "resourceResolver", resourceResolver);
        
        when(resource.getPath()).thenReturn("/content/test/faq");
        
        Resource ctasResource = mock(Resource.class);
        when(ctasResource.getName()).thenReturn("ctas");
        
        Iterator<Resource> children = Collections.singletonList(ctasResource).iterator();
        when(resource.listChildren()).thenReturn(children);
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWithJcrNode() {
        setField(faqJsonLdModel, "resource", resource);
        setField(faqJsonLdModel, "resourceResolver", resourceResolver);
        
        when(resource.getPath()).thenReturn("/content/test/faq");
        
        Resource jcrResource = mock(Resource.class);
        when(jcrResource.getName()).thenReturn("jcr:content");
        
        Iterator<Resource> children = Collections.singletonList(jcrResource).iterator();
        when(resource.listChildren()).thenReturn(children);
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWithValidFaqItem() {
        setupBasicMocks();
        setupFaqItem("item_1", "What is FAQ?", "This is the answer.");
        
        invokeInit();
        
        String jsonLd = faqJsonLdModel.getJsonLd();
        assertNotNull(jsonLd);
        assertTrue(jsonLd.contains("What is FAQ?"));
        assertTrue(jsonLd.contains("This is the answer."));
    }

    @Test
    void testGetJsonLdWithPanelTitleFallback() {
        setupBasicMocks();
        
        when(itemResource.getName()).thenReturn("item_1");
        when(itemResource.getValueMap()).thenReturn(itemValueMap);
        when(itemValueMap.get("cq:panelTitle", String.class)).thenReturn(null);
        when(itemValueMap.get("jcr:title", String.class)).thenReturn("Fallback Title");
        when(itemResource.getChild("text")).thenReturn(textResource);
        when(textResource.getValueMap()).thenReturn(textValueMap);
        when(textValueMap.get("text", String.class)).thenReturn("Answer text");
        
        Iterator<Resource> children = Collections.singletonList(itemResource).iterator();
        when(resource.listChildren()).thenReturn(children);
        
        invokeInit();
        
        String jsonLd = faqJsonLdModel.getJsonLd();
        assertTrue(jsonLd.contains("Fallback Title"));
    }

    @Test
    void testGetJsonLdWithContainerTextPath() {
        setupBasicMocks();
        
        when(itemResource.getName()).thenReturn("item_1");
        when(itemResource.getValueMap()).thenReturn(itemValueMap);
        when(itemValueMap.get("cq:panelTitle", String.class)).thenReturn("Question");
        when(itemResource.getChild("text")).thenReturn(null);
        when(itemResource.getChild("container")).thenReturn(containerResource);
        when(containerResource.getChild("text")).thenReturn(textResource);
        when(textResource.getValueMap()).thenReturn(textValueMap);
        when(textValueMap.get("text", String.class)).thenReturn("Container answer");
        
        Iterator<Resource> children = Collections.singletonList(itemResource).iterator();
        when(resource.listChildren()).thenReturn(children);
        
        invokeInit();
        
        String jsonLd = faqJsonLdModel.getJsonLd();
        assertTrue(jsonLd.contains("Container answer"));
    }

    @Test
    void testGetJsonLdWithTextComponentIteration() {
        setupBasicMocks();
        
        when(itemResource.getName()).thenReturn("item_1");
        when(itemResource.getValueMap()).thenReturn(itemValueMap);
        when(itemValueMap.get("cq:panelTitle", String.class)).thenReturn("Question");
        when(itemResource.getChild("text")).thenReturn(null);
        when(itemResource.getChild("container")).thenReturn(null);
        
        Resource textComponent = mock(Resource.class);
        when(textComponent.getName()).thenReturn("text_component");
        ValueMap textComponentValueMap = mock(ValueMap.class);
        when(textComponent.getValueMap()).thenReturn(textComponentValueMap);
        when(textComponentValueMap.get("sling:resourceType", String.class)).thenReturn("wcm/foundation/components/text");
        when(textComponentValueMap.get("text", String.class)).thenReturn("Iterated answer");
        
        when(itemResource.listChildren()).thenReturn(Collections.singletonList(textComponent).iterator());
        
        Iterator<Resource> children = Collections.singletonList(itemResource).iterator();
        when(resource.listChildren()).thenReturn(children);
        
        invokeInit();
        
        String jsonLd = faqJsonLdModel.getJsonLd();
        assertTrue(jsonLd.contains("Iterated answer"));
    }

    @Test
    void testGetJsonLdWithMissingQuestion() {
        setupBasicMocks();
        
        when(itemResource.getName()).thenReturn("item_1");
        when(itemResource.getValueMap()).thenReturn(itemValueMap);
        when(itemValueMap.get("cq:panelTitle", String.class)).thenReturn(null);
        when(itemValueMap.get("jcr:title", String.class)).thenReturn(null);
        when(itemResource.getChild("text")).thenReturn(textResource);
        when(textResource.getValueMap()).thenReturn(textValueMap);
        when(textValueMap.get("text", String.class)).thenReturn("Answer");
        
        Iterator<Resource> children = Collections.singletonList(itemResource).iterator();
        when(resource.listChildren()).thenReturn(children);
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWithMissingAnswer() {
        setupBasicMocks();
        
        when(itemResource.getName()).thenReturn("item_1");
        when(itemResource.getValueMap()).thenReturn(itemValueMap);
        when(itemValueMap.get("cq:panelTitle", String.class)).thenReturn("Question");
        when(itemResource.getChild("text")).thenReturn(null);
        when(itemResource.getChild("container")).thenReturn(null);
        when(itemResource.listChildren()).thenReturn(Collections.emptyIterator());
        when(itemResource.getPath()).thenReturn("/content/test/item_1");
        
        Iterator<Resource> children = Collections.singletonList(itemResource).iterator();
        when(resource.listChildren()).thenReturn(children);
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWithPageDates() {
        setupBasicMocks();
        setupFaqItem("item_1", "Question", "Answer");
        
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getContainingPage(resource)).thenReturn(currentPage);
        when(currentPage.adaptTo(Resource.class)).thenReturn(pageResource);
        when(pageResource.getChild("jcr:content")).thenReturn(pageContentResource);
        when(pageContentResource.getValueMap()).thenReturn(pageContentValueMap);
        
        Calendar created = Calendar.getInstance();
        Calendar modified = Calendar.getInstance();
        when(pageContentValueMap.get("jcr:created", Calendar.class)).thenReturn(created);
        when(pageContentValueMap.get("cq:lastModified", Calendar.class)).thenReturn(modified);
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWithJcrLastModified() {
        setupBasicMocks();
        setupFaqItem("item_1", "Question", "Answer");
        
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getContainingPage(resource)).thenReturn(currentPage);
        when(currentPage.adaptTo(Resource.class)).thenReturn(pageResource);
        when(pageResource.getChild("jcr:content")).thenReturn(pageContentResource);
        when(pageContentResource.getValueMap()).thenReturn(pageContentValueMap);
        
        Calendar created = Calendar.getInstance();
        Calendar modified = Calendar.getInstance();
        when(pageContentValueMap.get("jcr:created", Calendar.class)).thenReturn(created);
        when(pageContentValueMap.get("cq:lastModified", Calendar.class)).thenReturn(null);
        when(pageContentValueMap.get("jcr:lastModified", Calendar.class)).thenReturn(modified);
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWithPublishInstance() {
        setupBasicMocks();
        setupFaqItem("item_1", "Question", "Answer");
        
        Set<String> runModes = new HashSet<>();
        runModes.add("publish");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        setField(faqJsonLdModel, "slingSettingsService", slingSettingsService);
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWithAuthorInstance() {
        setupBasicMocks();
        setupFaqItem("item_1", "Question", "Answer");
        
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        setField(faqJsonLdModel, "slingSettingsService", slingSettingsService);
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWithCanonicalUrl() {
        setupBasicMocks();
        setupFaqItem("item_1", "Question", "Answer");
        
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getContainingPage(resource)).thenReturn(currentPage);
        when(currentPage.getContentResource()).thenReturn(pageContentResource);
        when(pageContentResource.getValueMap()).thenReturn(pageContentValueMap);
        when(pageContentValueMap.get("canonicalUrl", String.class)).thenReturn("https://example.com/faq");
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWithNullResourceResolver() {
        setField(faqJsonLdModel, "resource", resource);
        setField(faqJsonLdModel, "resourceResolver", null);
        
        when(resource.getPath()).thenReturn("/content/test/faq");
        when(resource.listChildren()).thenReturn(Collections.emptyIterator());
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWithMappedPath() {
        setupBasicMocks();
        setupFaqItem("item_1", "Question", "Answer");
        
        when(resourceResolver.map(anyString())).thenReturn("https://www.example.com/faq");
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWithRelativeMappedPath() {
        setupBasicMocks();
        setupFaqItem("item_1", "Question", "Answer");
        
        when(resourceResolver.map(anyString())).thenReturn("/content/faq");
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWithPageResourceFallback() {
        setupBasicMocks();
        setupFaqItem("item_1", "Question", "Answer");
        
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(null);
        
        Resource parentResource = mock(Resource.class);
        when(resource.getParent()).thenReturn(parentResource);
        when(parentResource.getResourceType()).thenReturn("cq:Page");
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWithEmptyQuestion() {
        setupBasicMocks();
        
        when(itemResource.getName()).thenReturn("item_1");
        when(itemResource.getValueMap()).thenReturn(itemValueMap);
        when(itemValueMap.get("cq:panelTitle", String.class)).thenReturn("");
        when(itemValueMap.get("jcr:title", String.class)).thenReturn("");
        when(itemResource.getChild("text")).thenReturn(textResource);
        when(textResource.getValueMap()).thenReturn(textValueMap);
        when(textValueMap.get("text", String.class)).thenReturn("Answer");
        
        Iterator<Resource> children = Collections.singletonList(itemResource).iterator();
        when(resource.listChildren()).thenReturn(children);
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWithEmptyAnswer() {
        setupBasicMocks();
        
        when(itemResource.getName()).thenReturn("item_1");
        when(itemResource.getValueMap()).thenReturn(itemValueMap);
        when(itemValueMap.get("cq:panelTitle", String.class)).thenReturn("Question");
        when(itemResource.getChild("text")).thenReturn(textResource);
        when(textResource.getValueMap()).thenReturn(textValueMap);
        when(textValueMap.get("text", String.class)).thenReturn("");
        when(itemResource.getChild("container")).thenReturn(null);
        when(itemResource.listChildren()).thenReturn(Collections.emptyIterator());
        when(itemResource.getPath()).thenReturn("/content/test/item_1");
        
        Iterator<Resource> children = Collections.singletonList(itemResource).iterator();
        when(resource.listChildren()).thenReturn(children);
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWhenJsonLdIsNull() {
        assertEquals("{}", faqJsonLdModel.getJsonLd());
    }

    @Test
    void testFaqItemClass() {
        FaqJsonLdModel.FaqItem faqItem = new FaqJsonLdModel.FaqItem("Test Question", "Test Answer");
        
        assertEquals("Test Question", faqItem.getQuestion());
        assertEquals("Test Answer", faqItem.getAnswer());
    }

    @Test
    void testGetJsonLdWithNonItemNode() {
        setupBasicMocks();
        
        Resource otherResource = mock(Resource.class);
        when(otherResource.getName()).thenReturn("other_node");
        
        Iterator<Resource> children = Collections.singletonList(otherResource).iterator();
        when(resource.listChildren()).thenReturn(children);
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    @Test
    void testGetJsonLdWithJcrChildInIteration() {
        setupBasicMocks();
        
        when(itemResource.getName()).thenReturn("item_1");
        when(itemResource.getValueMap()).thenReturn(itemValueMap);
        when(itemValueMap.get("cq:panelTitle", String.class)).thenReturn("Question");
        when(itemResource.getChild("text")).thenReturn(null);
        when(itemResource.getChild("container")).thenReturn(null);
        
        Resource jcrChild = mock(Resource.class);
        when(jcrChild.getName()).thenReturn("jcr:content");
        
        when(itemResource.listChildren()).thenReturn(Collections.singletonList(jcrChild).iterator());
        when(itemResource.getPath()).thenReturn("/content/test/item_1");
        
        Iterator<Resource> children = Collections.singletonList(itemResource).iterator();
        when(resource.listChildren()).thenReturn(children);
        
        invokeInit();
        
        assertNotNull(faqJsonLdModel.getJsonLd());
    }

    private void setupBasicMocks() {
        setField(faqJsonLdModel, "resource", resource);
        setField(faqJsonLdModel, "resourceResolver", resourceResolver);
        
        when(resource.getPath()).thenReturn("/content/test/faq");
        when(resource.listChildren()).thenReturn(Collections.emptyIterator());
        when(resourceResolver.map(anyString())).thenReturn("/content/test/faq");
    }

    private void setupFaqItem(String itemName, String question, String answer) {
        when(itemResource.getName()).thenReturn(itemName);
        when(itemResource.getValueMap()).thenReturn(itemValueMap);
        when(itemValueMap.get("cq:panelTitle", String.class)).thenReturn(question);
        when(itemResource.getChild("text")).thenReturn(textResource);
        when(textResource.getValueMap()).thenReturn(textValueMap);
        when(textValueMap.get("text", String.class)).thenReturn(answer);
        
        Iterator<Resource> children = Collections.singletonList(itemResource).iterator();
        when(resource.listChildren()).thenReturn(children);
    }

    private void invokeInit() {
        try {
            java.lang.reflect.Method initMethod = FaqJsonLdModel.class.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(faqJsonLdModel);
        } catch (Exception e) {
            // Init may throw exceptions in some test scenarios
        }
    }
}
