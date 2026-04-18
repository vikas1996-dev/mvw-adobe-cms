package com.mvw.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.Promotions;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class DestinationDefaultPromotionsTest {

    private final AemContext context = new AemContext();

    @Mock
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jahiaApiConfigServiceImpl.getApiImagePath()).thenReturn("");
        context.registerService(JahiaApiConfigServiceImpl.class, jahiaApiConfigServiceImpl);
    }

    @Test
    void testGetPropertiesList() throws Exception {

        // Mock request attribute JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree("{\r\n" + //
                        "          \"data\": {\r\n" + //
                        "            \"jcr\": {\r\n" + //
                        "              \"defaultPromotions\": {\r\n" + //
                        "                \"nodes\": [\r\n" + //
                        "                  { \"name\": \"editorial-test-1\" },\r\n" + //
                        "                  { \"name\": \"promo-test-2\" }\r\n" + //
                        "                ]\r\n" + //
                        "              }\r\n" + //
                        "            }\r\n" + //
                        "          }\r\n" + //
                        "        }");

        context.request().setAttribute("destinationResponse", jsonNode);

        DestinationDefaultPromotions model =
                context.request().adaptTo(DestinationDefaultPromotions.class);

        assertNotNull(model, "Model should be adapted from request");
        List<Promotions> promotions = model.getPropertiesList();

        assertEquals(2, promotions.size());
        assertEquals("editorial-test-1", promotions.get(0).getName());
    }

    @Test
    void testFilterEditorialPromotions() {

        Promotions p1 = new Promotions();
        p1.setName("editorial-offer-1");

        Promotions p2 = new Promotions();
        p2.setName("promo-offer-2");

        List<Promotions> result =
                DestinationDefaultPromotions
                        .filterEditorialPromotions(List.of(p1, p2));

        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().contains("editorial"));
    }
}
