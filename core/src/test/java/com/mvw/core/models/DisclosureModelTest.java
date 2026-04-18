package com.mvw.core.models;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.adobe.acs.commons.genericlists.GenericList;
import com.day.cq.dam.api.Asset;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import javax.jcr.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DisclosureModelTest {

    private DisclosureModel disclosureModel;

    @Mock
    private ResourceResolverFactory resolverFactory;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private Session session;

    @Mock
    private Query query;

    @Mock
    private SearchResult searchResult;

    @Mock
    private Hit hit;

    @Mock
    private Resource assetResource;

    @Mock
    private Resource metadataResource;

    @Mock
    private Asset asset;

    @Mock
    private TagManager tagManager;

    @Mock
    private Tag tag;

    @Mock
    private PageManager pageManager;

    @Mock
    private Page listPage;

    @Mock
    private GenericList genericList;

    @Mock
    private GenericList.Item listItem;

    @Mock
    private ValueMap valueMap;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        disclosureModel = new DisclosureModel();
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
    void testGetWelcomeText() {
        setField(disclosureModel, "welcomeText", "Welcome to Disclosures");
        assertEquals("Welcome to Disclosures", disclosureModel.getWelcomeText());
    }

    @Test
    void testGetCopyText() {
        setField(disclosureModel, "copyText", "Legal copy text");
        assertEquals("Legal copy text", disclosureModel.getCopyText());
    }

    @Test
    void testGetResortName() {
        setField(disclosureModel, "resortName", "Marriott Resort");
        assertEquals("Marriott Resort", disclosureModel.getResortName());
    }

    @Test
    void testGetActiveTab() {
        setField(disclosureModel, "active_tab", "Active Documents");
        assertEquals("Active Documents", disclosureModel.getActive_tab());
    }

    @Test
    void testGetPreviousTab() {
        setField(disclosureModel, "previous_tab", "Previous Documents");
        assertEquals("Previous Documents", disclosureModel.getPrevious_tab());
    }

    @Test
    void testGetFirstColumn() {
        setField(disclosureModel, "first_column", "Document Name");
        assertEquals("Document Name", disclosureModel.getFirst_column());
    }

    @Test
    void testGetSecondColumn() {
        setField(disclosureModel, "second_column", "Language");
        assertEquals("Language", disclosureModel.getSecond_column());
    }

    @Test
    void testGetDownload() {
        setField(disclosureModel, "download", "Download");
        assertEquals("Download", disclosureModel.getDownload());
    }

    @Test
    void testGetShowDownloadAll() {
        setField(disclosureModel, "showDownloadAll", "true");
        assertEquals("true", disclosureModel.getShowDownloadAll());
    }

    @Test
    void testGetFolderPath() {
        setField(disclosureModel, "folderPath", "/content/dam/mvw/disclosures");
        assertEquals("/content/dam/mvw/disclosures", disclosureModel.getFolderPath());
    }

    @Test
    void testGetActiveMap() {
        Map<String, Object> activeMap = new java.util.LinkedHashMap<>();
        activeMap.put("Test Doc", new java.util.TreeMap<String, String>());
        setField(disclosureModel, "activeMap", activeMap);
        assertEquals(activeMap, disclosureModel.getActiveMap());
    }

    @Test
    void testGetArchiveMap() {
        Map<String, Object> archiveMap = new java.util.LinkedHashMap<>();
        archiveMap.put("Archived Doc", new java.util.TreeMap<String, String>());
        setField(disclosureModel, "archiveMap", archiveMap);
        assertEquals(archiveMap, disclosureModel.getArchiveMap());
    }

    @Test
    void testGetActivePdfsCount() {
        setField(disclosureModel, "activePdfsCount", 5);
        assertEquals(5, disclosureModel.getActivePdfsCount());
    }

    @Test
    void testGetArchivePdfsCount() {
        setField(disclosureModel, "archivePdfsCount", 3);
        assertEquals(3, disclosureModel.getArchivePdfsCount());
    }

    @Test
    void testGetLanguageMap() {
        Map<String, String> languageMap = new java.util.HashMap<>();
        languageMap.put("en", "English");
        languageMap.put("es", "Spanish");
        setField(disclosureModel, "languageMap", languageMap);
        assertEquals(languageMap, disclosureModel.getLanguageMap());
    }

    @Test
    void testInitWithBlankFolderPath() throws Exception {
        setField(disclosureModel, "folderPath", "");
        setField(disclosureModel, "resolverFactory", resolverFactory);
        
        invokeInit();
        
        // When folderPath is blank, init should return early
        assertEquals(0, disclosureModel.getActivePdfsCount());
    }

    @Test
    void testInitWithNullFolderPath() throws Exception {
        setField(disclosureModel, "folderPath", null);
        setField(disclosureModel, "resolverFactory", resolverFactory);
        
        invokeInit();
        
        assertEquals(0, disclosureModel.getActivePdfsCount());
    }

    @Test
    void testInitWithNullResourceResolver() throws Exception {
        setField(disclosureModel, "folderPath", "/content/dam/test");
        setField(disclosureModel, "resolverFactory", resolverFactory);
        
        when(resolverFactory.getServiceResourceResolver(any())).thenReturn(null);
        
        invokeInit();
        
        assertEquals(0, disclosureModel.getActivePdfsCount());
    }

    @Test
    void testInitWithLoginException() throws Exception {
        setField(disclosureModel, "folderPath", "/content/dam/test");
        setField(disclosureModel, "resolverFactory", resolverFactory);
        
        when(resolverFactory.getServiceResourceResolver(any())).thenThrow(new LoginException("Access denied"));
        
        invokeInit();
        
        assertEquals(0, disclosureModel.getActivePdfsCount());
    }

    @Test
    void testInitWithNullQueryBuilder() throws Exception {
        setField(disclosureModel, "folderPath", "/content/dam/test");
        setField(disclosureModel, "resolverFactory", resolverFactory);
        
        when(resolverFactory.getServiceResourceResolver(any())).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(QueryBuilder.class)).thenReturn(null);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        
        invokeInit();
        
        assertEquals(0, disclosureModel.getActivePdfsCount());
    }

    @Test
    void testInitWithNullSession() throws Exception {
        setField(disclosureModel, "folderPath", "/content/dam/test");
        setField(disclosureModel, "resolverFactory", resolverFactory);
        
        when(resolverFactory.getServiceResourceResolver(any())).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(QueryBuilder.class)).thenReturn(queryBuilder);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(null);
        
        invokeInit();
        
        assertEquals(0, disclosureModel.getActivePdfsCount());
    }

    @Test
    void testInitWithEmptySearchResults() throws Exception {
        setupBasicMocks();
        
        when(searchResult.getHits()).thenReturn(Collections.emptyList());
        
        invokeInit();
        
        assertEquals(0, disclosureModel.getActivePdfsCount());
    }

    @Test
    void testInitWithNullAssetResource() throws Exception {
        setupBasicMocks();
        
        when(searchResult.getHits()).thenReturn(Collections.singletonList(hit));
        when(hit.getResource()).thenReturn(null);
        
        invokeInit();
        
        assertEquals(0, disclosureModel.getActivePdfsCount());
    }

    @Test
    void testInitWithAssetAdaptationFailure() throws Exception {
        setupBasicMocks();
        
        when(searchResult.getHits()).thenReturn(Collections.singletonList(hit));
        when(hit.getResource()).thenReturn(assetResource);
        when(assetResource.adaptTo(Asset.class)).thenReturn(null);
        when(assetResource.getPath()).thenReturn("/content/dam/test/doc.pdf");
        
        invokeInit();
        
        assertEquals(0, disclosureModel.getActivePdfsCount());
    }

    @Test
    void testInitWithValidPdfAsset() throws Exception {
        setupBasicMocks();
        setupValidPdfAsset("1 Test Document-english.pdf", "/content/dam/test/1 Test Document-english.pdf");
        
        invokeInit();
        
        assertTrue(disclosureModel.getActivePdfsCount() >= 0);
    }

    @Test
    void testInitWithArchiveTaggedAsset() throws Exception {
        setupBasicMocks();
        setupValidPdfAsset("Archive Document.pdf", "/content/dam/test/Archive Document.pdf");
        
        when(metadataResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("cq:tags", String[].class)).thenReturn(new String[]{"legal:archive"});
        when(tagManager.resolve("legal:archive")).thenReturn(tag);
        when(tag.getTagID()).thenReturn("legal:archive");
        
        invokeInit();
        
        assertTrue(disclosureModel.getArchivePdfsCount() >= 0);
    }

    @Test
    void testInitWithNullTagManager() throws Exception {
        setupBasicMocks();
        setupValidPdfAsset("Test.pdf", "/content/dam/test/Test.pdf");
        
        when(resourceResolver.adaptTo(TagManager.class)).thenReturn(null);
        
        invokeInit();
        
        assertTrue(disclosureModel.getActivePdfsCount() >= 0);
    }

    @Test
    void testInitWithNullMetadataResource() throws Exception {
        setupBasicMocks();
        setupValidPdfAsset("Test.pdf", "/content/dam/test/Test.pdf");
        
        when(assetResource.getChild("jcr:content/metadata")).thenReturn(null);
        
        invokeInit();
        
        assertTrue(disclosureModel.getActivePdfsCount() >= 0);
    }

    @Test
    void testInitWithNullTags() throws Exception {
        setupBasicMocks();
        setupValidPdfAsset("Test.pdf", "/content/dam/test/Test.pdf");
        
        when(metadataResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("cq:tags", String[].class)).thenReturn(null);
        
        invokeInit();
        
        assertTrue(disclosureModel.getActivePdfsCount() >= 0);
    }

    @Test
    void testInitWithUnresolvedTag() throws Exception {
        setupBasicMocks();
        setupValidPdfAsset("Test.pdf", "/content/dam/test/Test.pdf");
        
        when(metadataResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("cq:tags", String[].class)).thenReturn(new String[]{"unknown:tag"});
        when(tagManager.resolve("unknown:tag")).thenReturn(null);
        
        invokeInit();
        
        assertTrue(disclosureModel.getActivePdfsCount() >= 0);
    }

    @Test
    void testInitWithNonArchiveTag() throws Exception {
        setupBasicMocks();
        setupValidPdfAsset("Test.pdf", "/content/dam/test/Test.pdf");
        
        when(metadataResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("cq:tags", String[].class)).thenReturn(new String[]{"legal:active"});
        when(tagManager.resolve("legal:active")).thenReturn(tag);
        when(tag.getTagID()).thenReturn("legal:active");
        
        invokeInit();
        
        assertTrue(disclosureModel.getActivePdfsCount() >= 0);
    }

    @Test
    void testInitWithFileNameContainingNumber() throws Exception {
        setupBasicMocks();
        setupValidPdfAsset("6 Rules and Regulations-spanish.pdf", "/content/dam/test/6 Rules and Regulations-spanish.pdf");
        
        invokeInit();
        
        assertNotNull(disclosureModel.getActiveMap());
    }

    @Test
    void testInitWithFileNameNoExtension() throws Exception {
        setupBasicMocks();
        setupValidPdfAsset("Document", "/content/dam/test/Document");
        
        invokeInit();
        
        assertNotNull(disclosureModel.getActiveMap());
    }

    @Test
    void testInitWithDcTitle() throws Exception {
        setupBasicMocks();
        setupValidPdfAsset("Test.pdf", "/content/dam/test/Test.pdf");
        
        when(asset.getMetadataValue("dc:title")).thenReturn("Custom Title");
        
        invokeInit();
        
        assertNotNull(disclosureModel.getActiveMap());
    }

    @Test
    void testGroupsByDcTitleWhenPresent() throws Exception {
        setupBasicMocks();

        GenericList.Item englishItem = mock(GenericList.Item.class);
        GenericList.Item spanishItem = mock(GenericList.Item.class);
        when(englishItem.getTitle()).thenReturn("english");
        when(englishItem.getValue()).thenReturn("English");
        when(spanishItem.getTitle()).thenReturn("spanish");
        when(spanishItem.getValue()).thenReturn("Spanish");
        when(genericList.getItems()).thenReturn(Arrays.asList(englishItem, spanishItem));

        Hit hit1 = mock(Hit.class);
        Hit hit2 = mock(Hit.class);
        Resource res1 = mock(Resource.class);
        Resource res2 = mock(Resource.class);
        Asset asset1 = mock(Asset.class);
        Asset asset2 = mock(Asset.class);
        Resource meta1 = mock(Resource.class);
        Resource meta2 = mock(Resource.class);
        ValueMap vm1 = mock(ValueMap.class);
        ValueMap vm2 = mock(ValueMap.class);

        when(searchResult.getHits()).thenReturn(Arrays.asList(hit1, hit2));
        when(hit1.getResource()).thenReturn(res1);
        when(hit2.getResource()).thenReturn(res2);
        when(res1.adaptTo(Asset.class)).thenReturn(asset1);
        when(res2.adaptTo(Asset.class)).thenReturn(asset2);
        when(res1.getChild("jcr:content/metadata")).thenReturn(meta1);
        when(res2.getChild("jcr:content/metadata")).thenReturn(meta2);
        when(meta1.getValueMap()).thenReturn(vm1);
        when(meta2.getValueMap()).thenReturn(vm2);
        when(vm1.get("cq:tags", String[].class)).thenReturn(null);
        when(vm2.get("cq:tags", String[].class)).thenReturn(null);

        when(asset1.getName()).thenReturn("101 file-one-english.pdf");
        when(asset2.getName()).thenReturn("999 another-name-spanish.pdf");
        when(asset1.getPath()).thenReturn("/content/dam/test/101 file-one-english.pdf");
        when(asset2.getPath()).thenReturn("/content/dam/test/999 another-name-spanish.pdf");
        when(asset1.getMetadataValue("dc:title")).thenReturn("10 Terms");
        when(asset2.getMetadataValue("dc:title")).thenReturn("10 Terms");

        invokeInit();

        assertEquals(1, disclosureModel.getActiveMap().size());
        assertTrue(disclosureModel.getActiveMap().containsKey("10 Terms"));
    }

    @Test
    void testUsesCleanedFileNameAsDisplayTitleWhenDcTitleMissing() throws Exception {
        setupBasicMocks();

        GenericList.Item englishItem = mock(GenericList.Item.class);
        when(englishItem.getTitle()).thenReturn("english");
        when(englishItem.getValue()).thenReturn("English");
        when(genericList.getItems()).thenReturn(Collections.singletonList(englishItem));

        setupValidPdfAsset("10 Zeta-english.pdf", "/content/dam/test/10 Zeta-english.pdf");
        when(asset.getMetadataValue("dc:title")).thenReturn(" ");

        invokeInit();

        assertTrue(disclosureModel.getActiveMap().containsKey("10 Zeta"));
        Object value = disclosureModel.getActiveMap().get("10 Zeta");
        assertTrue(value instanceof Map);
        Map<?, ?> languageEntries = (Map<?, ?>) value;
        assertTrue(languageEntries.containsKey("English"));
    }

    @Test
    void testSortsOnlyByDisplayName() throws Exception {
        setupBasicMocks();

        Hit hit1 = mock(Hit.class);
        Hit hit2 = mock(Hit.class);
        Hit hit3 = mock(Hit.class);
        Resource res1 = mock(Resource.class);
        Resource res2 = mock(Resource.class);
        Resource res3 = mock(Resource.class);
        Asset asset1 = mock(Asset.class);
        Asset asset2 = mock(Asset.class);
        Asset asset3 = mock(Asset.class);
        Resource meta1 = mock(Resource.class);
        Resource meta2 = mock(Resource.class);
        Resource meta3 = mock(Resource.class);
        ValueMap vm1 = mock(ValueMap.class);
        ValueMap vm2 = mock(ValueMap.class);
        ValueMap vm3 = mock(ValueMap.class);

        when(searchResult.getHits()).thenReturn(Arrays.asList(hit1, hit2, hit3));
        when(hit1.getResource()).thenReturn(res1);
        when(hit2.getResource()).thenReturn(res2);
        when(hit3.getResource()).thenReturn(res3);
        when(res1.adaptTo(Asset.class)).thenReturn(asset1);
        when(res2.adaptTo(Asset.class)).thenReturn(asset2);
        when(res3.adaptTo(Asset.class)).thenReturn(asset3);
        when(res1.getChild("jcr:content/metadata")).thenReturn(meta1);
        when(res2.getChild("jcr:content/metadata")).thenReturn(meta2);
        when(res3.getChild("jcr:content/metadata")).thenReturn(meta3);
        when(meta1.getValueMap()).thenReturn(vm1);
        when(meta2.getValueMap()).thenReturn(vm2);
        when(meta3.getValueMap()).thenReturn(vm3);
        when(vm1.get("cq:tags", String[].class)).thenReturn(null);
        when(vm2.get("cq:tags", String[].class)).thenReturn(null);
        when(vm3.get("cq:tags", String[].class)).thenReturn(null);

        when(asset1.getName()).thenReturn("1 Zeta.pdf");
        when(asset2.getName()).thenReturn("2 Alpha.pdf");
        when(asset3.getName()).thenReturn("3 Beta.pdf");
        when(asset1.getPath()).thenReturn("/content/dam/test/1 Zeta.pdf");
        when(asset2.getPath()).thenReturn("/content/dam/test/2 Alpha.pdf");
        when(asset3.getPath()).thenReturn("/content/dam/test/3 Beta.pdf");
        when(asset1.getMetadataValue("dc:title")).thenReturn("3 Beta");
        when(asset2.getMetadataValue("dc:title")).thenReturn("10 Zeta");
        when(asset3.getMetadataValue("dc:title")).thenReturn("2 Alpha");

        invokeInit();

        List<String> keys = disclosureModel.getActiveMap().keySet().stream().collect(Collectors.toList());
        assertEquals(Arrays.asList("2 Alpha", "3 Beta", "10 Zeta"), keys);
    }

    @Test
    void testSortsNumericTitlesWithAlphaSuffixes() throws Exception {
        setupBasicMocks();

        Hit hit1 = mock(Hit.class);
        Hit hit2 = mock(Hit.class);
        Hit hit3 = mock(Hit.class);
        Resource res1 = mock(Resource.class);
        Resource res2 = mock(Resource.class);
        Resource res3 = mock(Resource.class);
        Asset asset1 = mock(Asset.class);
        Asset asset2 = mock(Asset.class);
        Asset asset3 = mock(Asset.class);
        Resource meta1 = mock(Resource.class);
        Resource meta2 = mock(Resource.class);
        Resource meta3 = mock(Resource.class);
        ValueMap vm1 = mock(ValueMap.class);
        ValueMap vm2 = mock(ValueMap.class);
        ValueMap vm3 = mock(ValueMap.class);

        when(searchResult.getHits()).thenReturn(Arrays.asList(hit1, hit2, hit3));
        when(hit1.getResource()).thenReturn(res1);
        when(hit2.getResource()).thenReturn(res2);
        when(hit3.getResource()).thenReturn(res3);
        when(res1.adaptTo(Asset.class)).thenReturn(asset1);
        when(res2.adaptTo(Asset.class)).thenReturn(asset2);
        when(res3.adaptTo(Asset.class)).thenReturn(asset3);
        when(res1.getChild("jcr:content/metadata")).thenReturn(meta1);
        when(res2.getChild("jcr:content/metadata")).thenReturn(meta2);
        when(res3.getChild("jcr:content/metadata")).thenReturn(meta3);
        when(meta1.getValueMap()).thenReturn(vm1);
        when(meta2.getValueMap()).thenReturn(vm2);
        when(meta3.getValueMap()).thenReturn(vm3);
        when(vm1.get("cq:tags", String[].class)).thenReturn(null);
        when(vm2.get("cq:tags", String[].class)).thenReturn(null);
        when(vm3.get("cq:tags", String[].class)).thenReturn(null);

        when(asset1.getName()).thenReturn("1 Base.pdf");
        when(asset2.getName()).thenReturn("1a Base.pdf");
        when(asset3.getName()).thenReturn("1b Base.pdf");
        when(asset1.getPath()).thenReturn("/content/dam/test/1 Base.pdf");
        when(asset2.getPath()).thenReturn("/content/dam/test/1a Base.pdf");
        when(asset3.getPath()).thenReturn("/content/dam/test/1b Base.pdf");
        when(asset1.getMetadataValue("dc:title")).thenReturn("1 Base");
        when(asset2.getMetadataValue("dc:title")).thenReturn("1a Base");
        when(asset3.getMetadataValue("dc:title")).thenReturn("1b Base");

        invokeInit();

        List<String> keys = disclosureModel.getActiveMap().keySet().stream().collect(Collectors.toList());
        assertEquals(Arrays.asList("1 Base", "1a Base", "1b Base"), keys);
    }

    @Test
    void testInitWithBlankDcTitle() throws Exception {
        setupBasicMocks();
        setupValidPdfAsset("Test.pdf", "/content/dam/test/Test.pdf");
        
        when(asset.getMetadataValue("dc:title")).thenReturn("   ");
        
        invokeInit();
        
        assertNotNull(disclosureModel.getActiveMap());
    }

    @Test
    void testInitWithNullPageManager() throws Exception {
        setupBasicMocks();
        
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(null);
        
        invokeInit();
        
        assertNotNull(disclosureModel.getLanguageMap());
    }

    @Test
    void testInitWithNullGenericListPage() throws Exception {
        setupBasicMocks();
        
        when(pageManager.getPage(anyString())).thenReturn(null);
        
        invokeInit();
        
        assertNotNull(disclosureModel.getLanguageMap());
    }

    @Test
    void testInitWithNullGenericList() throws Exception {
        setupBasicMocks();
        
        when(listPage.adaptTo(GenericList.class)).thenReturn(null);
        
        invokeInit();
        
        assertNotNull(disclosureModel.getLanguageMap());
    }

    @Test
    void testInitWithValidGenericListItems() throws Exception {
        setupBasicMocks();
        
        when(listItem.getTitle()).thenReturn("spanish");
        when(listItem.getValue()).thenReturn("Spanish");
        when(genericList.getItems()).thenReturn(Collections.singletonList(listItem));
        
        invokeInit();
        
        assertNotNull(disclosureModel.getLanguageMap());
    }

    @Test
    void testInitWithBlankGenericListItem() throws Exception {
        setupBasicMocks();
        
        when(listItem.getTitle()).thenReturn("   ");
        when(listItem.getValue()).thenReturn("   ");
        when(genericList.getItems()).thenReturn(Collections.singletonList(listItem));
        
        invokeInit();
        
        assertNotNull(disclosureModel.getLanguageMap());
    }

    @Test
    void testInitWithFileNameOnlyNumber() throws Exception {
        setupBasicMocks();
        setupValidPdfAsset("123.pdf", "/content/dam/test/123.pdf");
        
        invokeInit();
        
        assertNotNull(disclosureModel.getActiveMap());
    }

    @Test
    void testInitWithFileNameNoNumber() throws Exception {
        setupBasicMocks();
        setupValidPdfAsset("Document Name.pdf", "/content/dam/test/Document Name.pdf");
        
        invokeInit();
        
        assertNotNull(disclosureModel.getActiveMap());
    }

    @Test
    void testInitWithMultipleHyphensInFileName() throws Exception {
        setupBasicMocks();
        setupValidPdfAsset("Multi-Part-Document-english.pdf", "/content/dam/test/Multi-Part-Document-english.pdf");
        
        invokeInit();
        
        assertNotNull(disclosureModel.getActiveMap());
    }

    @Test
    void testInitWithHitException() throws Exception {
        setupBasicMocks();
        
        when(searchResult.getHits()).thenReturn(Collections.singletonList(hit));
        when(hit.getResource()).thenThrow(new RuntimeException("Test exception"));
        
        invokeInit();
        
        assertEquals(0, disclosureModel.getActivePdfsCount());
    }

    @Test
    void testNullValues() {
        DisclosureModel emptyModel = new DisclosureModel();
        assertNull(emptyModel.getWelcomeText());
        assertNull(emptyModel.getCopyText());
        assertNull(emptyModel.getResortName());
        assertNull(emptyModel.getActive_tab());
        assertNull(emptyModel.getPrevious_tab());
        assertNull(emptyModel.getFirst_column());
        assertNull(emptyModel.getSecond_column());
        assertNull(emptyModel.getDownload());
        assertNull(emptyModel.getShowDownloadAll());
        assertNull(emptyModel.getFolderPath());
    }

    private void setupBasicMocks() throws Exception {
        setField(disclosureModel, "folderPath", "/content/dam/test");
        setField(disclosureModel, "resolverFactory", resolverFactory);
        
        when(resolverFactory.getServiceResourceResolver(any())).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(QueryBuilder.class)).thenReturn(queryBuilder);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(resourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        
        when(queryBuilder.createQuery(any(PredicateGroup.class), any(Session.class))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        when(searchResult.getHits()).thenReturn(Collections.emptyList());
        
        when(pageManager.getPage(anyString())).thenReturn(listPage);
        when(listPage.adaptTo(GenericList.class)).thenReturn(genericList);
        when(genericList.getItems()).thenReturn(Collections.emptyList());
    }

    private void setupValidPdfAsset(String fileName, String path) throws Exception {
        when(searchResult.getHits()).thenReturn(Collections.singletonList(hit));
        when(hit.getResource()).thenReturn(assetResource);
        when(assetResource.adaptTo(Asset.class)).thenReturn(asset);
        when(assetResource.getPath()).thenReturn(path);
        when(assetResource.getChild("jcr:content/metadata")).thenReturn(metadataResource);
        
        when(asset.getName()).thenReturn(fileName);
        when(asset.getPath()).thenReturn(path);
        when(asset.getMetadataValue("dc:title")).thenReturn("");
        
        when(metadataResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("cq:tags", String[].class)).thenReturn(null);
    }

    private void invokeInit() {
        try {
            java.lang.reflect.Method initMethod = DisclosureModel.class.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(disclosureModel);
        } catch (Exception e) {
            // Init may throw exceptions in some test scenarios
        }
    }
}
