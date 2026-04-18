package com.mvw.core.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.models.dto.Places;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ResortPlacesModelTest {

    private ResortPlacesModel resortPlacesModel;

    @Mock
    private SlingHttpServletRequest request;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resortPlacesModel = new ResortPlacesModel();
        setField(resortPlacesModel, "request", request);
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
    void testGetPropertiesListWithValidResponse() throws IOException {
        ObjectNode jsonResponse = createValidJsonResponse();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Places> result = resortPlacesModel.getPropertiesList();

        assertNotNull(result);
        // getPropertiesList() filters only parking places, "beach" doesn't contain "parking"
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesListWithParkingPlaces() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithParkingPlaces();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Places> result = resortPlacesModel.getPropertiesList();

        assertNotNull(result);
    }

    @Test
    void testGetPropertiesListWithEmptyNodes() throws IOException {
        ObjectNode jsonResponse = createEmptyJsonResponse();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Places> result = resortPlacesModel.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesListWithMultiplePlaces() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithMultipleParkingPlaces();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Places> result = resortPlacesModel.getPropertiesList();

        assertNotNull(result);
        // getPropertiesList() filters only parking places, so should have 2 parking places
        assertTrue(result.size() >= 2);
    }

    @Test
    void testGetParkingPlacesListWithParkingNodes() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithParkingPlaces();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Places> allPlaces = resortPlacesModel.getPropertiesList();
        List<Places> parkingPlaces = resortPlacesModel.getParkingPlacesList(allPlaces);

        assertNotNull(parkingPlaces);
        assertTrue(parkingPlaces.stream().allMatch(p -> 
            p.getNodename() != null && p.getNodename().toLowerCase().contains("parking")));
    }

    @Test
    void testGetParkingPlacesListWithNoParkingNodes() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithNoParkingPlaces();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Places> allPlaces = resortPlacesModel.getPropertiesList();
        List<Places> parkingPlaces = resortPlacesModel.getParkingPlacesList(allPlaces);

        assertNotNull(parkingPlaces);
        assertTrue(parkingPlaces.isEmpty());
    }

    @Test
    void testGetParkingPlacesListCaseInsensitive() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithMixedCaseParkingPlaces();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Places> allPlaces = resortPlacesModel.getPropertiesList();
        List<Places> parkingPlaces = resortPlacesModel.getParkingPlacesList(allPlaces);

        assertNotNull(parkingPlaces);
        assertEquals(3, parkingPlaces.size());
    }

    private ObjectNode createValidJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode places = jcr.putObject("places");
        ArrayNode nodes = places.putArray("nodes");
        
        ObjectNode place = nodes.addObject();
        place.put("nodename", "beach");
        place.put("name", "Beach");
        place.put("description", "Beautiful sandy beach");
        
        return root;
    }

    private ObjectNode createEmptyJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode places = jcr.putObject("places");
        places.putArray("nodes");
        
        return root;
    }

    private ObjectNode createJsonResponseWithParkingPlaces() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode places = jcr.putObject("places");
        ArrayNode nodes = places.putArray("nodes");
        
        ObjectNode parking = nodes.addObject();
        parking.put("nodename", "parking-lot");
        parking.put("name", "Guest Parking");
        
        ObjectNode beach = nodes.addObject();
        beach.put("nodename", "beach");
        beach.put("name", "Beach");
        
        return root;
    }

    private ObjectNode createJsonResponseWithNoParkingPlaces() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode places = jcr.putObject("places");
        ArrayNode nodes = places.putArray("nodes");
        
        ObjectNode beach = nodes.addObject();
        beach.put("nodename", "beach");
        beach.put("name", "Beach");
        
        ObjectNode pool = nodes.addObject();
        pool.put("nodename", "pool");
        pool.put("name", "Pool");
        
        return root;
    }

    private ObjectNode createJsonResponseWithMultiplePlaces() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode places = jcr.putObject("places");
        ArrayNode nodes = places.putArray("nodes");
        
        ObjectNode place1 = nodes.addObject();
        place1.put("nodename", "beach");
        place1.put("name", "Beach");
        
        ObjectNode place2 = nodes.addObject();
        place2.put("nodename", "pool");
        place2.put("name", "Pool");
        
        ObjectNode place3 = nodes.addObject();
        place3.put("nodename", "spa");
        place3.put("name", "Spa");
        
        return root;
    }

    private ObjectNode createJsonResponseWithMultipleParkingPlaces() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode places = jcr.putObject("places");
        ArrayNode nodes = places.putArray("nodes");
        
        ObjectNode parking1 = nodes.addObject();
        parking1.put("nodename", "parking-lot-1");
        parking1.put("name", "Main Parking Lot");
        
        ObjectNode parking2 = nodes.addObject();
        parking2.put("nodename", "parking-garage");
        parking2.put("name", "Parking Garage");
        
        ObjectNode beach = nodes.addObject();
        beach.put("nodename", "beach");
        beach.put("name", "Beach");
        
        return root;
    }

    private ObjectNode createJsonResponseWithMixedCaseParkingPlaces() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode places = jcr.putObject("places");
        ArrayNode nodes = places.putArray("nodes");
        
        ObjectNode parking1 = nodes.addObject();
        parking1.put("nodename", "parking-lot");
        parking1.put("name", "Parking Lot");
        
        ObjectNode parking2 = nodes.addObject();
        parking2.put("nodename", "PARKING-garage");
        parking2.put("name", "Parking Garage");
        
        ObjectNode parking3 = nodes.addObject();
        parking3.put("nodename", "Parking-Valet");
        parking3.put("name", "Valet Parking");
        
        ObjectNode beach = nodes.addObject();
        beach.put("nodename", "beach");
        beach.put("name", "Beach");
        
        return root;
    }
}
