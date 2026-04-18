package com.mvw.core.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.models.dto.DefaultModel;
import com.mvw.core.models.dto.Places;
import com.mvw.core.models.dto.ResortInfoItem;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ResortPolicyModelTest {

    private ResortPolicyModel resortPolicyModel;

    @Mock
    private SlingHttpServletRequest request;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resortPolicyModel = new ResortPolicyModel();
        setField(resortPolicyModel, "request", request);
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
    void testGetResortInfoItemsWithNullJahiaResponse() {
        when(request.getAttribute("jahiaResponse")).thenReturn(null);
        
        List<ResortInfoItem> result = resortPolicyModel.getResortInfoItems();
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetResortInfoItemsWithValidResponse() {
        ObjectNode jsonResponse = createValidJsonResponse();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);
        when(request.getAttribute("staticData")).thenReturn(createStaticDataResponse());
        
        List<ResortInfoItem> result = resortPolicyModel.getResortInfoItems();
        
        assertNotNull(result);
    }

    @Test
    void testGetResortInfoItemsWithParkingPlace() {
        ObjectNode jsonResponse = createJsonResponseWithParking();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);
        when(request.getAttribute("staticData")).thenReturn(createStaticDataResponse());
        
        List<ResortInfoItem> result = resortPolicyModel.getResortInfoItems();
        
        assertNotNull(result);
    }

    @Test
    void testGetResortInfoItemsWithException() {
        ObjectNode jsonResponse = mapper.createObjectNode();
        jsonResponse.put("invalid", "data");
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);
        
        List<ResortInfoItem> result = resortPolicyModel.getResortInfoItems();
        
        assertNotNull(result);
    }

    @Test
    void testGetServiceAnimalAndCashlessPoliciesWithNullList() {
        List<ResortInfoItem> result = resortPolicyModel.getServiceAnimalAndCashlessPolicies(null);
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetServiceAnimalAndCashlessPoliciesWithEmptyList() {
        List<ResortInfoItem> result = resortPolicyModel.getServiceAnimalAndCashlessPolicies(Collections.emptyList());
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetServiceAnimalAndCashlessPoliciesWithServiceAnimal() {
        ResortInfoItem serviceAnimalItem = new ResortInfoItem("Service Animal Policy", "Description");
        List<ResortInfoItem> items = Collections.singletonList(serviceAnimalItem);
        
        List<ResortInfoItem> result = resortPolicyModel.getServiceAnimalAndCashlessPolicies(items);
        
        assertNotNull(result);
    }

    @Test
    void testGetServiceAnimalAndCashlessPoliciesWithCashless() {
        ResortInfoItem cashlessItem = new ResortInfoItem("Cashless Resort", "Description");
        List<ResortInfoItem> items = Collections.singletonList(cashlessItem);
        
        List<ResortInfoItem> result = resortPolicyModel.getServiceAnimalAndCashlessPolicies(items);
        
        assertNotNull(result);
    }

    @Test
    void testGetIconPropertiesMapWithNullList() {
        Map<String, String> result = resortPolicyModel.getIconPropertiesMap(null);
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetIconPropertiesMapWithEmptyList() {
        Map<String, String> result = resortPolicyModel.getIconPropertiesMap(Collections.emptyList());
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetIconPropertiesMapWithValidIcons() {
        DefaultModel parkingIcon = new DefaultModel();
        setFieldOnDto(parkingIcon, "nodename", "parking-icon");
        setFieldOnDto(parkingIcon, "properties", "/content/dam/icons/parking.svg");
        
        DefaultModel smokingIcon = new DefaultModel();
        setFieldOnDto(smokingIcon, "nodename", "smoking-icon");
        setFieldOnDto(smokingIcon, "properties", "/content/dam/icons/smoking.svg");
        
        List<DefaultModel> models = Arrays.asList(parkingIcon, smokingIcon);
        
        Map<String, String> result = resortPolicyModel.getIconPropertiesMap(models);
        
        assertEquals(2, result.size());
        assertEquals("/content/dam/icons/parking.svg", result.get("parking-icon"));
    }

    @Test
    void testGetIconPropertiesMapWithNullNodename() {
        DefaultModel model = new DefaultModel();
        setFieldOnDto(model, "nodename", null);
        setFieldOnDto(model, "properties", "/content/dam/icons/test.svg");
        
        List<DefaultModel> models = Collections.singletonList(model);
        
        Map<String, String> result = resortPolicyModel.getIconPropertiesMap(models);
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetIconPropertiesMapWithUnknownIcon() {
        DefaultModel model = new DefaultModel();
        setFieldOnDto(model, "nodename", "unknown-icon");
        setFieldOnDto(model, "properties", "/content/dam/icons/unknown.svg");
        
        List<DefaultModel> models = Collections.singletonList(model);
        
        Map<String, String> result = resortPolicyModel.getIconPropertiesMap(models);
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetIconPropertiesMapWithServiceAnimalIcon() {
        DefaultModel model = new DefaultModel();
        setFieldOnDto(model, "nodename", "service-animal-icon");
        setFieldOnDto(model, "properties", "/content/dam/icons/service-animal.svg");
        
        List<DefaultModel> models = Collections.singletonList(model);
        
        Map<String, String> result = resortPolicyModel.getIconPropertiesMap(models);
        
        assertEquals(1, result.size());
    }

    @Test
    void testGetIconPropertiesMapWithNoResortFeesIcon() {
        DefaultModel model = new DefaultModel();
        setFieldOnDto(model, "nodename", "no-resort-fees-icon");
        setFieldOnDto(model, "properties", "/content/dam/icons/no-fees.svg");
        
        List<DefaultModel> models = Collections.singletonList(model);
        
        Map<String, String> result = resortPolicyModel.getIconPropertiesMap(models);
        
        assertEquals(1, result.size());
    }

    private ObjectNode createValidJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        
        ObjectNode places = jcr.putObject("places");
        ArrayNode placesNodes = places.putArray("nodes");
        
        ObjectNode defaultSmokingPolicy = jcr.putObject("defaultSmokingPolicy");
        ObjectNode smokingCopy = defaultSmokingPolicy.putObject("copy");
        smokingCopy.putObject("textFields");
        
        ObjectNode specificResortInfo = jcr.putObject("specificResortInfo");
        ObjectNode infoCopy = specificResortInfo.putObject("copy");
        infoCopy.putObject("textFields");
        
        return root;
    }

    private ObjectNode createJsonResponseWithParking() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        
        ObjectNode places = jcr.putObject("places");
        ArrayNode placesNodes = places.putArray("nodes");
        
        ObjectNode parkingPlace = placesNodes.addObject();
        parkingPlace.put("nodename", "resort-parking");
        parkingPlace.put("name", "Resort Parking");
        parkingPlace.put("description", "Parking available");
        
        ObjectNode defaultSmokingPolicy = jcr.putObject("defaultSmokingPolicy");
        ObjectNode smokingCopy = defaultSmokingPolicy.putObject("copy");
        smokingCopy.putObject("textFields");
        
        ObjectNode specificResortInfo = jcr.putObject("specificResortInfo");
        ObjectNode infoCopy = specificResortInfo.putObject("copy");
        infoCopy.putObject("textFields");
        
        return root;
    }

    private ObjectNode createStaticDataResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode staticContent = jcr.putObject("staticContent");
        ObjectNode textFields = staticContent.putObject("textFields");
        ArrayNode documents = textFields.putArray("documents");
        
        ObjectNode parkingIcon = documents.addObject();
        parkingIcon.put("nodename", "parking-icon");
        parkingIcon.put("properties", "/content/dam/icons/parking.svg");
        
        return root;
    }

    private void setFieldOnDto(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            // Try superclass
            try {
                java.lang.reflect.Field field = target.getClass().getSuperclass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
            } catch (Exception ex) {
                // Field might not exist
            }
        }
    }
}
