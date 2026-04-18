package com.mvw.core.models.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResortInfoMapperTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testMapHeaderAndHtmlWithValidData() throws Exception {
        String json = "[" +
            "{\"path\": \"/sites/vistana/contents/properties/KOHNLKO/welcome-kit/resort-information/trash-removal/headertext\", \"nodename\": \"headertext\", \"description\": \"Trash Removal\"}," +
            "{\"path\": \"/sites/vistana/contents/properties/KOHNLKO/welcome-kit/resort-information/trash-removal/htmlcontent\", \"nodename\": \"htmlcontent\", \"description\": \"Trash is collected daily.\"}" +
            "]";
        
        JsonNode node = objectMapper.readTree(json);
        List<ResortInfoItem> items = ResortInfoMapper.mapHeaderAndHtml(node);
        
        assertEquals(1, items.size());
        assertEquals("Trash Removal", items.get(0).getName());
        assertEquals("Trash is collected daily.", items.get(0).getDescription());
    }

    @Test
    void testMapHeaderAndHtmlWithMultipleItems() throws Exception {
        String json = "[" +
            "{\"path\": \"/resort-information/trash-removal/headertext\", \"nodename\": \"headertext\", \"description\": \"Trash Removal\"}," +
            "{\"path\": \"/resort-information/trash-removal/htmlcontent\", \"nodename\": \"htmlcontent\", \"description\": \"Trash is collected daily.\"}," +
            "{\"path\": \"/resort-information/pool-hours/headertext\", \"nodename\": \"headertext\", \"description\": \"Pool Hours\"}," +
            "{\"path\": \"/resort-information/pool-hours/htmlcontent\", \"nodename\": \"htmlcontent\", \"description\": \"8 AM - 10 PM\"}" +
            "]";
        
        JsonNode node = objectMapper.readTree(json);
        List<ResortInfoItem> items = ResortInfoMapper.mapHeaderAndHtml(node);
        
        assertEquals(2, items.size());
    }

    @Test
    void testMapHeaderAndHtmlWithNullNode() {
        List<ResortInfoItem> items = ResortInfoMapper.mapHeaderAndHtml(null);
        assertTrue(items.isEmpty());
    }

    @Test
    void testMapHeaderAndHtmlWithNonArrayNode() throws Exception {
        String json = "{\"key\": \"value\"}";
        JsonNode node = objectMapper.readTree(json);
        
        List<ResortInfoItem> items = ResortInfoMapper.mapHeaderAndHtml(node);
        assertTrue(items.isEmpty());
    }

    @Test
    void testMapHeaderAndHtmlWithHeaderOnlyNoContent() throws Exception {
        String json = "[" +
            "{\"path\": \"/resort-information/trash-removal/headertext\", \"nodename\": \"headertext\", \"description\": \"Trash Removal\"}" +
            "]";
        
        JsonNode node = objectMapper.readTree(json);
        List<ResortInfoItem> items = ResortInfoMapper.mapHeaderAndHtml(node);
        
        // No htmlcontent, so no items should be created
        assertTrue(items.isEmpty());
    }

    @Test
    void testMapHeaderAndHtmlWithContentOnlyNoHeader() throws Exception {
        String json = "[" +
            "{\"path\": \"/resort-information/trash-removal/htmlcontent\", \"nodename\": \"htmlcontent\", \"description\": \"Trash is collected daily.\"}" +
            "]";
        
        JsonNode node = objectMapper.readTree(json);
        List<ResortInfoItem> items = ResortInfoMapper.mapHeaderAndHtml(node);
        
        // No headertext, so no items should be created
        assertTrue(items.isEmpty());
    }

    @Test
    void testMapHeaderAndHtmlWithInvalidPath() throws Exception {
        String json = "[" +
            "{\"path\": \"/some/other/path/headertext\", \"nodename\": \"headertext\", \"description\": \"Header\"}" +
            "]";
        
        JsonNode node = objectMapper.readTree(json);
        List<ResortInfoItem> items = ResortInfoMapper.mapHeaderAndHtml(node);
        
        // Invalid path without /resort-information/ marker
        assertTrue(items.isEmpty());
    }

    @Test
    void testMapHeaderAndHtmlWithEmptyArray() throws Exception {
        String json = "[]";
        
        JsonNode node = objectMapper.readTree(json);
        List<ResortInfoItem> items = ResortInfoMapper.mapHeaderAndHtml(node);
        
        assertTrue(items.isEmpty());
    }

    @Test
    void testMapHeaderAndHtmlWithMixedValidAndInvalid() throws Exception {
        String json = "[" +
            "{\"path\": \"/resort-information/valid-item/headertext\", \"nodename\": \"headertext\", \"description\": \"Valid Header\"}," +
            "{\"path\": \"/resort-information/valid-item/htmlcontent\", \"nodename\": \"htmlcontent\", \"description\": \"Valid Content\"}," +
            "{\"path\": \"/invalid/path/headertext\", \"nodename\": \"headertext\", \"description\": \"Invalid\"}" +
            "]";
        
        JsonNode node = objectMapper.readTree(json);
        List<ResortInfoItem> items = ResortInfoMapper.mapHeaderAndHtml(node);
        
        assertEquals(1, items.size());
        assertEquals("Valid Header", items.get(0).getName());
    }
}
