package com.mvw.core.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.DestinationLandingDto;
import com.mvw.core.services.TripAdvisorEnrichmentService;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;
import com.mvw.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class DestinationDetailsTest {

    private final AemContext context = AppAemContext.newAemContext();
    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    @Mock
    private TripAdvisorEnrichmentService tripAdvisorEnrichmentService;

    @BeforeEach
    void setUp() throws Exception {
        when(jahiaApiConfigServiceImpl.getApiImagePath()).thenReturn("");
        when(tripAdvisorEnrichmentService.enrichWithTripAdvisorParallel(any(), anyList())).thenAnswer(inv -> inv.getArgument(1));
        context.registerService(JahiaApiConfigServiceImpl.class, jahiaApiConfigServiceImpl);
        context.registerService(TripAdvisorEnrichmentService.class, tripAdvisorEnrichmentService);

        String json ="{\r\n" + //
                        "                  \"data\": {\r\n" + //
                        "                    \"jcr\": {\r\n" + //
                        "                      \"destinations\": {\r\n" + //
                        "                        \"nodes\": [\r\n" + //
                        "                          {\r\n" + //
                        "                            \"nodename\": \"orlando\",\r\n" + //
                        "                            \"images\": [],\r\n" + //
                        "                            \"region\": null\r\n" + //
                        "                          }\r\n" + //
                        "                        ]\r\n" + //
                        "                      },\r\n" + //
                        "                      \"propertiesFolderNode\": {\r\n" + //
                        "                        \"names\": {\r\n" + //
                        "                          \"nodes\": [\r\n" + //
                        "                            {\r\n" + //
                        "                              \"nodename\": \"orlando\",\r\n" + //
                        "                              \"name\": \"Orlando Destination\"\r\n" + //
                        "                            }\r\n" + //
                        "                          ]\r\n" + //
                        "                        }\r\n" + //
                        "                      }\r\n" + //
                        "                    }\r\n" + //
                        "                  }\r\n" + //
                        "                }";

        JsonNode jsonNode = mapper.readTree(json);

        // Add JSON to request attribute (what your model expects)
        context.request().setAttribute("destinationResponse", jsonNode);
    }

    private void setField(Object target, String fieldName, Object value) {
        for (Class<?> c = target.getClass(); c != null; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    try {
                        field.setAccessible(true);
                        field.set(target, value);
                        return;
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to set " + fieldName, e);
                    }
                }
            }
        }
        throw new RuntimeException("Field not found: " + fieldName);
    }

    @Test
    void testGetPropertiesList_enrichesDestinationName() throws Exception {

        DestinationDetails model =
                context.request().adaptTo(DestinationDetails.class);

        assertNotNull(model);
        setField(model, "jahiaApiConfigServiceImpl", jahiaApiConfigServiceImpl);
        setField(model, "tripAdvisorEnrichmentService", tripAdvisorEnrichmentService);

        List<DestinationLandingDto> result = model.getPropertiesList();

        assertNotNull(result);
        assertEquals(1, result.size());

        DestinationLandingDto dto = result.get(0);

        assertEquals("orlando", dto.getNodename());
        assertEquals("Orlando Destination", dto.getDestination());
    }

    @Test
    void testGetPropertiesList_handlesMissingNamesGracefully() throws Exception {

        // Override request attribute with no names
        String json =
                " {\r\n" + //
                                        "                  \"data\": {\r\n" + //
                                        "                    \"jcr\": {\r\n" + //
                                        "                      \"destinations\": {\r\n" + //
                                        "                        \"nodes\": [\r\n" + //
                                        "                          {\r\n" + //
                                        "                            \"nodename\": \"hawaii\"\r\n" + //
                                        "                          }\r\n" + //
                                        "                        ]\r\n" + //
                                        "                      }\r\n" + //
                                        "                    }\r\n" + //
                                        "                  }\r\n" + //
                                        "                }";

        context.request().setAttribute(
                "destinationResponse",
                mapper.readTree(json)
        );

        DestinationDetails model =
                context.request().adaptTo(DestinationDetails.class);

        assertNotNull(model);
        setField(model, "jahiaApiConfigServiceImpl", jahiaApiConfigServiceImpl);
        setField(model, "tripAdvisorEnrichmentService", tripAdvisorEnrichmentService);

        List<DestinationLandingDto> result = model.getPropertiesList();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getDestination());
    }
}

