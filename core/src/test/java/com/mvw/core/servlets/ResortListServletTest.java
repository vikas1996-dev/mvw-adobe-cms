// package com.mvw.core.servlets;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.mvw.core.models.dto.ResortDto;
// import com.mvw.core.models.dto.Tags;
// import com.mvw.core.services.TripAdvisorEnrichmentService;
// import io.wcm.testing.mock.aem.junit5.AemContext;
// import io.wcm.testing.mock.aem.junit5.AemContextExtension;
// import org.apache.sling.api.resource.ResourceResolver;
// import org.apache.sling.testing.mock.osgi.MockOsgi;
// import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyList;
// import static org.mockito.Mockito.*;

// import java.lang.reflect.Field;
// import java.util.List;

// @ExtendWith(AemContextExtension.class)
// class ResortListServletTest {

//     private static final ObjectMapper MAPPER = new ObjectMapper();
//     private final AemContext context = new AemContext();

//     private ResortListServlet servlet;

//     @Mock
//     private TripAdvisorEnrichmentService tripAdvisorEnrichmentService;

//     @BeforeEach
//     void setUp() throws Exception {
//         MockitoAnnotations.openMocks(this);

//         context.registerService(TripAdvisorEnrichmentService.class, tripAdvisorEnrichmentService);

//         // Mock enrichWithTripAdvisorParallel to return the same list passed in
//         when(tripAdvisorEnrichmentService.enrichWithTripAdvisorParallel(any(ResourceResolver.class), anyList()))
//                 .thenAnswer(invocation -> invocation.getArgument(1));

//         servlet = spy(new ResortListServlet());

//         // Inject the mock service using reflection
//         Field serviceField = ResortListServlet.class.getDeclaredField("tripAdvisorEnrichmentService");
//         serviceField.setAccessible(true);
//         serviceField.set(servlet, tripAdvisorEnrichmentService);

//         MockOsgi.injectServices(servlet, context.bundleContext());

//         JsonNode graphQlResponse = MAPPER.readTree(MOCK_GRAPHQL_JSON);

//         doReturn(graphQlResponse)
//                 .when(servlet)
//                 .executeGraphQlRequest();
//     }

//     // ------------------------------------------------------------------
//     // HAPPY PATH
//     // ------------------------------------------------------------------

//     @Test
//     void testDoGet_success_fullResponse() throws Exception {

//         MockSlingHttpServletResponse response = context.response();

//         servlet.doGet(context.request(), response);

//         String json = response.getOutputAsString();

//         assertEquals(200, response.getStatus());
//         assertTrue(json.contains("\"resorts\""));
//         assertTrue(json.contains("\"filters\""));
//         assertTrue(json.contains("\"offerCards\""));
//         assertTrue(json.contains("\"count\":2"));
//         assertTrue(response.getContentType().contains("application/json"));
//     }

//     // ------------------------------------------------------------------
//     // EMPTY RESORT LIST (extractResorts branch)
//     // ------------------------------------------------------------------

//     @Test
//     void testDoGet_emptyResorts() throws Exception {

//         JsonNode emptyGraphQl = MAPPER.readTree(EMPTY_GRAPHQL_JSON);

//         doReturn(emptyGraphQl)
//                 .when(servlet)
//                 .executeGraphQlRequest();

//         MockSlingHttpServletResponse response = context.response();

//         servlet.doGet(context.request(), response);

//         String json = response.getOutputAsString();

//         assertEquals(200, response.getStatus());
//         assertTrue(json.contains("\"count\":0"));
//     }

//     // ------------------------------------------------------------------
//     // TRIPADVISOR ID NULL (early return branch)
//     // ------------------------------------------------------------------

//     @Test
//     void testDoGet_tripAdvisorIdNull() throws Exception {

//         // When resorts don't have tripadvisorId, enrichment should still work
//         when(tripAdvisorEnrichmentService.enrichWithTripAdvisorParallel(any(ResourceResolver.class), anyList()))
//                 .thenAnswer(invocation -> invocation.getArgument(1));

//         MockSlingHttpServletResponse response = context.response();

//         servlet.doGet(context.request(), response);

//         assertEquals(200, response.getStatus());
//     }

//     // ------------------------------------------------------------------
//     // GRAPHQL FAILURE
//     // ------------------------------------------------------------------

//     @Test
//     void testDoGet_graphQlFailure_returns500() throws Exception {

//         doThrow(new RuntimeException("GraphQL failed"))
//                 .when(servlet)
//                 .executeGraphQlRequest();

//         MockSlingHttpServletResponse response = context.response();

//         servlet.doGet(context.request(), response);

//         assertEquals(500, response.getStatus());
//         assertTrue(response.getOutputAsString().contains("Unable to fetch resort list"));
//     }

//     // ------------------------------------------------------------------
//     // MOCK JSON
//     // ------------------------------------------------------------------

//     private static final String MOCK_GRAPHQL_JSON = "{\n" +
//             "  \"data\": {\n" +
//             "    \"jcr\": {\n" +
//             "      \"propertiesFolderNode\": {\n" +
//             "        \"propertiesRoot\": {\n" +
//             "          \"nodes\": [\n" +
//             "            {\n" +
//             "              \"name\": \"Z Resort\",\n" +
//             "              \"country\": \"United States\",\n" +
//             "              \"tripadvisorId\": \"123\"\n" +
//             "            },\n" +
//             "            {\n" +
//             "              \"name\": \"A Resort\",\n" +
//             "              \"country\": \"United States\"\n" +
//             "            }\n" +
//             "          ]\n" +
//             "        }\n" +
//             "      },\n" +
//             "      \"promotions\": {\n" +
//             "        \"nodes\": []\n" +
//             "      }\n" +
//             "    }\n" +
//             "  }\n" +
//             "}";

//     private static final String EMPTY_GRAPHQL_JSON = "{\n" +
//             "  \"data\": {\n" +
//             "    \"jcr\": {\n" +
//             "      \"propertiesFolderNode\": {\n" +
//             "        \"propertiesRoot\": {\n" +
//             "          \"nodes\": []\n" +
//             "        }\n" +
//             "      },\n" +
//             "      \"promotions\": { \"nodes\": [] }\n" +
//             "    }\n" +
//             "  }\n" +
//             "}";

//      private static final String GRAPHQL_WITH_PROMOTIONS = "{\n" +
// "  \"data\": {\n" +
// "    \"jcr\": {\n" +
// "      \"propertiesFolderNode\": {\n" +
// "        \"propertiesRoot\": {\n" +
// "          \"nodes\": [\n" +
// "            {\n" +
// "              \"name\": \"Test Resort\",\n" +
// "              \"country\": \"USA\",\n" +
// "              \"tripadvisorId\": \"123\"\n" +
// "            }\n" +
// "          ]\n" +
// "        }\n" +
// "      },\n" +
// "      \"promotions\": {\n" +
// "        \"nodes\": [\n" +
// "          {\n" +
// "            \"priority\": \"1\",\n" +
// "            \"name\": \"Promo Name\",\n" +
// "            \"shortDescriptionAd\": \"Promo Title\",\n" +
// "            \"descriptionAd\": \"Promo Desc\",\n" +
// "            \"buttonOfferTextAd\": \"Book\",\n" +
// "            \"buttonOfferUrlAd\": \"/book\",\n" +
// "            \"imagesAd\": [\n" +
// "              {\n" +
// "                \"altText\": \"Alt\",\n" +
// "                \"photo\": null\n" +
// "              },\n" +
// "              {\n" +
// "                \"altText\": \"Alt2\",\n" +
// "                \"photo\": [\n" +
// "                  { \"name\": \"img\", \"path\": \"/img.png\", \"ratio\": \"16:9\" }\n" +
// "                ]\n" +
// "              }\n" +
// "            ]\n" +
// "          }\n" +
// "        ]\n" +
// "      }\n" +
// "    }\n" +
// "  }\n" +
// "}";
// @Test
// void testDoGet_withPromotionsAndImages_coversOfferBranches() throws Exception {

//     JsonNode promoGraphQl = MAPPER.readTree(GRAPHQL_WITH_PROMOTIONS);

//     doReturn(promoGraphQl)
//             .when(servlet)
//             .executeGraphQlRequest();

//     when(tripAdvisorEnrichmentService.enrichWithTripAdvisorParallel(any(ResourceResolver.class), anyList()))
//             .thenAnswer(invocation -> invocation.getArgument(1));

//     MockSlingHttpServletResponse response = context.response();

//     servlet.doGet(context.request(), response);

//     String json = response.getOutputAsString();

//     assertEquals(200, response.getStatus());
//     assertTrue(json.contains("Promo Title"));
//     assertTrue(json.contains("offerCards"));
//     assertTrue(json.contains("images"));
//     assertTrue(json.contains("count"));
//     assertTrue(json.contains("offerCards"));
//     assertTrue(json.contains("filters"));
//     assertTrue(json.contains("resorts"));
// }

// private static final String GRAPHQL_WITH_TRIPADVISOR_ID = "{\n" +
// "  \"data\": {\n" +
// "    \"jcr\": {\n" +
// "      \"propertiesFolderNode\": {\n" +
// "        \"propertiesRoot\": {\n" +
// "          \"nodes\": [\n" +
// "            {\n" +
// "              \"name\": \"Edge Resort\",\n" +
// "              \"country\": \"USA\",\n" +
// "              \"tripadvisorId\": \"999\"\n" +
// "            }\n" +
// "          ]\n" +
// "        }\n" +
// "      },\n" +
// "      \"promotions\": { \"nodes\": [] }\n" +
// "    }\n" +
// "  }\n" +
// "}";
// @Test
// void testDoGet_whenTripAdvisorThrows_exceptionBranchCovered() throws Exception {

//     // Force TripAdvisor enrichment exception
//     when(tripAdvisorEnrichmentService.enrichWithTripAdvisorParallel(any(ResourceResolver.class), anyList()))
//             .thenThrow(new RuntimeException("TA failure"));

//     // GraphQL must still return a resort WITH tripadvisorId
//     JsonNode graphQl = MAPPER.readTree(GRAPHQL_WITH_TRIPADVISOR_ID);

//     doReturn(graphQl)
//             .when(servlet)
//             .executeGraphQlRequest();

//     MockSlingHttpServletResponse response = context.response();

//     servlet.doGet(context.request(), response);

//     // Servlet should return error since enrichment failed
//     assertEquals(500, response.getStatus());
//     assertTrue(response.getOutputAsString().contains("Unable to fetch resort list"));
// }
// @Test
// void testDoGet_offerWithMultipleImagesAndAlt() throws Exception {

//     String graphQlWithImages = "{\n" +
//             "  \"data\": {\n" +
//             "    \"jcr\": {\n" +
//             "      \"propertiesFolderNode\": {\"propertiesRoot\":{\"nodes\":[]}},\n" +
//             "      \"promotions\": {\"nodes\":[\n" +
//             "        {\n" +
//             "          \"priority\":\"1\",\n" +
//             "          \"name\":\"Promo Eyebrow\",\n" +
//             "          \"shortDescriptionAd\":\"Promo Title\",\n" +
//             "          \"descriptionAd\":\"Desc\",\n" +
//             "          \"buttonOfferTextAd\":\"Book\",\n" +
//             "          \"buttonOfferUrlAd\":\"/book\",\n" +
//             "          \"buttonOfferTextAd1\":\"Book1\",\n" +
//             "          \"buttonOfferUrlAd1\":\"/book1\",\n" +
//             "          \"imagesAd\":[\n" +
//             "            {\"altText\":\"Alt1\",\"photo\":null},\n" +
//             "            {\"altText\":\"Alt2\",\"photo\":[{\"name\":\"img2\",\"path\":\"/img2.png\",\"ratio\":\"4:3\"}]}]\n" +
//             "        }\n" +
//             "      ]}\n" +
//             "    }\n" +
//             "  }\n" +
//             "}";

//     JsonNode promoGraphQl = MAPPER.readTree(graphQlWithImages);
//     doReturn(promoGraphQl).when(servlet).executeGraphQlRequest();

//     MockSlingHttpServletResponse response = context.response();
//     servlet.doGet(context.request(), response);

//     String json = response.getOutputAsString();
//     assertEquals(200, response.getStatus());
//     assertTrue(json.contains("Promo Title"));
//     assertTrue(json.contains("images"));
//     assertTrue(json.contains("Alt1"));
//     assertTrue(json.contains("Alt2"));
//     assertTrue(json.contains("Book1"));
// }

// @Test
// void testDoGet_tripAdvisorParallelExceptionHandled() throws Exception {

//     // Resort with tripadvisorId triggers fetchTripAdvisorData
//     String graphQl = "{\n" +
//             "  \"data\": {\"jcr\": {\"propertiesFolderNode\":{\"propertiesRoot\":{\"nodes\":[\n" +
//             "    {\"name\":\"Resort A\",\"country\":\"USA\",\"tripadvisorId\":\"777\"}\n" +
//             "  ]}},\"promotions\":{\"nodes\":[]}}}}";

//     JsonNode graphQlNode = MAPPER.readTree(graphQl);
//     doReturn(graphQlNode).when(servlet).executeGraphQlRequest();

//     // Force TripAdvisorEnrichmentService to throw exception
//     when(tripAdvisorEnrichmentService.enrichWithTripAdvisorParallel(any(ResourceResolver.class), anyList()))
//             .thenThrow(new RuntimeException("TripAdvisor fails"));

//     MockSlingHttpServletResponse response = context.response();
//     servlet.doGet(context.request(), response);

//     // Should return error since enrichment failed
//     assertEquals(500, response.getStatus());
//     assertTrue(response.getOutputAsString().contains("Unable to fetch resort list"));
// }

// @Test
// void testDoGet_filtersPopulatedFromResort() throws Exception {

//     // -----------------------
//     // Step 1: Create a resort with all filter fields populated
//     // -----------------------
//     ResortDto resort = new ResortDto();
//     resort.setRegion("East Coast");                 // Region filter

//     // Vacation types must be List<Tags> with names
//     Tags vacationTag = new Tags();
//     vacationTag.setName("Family");
//     resort.setVacationTypes(List.of(vacationTag));

//     // Activities must be List<Tags> with names
//     Tags activityTag = new Tags();
//     activityTag.setName("Skiing");
//     resort.setActivityTags(List.of(activityTag));

//     // Brand (must have a non-null name)
//     Tags brand = new Tags();
//     brand.setName("Marriott");
//     resort.setDcmBrand(brand);

//     // -----------------------
//     // Step 2: Mock GraphQL response (empty, since we override extractResorts)
//     // -----------------------
//     String graphQl = "{\n" +
//             "  \"data\": {\"jcr\": {\"propertiesFolderNode\":{\"propertiesRoot\":{\"nodes\":[]}},\"promotions\":{\"nodes\":[]}}}}";

//     JsonNode graphQlNode = MAPPER.readTree(graphQl);
//     doReturn(graphQlNode).when(servlet).executeGraphQlRequest();

//     // -----------------------
//     // Step 3: Override extractResorts to return our custom resort
//     // -----------------------
//     doReturn(List.of(resort)).when(servlet).extractResorts(graphQlNode);

//     // -----------------------
//     // Step 4: Perform GET request
//     // -----------------------
//     MockSlingHttpServletResponse response = context.response();
//     servlet.doGet(context.request(), response);

//     String json = response.getOutputAsString();

//     // -----------------------
//     // Step 5: Assert that all filters are present in response
//     // -----------------------
//     assertEquals(200, response.getStatus());
//     assertTrue(json.contains("East Coast")); // region
//     assertTrue(json.contains("Family"));     // vacation type
//     assertTrue(json.contains("Skiing"));     // activity
//     assertTrue(json.contains("Marriott"));   // brand
// }

// @Test
// void testDoGet_resortWithDoNotShowFlag() throws Exception {
//     String graphQlWithFlag = "{\n" +
//             "  \"data\": {\n" +
//             "    \"jcr\": {\n" +
//             "      \"propertiesFolderNode\": {\n" +
//             "        \"propertiesRoot\": {\n" +
//             "          \"nodes\": [\n" +
//             "            {\n" +
//             "              \"name\": \"Hidden Resort\",\n" +
//             "              \"country\": \"USA\",\n" +
//             "              \"flags\": [{\"nodename\": \"do-not-show\"}]\n" +
//             "            },\n" +
//             "            {\n" +
//             "              \"name\": \"Visible Resort\",\n" +
//             "              \"country\": \"USA\"\n" +
//             "            }\n" +
//             "          ]\n" +
//             "        }\n" +
//             "      },\n" +
//             "      \"promotions\": { \"nodes\": [] }\n" +
//             "    }\n" +
//             "  }\n" +
//             "}";

//     JsonNode graphQlNode = MAPPER.readTree(graphQlWithFlag);
//     doReturn(graphQlNode).when(servlet).executeGraphQlRequest();

//     MockSlingHttpServletResponse response = context.response();
//     servlet.doGet(context.request(), response);

//     String json = response.getOutputAsString();
//     assertEquals(200, response.getStatus());
//     assertTrue(json.contains("Visible Resort"));
//     assertFalse(json.contains("Hidden Resort"));
// }

// @Test
// void testExtractResorts_invalidJson() throws Exception {
//     JsonNode invalidNode = MAPPER.createObjectNode();
//     List<ResortDto> result = servlet.extractResorts(invalidNode);
//     assertTrue(result.isEmpty());
// }

// @Test
// void testBuildFilters_emptyResorts() {
//     List<ResortDto> emptyList = List.of();
//     assertNotNull(servlet.buildFilters(emptyList));
// }

// @Test
// void testMapToOffer_withAllFields() {
//     com.mvw.core.models.dto.Promotions promo = new com.mvw.core.models.dto.Promotions();
//     setFieldValue(promo, "name", "Test Promo");
//     setFieldValue(promo, "shortDescriptionAd", "Short Desc");
//     setFieldValue(promo, "descriptionAd", "Full Description");
//     setFieldValue(promo, "buttonOfferTextAd", "Book Now");
//     setFieldValue(promo, "buttonOfferUrlAd", "/book");
//     setFieldValue(promo, "buttonOfferTextAd1", "Learn More");
//     setFieldValue(promo, "buttonOfferUrlAd1", "/learn");

//     com.mvw.core.models.dto.Offer result = servlet.mapToOffer(promo);

//     assertNotNull(result);
//     assertEquals("Test Promo", result.getEyebrow());
//     assertEquals("Short Desc", result.getTitle());
// }

// @Test
// void testMapImages_nullPhoto() {
//     com.mvw.core.models.dto.ImageAd imageAd = new com.mvw.core.models.dto.ImageAd();
//     imageAd.setAltText("Alt Text");
//     imageAd.setPhoto(null);

//     java.util.stream.Stream<com.mvw.core.models.dto.Offer.Image> result = servlet.mapImages(imageAd);
//     List<com.mvw.core.models.dto.Offer.Image> images = result.collect(java.util.stream.Collectors.toList());

//     assertEquals(1, images.size());
//     assertEquals("Alt Text", images.get(0).getAltText());
// }

// @Test
// void testMapImages_withPhoto() {
//     com.mvw.core.models.dto.ImageAd imageAd = new com.mvw.core.models.dto.ImageAd();
//     com.mvw.core.models.dto.ImageAd.Photo photo = new com.mvw.core.models.dto.ImageAd.Photo();
//     photo.setName("photo1");
//     photo.setPath("/content/dam/photo.jpg");
//     photo.setRatio("16:9");
//     imageAd.setAltText("Alt Text");
//     imageAd.setPhoto(List.of(photo));

//     java.util.stream.Stream<com.mvw.core.models.dto.Offer.Image> result = servlet.mapImages(imageAd);
//     List<com.mvw.core.models.dto.Offer.Image> images = result.collect(java.util.stream.Collectors.toList());

//     assertEquals(1, images.size());
//     assertEquals("/content/dam/photo.jpg", images.get(0).getPath());
// }

// @Test
// void testExtractOffers_emptyPromotions() throws Exception {
//     String graphQl = "{\n" +
//             "  \"data\": {\n" +
//             "    \"jcr\": {\n" +
//             "      \"propertiesFolderNode\": {\"propertiesRoot\":{\"nodes\":[]}},\n" +
//             "      \"promotions\": {\"nodes\":[]}\n" +
//             "    }\n" +
//             "  }\n" +
//             "}";

//     JsonNode graphQlNode = MAPPER.readTree(graphQl);
//     List<com.mvw.core.models.dto.Offer> offers = servlet.extractOffers(graphQlNode);
//     assertTrue(offers.isEmpty());
// }

// private void setFieldValue(Object target, String fieldName, Object value) {
//     try {
//         java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
//         field.setAccessible(true);
//         field.set(target, value);
//     } catch (Exception e) {
//         // Field may not exist
//     }
// }
// }
