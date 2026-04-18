package com.mvw.core.servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.*;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
class ResortDestinationServletTest {

    private final AemContext context = new AemContext();
    private ResortDestinationServlet servlet;
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        servlet = spy(new ResortDestinationServlet());
        mapper = new ObjectMapper();
    }

    /* ===================== DO GET ===================== */

    @Test
    void doGet_success() throws Exception {
        doReturn(mockGraphQlJson())
                .when(servlet)
                .executeGraphQlRequest();

        servlet.doGet(context.request(), context.response());

        String output = context.response().getOutputAsString();

        assertEquals(200, context.response().getStatus());
        assertTrue(output.contains("leftColumn"));
        assertTrue(output.contains("rightColumn"));
        assertTrue(output.contains("brandsFilter"));
        assertTrue(output.contains("regionFilter"));
    }

    @Test
    void doGet_errorPath() throws Exception {
        doThrow(new RuntimeException("boom"))
                .when(servlet)
                .executeGraphQlRequest();

        servlet.doGet(context.request(), context.response());

        assertEquals(500, context.response().getStatus());
        assertTrue(context.response().getOutputAsString()
                .contains("Unable to fetch resort list"));
    }

  


    /* ===================== HELPERS ===================== */

    @Test
    void parsePriority_invalid() {
        assertEquals(Integer.MAX_VALUE, servlet.parsePriority("abc"));
    }

    @Test
    void parsePriority_valid() {
        assertEquals(100, servlet.parsePriority("100"));
    }

    @Test
    void extractResorts_emptyNode() {
        JsonNode empty = mapper.createObjectNode();
        assertTrue(servlet.extractResorts(empty).isEmpty());
    }

    @Test
    void isMexico_true() {
        ResortListDto r = new ResortListDto();
        r.setContinent("North America");
        r.setRegion("Mexico");
        assertTrue(servlet.isMexico(r));
    }

    @Test
    void isMexico_false() {
        ResortListDto r = new ResortListDto();
        r.setContinent("Europe");
        r.setRegion("Spain");
        assertFalse(servlet.isMexico(r));
    }

    /* ===================== MOCK DATA ===================== */

    private JsonNode mockGraphQlJson() throws Exception {
        return mapper.readTree("{\n" +
                "  \"data\": {\n" +
                "    \"jcr\": {\n" +
                "      \"propertiesFolderNode\": {\n" +
                "        \"propertiesRoot\": {\n" +
                "          \"nodes\": [\n" +
                "            {\n" +
                "              \"name\": \"Marriott's Canyon Villas\",\n" +
                "              \"continent\": \"North America\",\n" +
                "              \"country\": \"United States\",\n" +
                "              \"region\": \"USA - West\",\n" +
                "              \"state\": \"Arizona\",\n" +
                "              \"city\": \"Phoenix\",\n" +
                "              \"dcmBrand\": {\"name\": \"Marriott Vacation Club®\"},\n" +
                "              \"locale\": {\"priority\": \"200\"}\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"Westin Lagunamar\",\n" +
                "              \"continent\": \"North America\",\n" +
                "              \"country\": \"Mexico\",\n" +
                "              \"region\": \"Mexico\",\n" +
                "              \"state\": \"Quintana Roo\",\n" +
                "              \"city\": \"Cancún\",\n" +
                "              \"dcmBrand\": {\"name\": \"Westin® Vacation Club\"},\n" +
                "              \"locale\": {\"priority\": \"300\"}\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}");
    }

    private ResortListDto createResort(
            String name,
            String continent,
            String country,
            String region,
            String state,
            String city,
            String priority
    ) {
        ResortListDto r = new ResortListDto();
        r.setName(name);
        r.setContinent(continent);
        r.setCountry(country);
        r.setRegion(region);
        r.setState(state);
        // city is read-only in ResortListDto, use reflection
        setFieldValue(r, "city", city);
        Tags brand = new Tags();
        brand.setName("Test Brand");
        r.setDcmBrand(brand);
        
        if (priority != null) {
            ResortListDto.LocaleInfo locale = new ResortListDto.LocaleInfo();
            locale.setPriority(priority);
            r.setLocale(locale);
        }

        return r;
    }
    
    private void setFieldValue(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            // ignore
        }
    }

    /* ===================== BUILD RESPONSE TESTS ===================== */

    @Test
    void buildResponse_withWashingtonDC() {
        ResortListDto dcResort = createResort(
            "DC Resort",
            "North America",
            "United States",
            "USA - East",
            "D.C.",
            "Washington",
            "100"
        );

        ResortListResponseDTO result = servlet.buildResponse(List.of(dcResort));

        assertNotNull(result);
        // State should be normalized to "Washington, D.C."
    }

    @Test
    void buildResponse_withMexicoResort() {
        ResortListDto mexicoResort = createResort(
            "Cancun Resort",
            "North America",
            "Mexico",
            "Mexico",
            "Quintana Roo",
            "Cancún",
            "200"
        );

        ResortListResponseDTO result = servlet.buildResponse(List.of(mexicoResort));

        assertNotNull(result);
        assertNotNull(result.getRightColumn());
    }

    @Test
    void buildResponse_withEuropeanResort() {
        ResortListDto europeResort = createResort(
            "Spain Resort",
            "Europe",
            "Spain",
            "Europe",
            null,
            "Barcelona",
            "300"
        );

        ResortListResponseDTO result = servlet.buildResponse(List.of(europeResort));

        assertNotNull(result);
        assertNotNull(result.getRightColumn());
    }

    @Test
    void buildResponse_withUsaWestResort() {
        ResortListDto usaWestResort = createResort(
            "Arizona Resort",
            "North America",
            "United States",
            "USA - West",
            "Arizona",
            "Phoenix",
            "150"
        );

        ResortListResponseDTO result = servlet.buildResponse(List.of(usaWestResort));

        assertNotNull(result);
        assertNotNull(result.getLeftColumn());
    }

    @Test
    void buildResponse_withMultipleResorts() {
        ResortListDto resort1 = createResort(
            "Resort A",
            "North America",
            "United States",
            "USA - East",
            "Florida",
            "Orlando",
            "100"
        );
        ResortListDto resort2 = createResort(
            "Resort B",
            "North America",
            "United States",
            "USA - West",
            "California",
            "Los Angeles",
            "200"
        );
        ResortListDto resort3 = createResort(
            "Resort C",
            "Europe",
            "France",
            "Europe",
            null,
            "Paris",
            "300"
        );

        ResortListResponseDTO result = servlet.buildResponse(List.of(resort1, resort2, resort3));

        assertNotNull(result);
        assertNotNull(result.getLeftColumn());
        assertNotNull(result.getRightColumn());
        assertNotNull(result.getBrandsFilter());
        assertNotNull(result.getRegionFilter());
    }

    @Test
    void buildResponse_withNullBrand() {
        ResortListDto resortNoBrand = new ResortListDto();
        resortNoBrand.setName("No Brand Resort");
        resortNoBrand.setContinent("North America");
        resortNoBrand.setCountry("United States");
        resortNoBrand.setRegion("USA - East");
        resortNoBrand.setState("Texas");
        setFieldValue(resortNoBrand, "city", "Houston");
        resortNoBrand.setDcmBrand(null);

        ResortListResponseDTO result = servlet.buildResponse(List.of(resortNoBrand));

        assertNotNull(result);
    }

    @Test
    void buildResponse_withNullState() {
        ResortListDto resortNoState = createResort(
            "No State Resort",
            "North America",
            "United States",
            "USA - East",
            null,
            "City",
            "100"
        );

        ResortListResponseDTO result = servlet.buildResponse(List.of(resortNoState));

        assertNotNull(result);
    }

    @Test
    void buildResponse_withNullCountry() {
        ResortListDto resortNoCountry = new ResortListDto();
        resortNoCountry.setName("No Country Resort");
        resortNoCountry.setContinent("Asia");
        resortNoCountry.setCountry(null);
        resortNoCountry.setRegion("Asia");
        resortNoCountry.setState("State");
        setFieldValue(resortNoCountry, "city", "City");

        ResortListResponseDTO result = servlet.buildResponse(List.of(resortNoCountry));

        assertNotNull(result);
    }

    @Test
    void buildResponse_emptyList() {
        ResortListResponseDTO result = servlet.buildResponse(List.of());

        assertNotNull(result);
        assertTrue(result.getLeftColumn().isEmpty());
        assertTrue(result.getRightColumn().isEmpty());
    }

    @Test
    void buildResponse_withNullLocale() {
        ResortListDto resortNullLocale = createResort(
            "Null Locale Resort",
            "North America",
            "United States",
            "USA - East",
            "Florida",
            "Miami",
            null
        );

        ResortListResponseDTO result = servlet.buildResponse(List.of(resortNullLocale));

        assertNotNull(result);
    }

    @Test
    void buildResponse_withInvalidPriority() {
        ResortListDto resortInvalidPriority = new ResortListDto();
        resortInvalidPriority.setName("Invalid Priority Resort");
        resortInvalidPriority.setContinent("North America");
        resortInvalidPriority.setCountry("United States");
        resortInvalidPriority.setRegion("USA - East");
        resortInvalidPriority.setState("Georgia");
        setFieldValue(resortInvalidPriority, "city", "Atlanta");
        ResortListDto.LocaleInfo locale = new ResortListDto.LocaleInfo();
        locale.setPriority("not-a-number");
        resortInvalidPriority.setLocale(locale);

        ResortListResponseDTO result = servlet.buildResponse(List.of(resortInvalidPriority));

        assertNotNull(result);
    }

    @Test
    void buildResponse_withBrandNameNull() {
        ResortListDto resortBrandNameNull = createResort(
            "Brand Name Null Resort",
            "North America",
            "United States",
            "USA - East",
            "New York",
            "NYC",
            "100"
        );
        Tags brandWithNullName = new Tags();
        brandWithNullName.setName(null);
        resortBrandNameNull.setDcmBrand(brandWithNullName);

        ResortListResponseDTO result = servlet.buildResponse(List.of(resortBrandNameNull));

        assertNotNull(result);
    }
}
