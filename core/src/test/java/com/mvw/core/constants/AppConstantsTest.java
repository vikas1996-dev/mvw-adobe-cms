// package com.mvw.core.constants;

// import static org.junit.jupiter.api.Assertions.*;

// import org.junit.jupiter.api.Test;

// class AppConstantsTest {

//     @Test
//     void testJahiaGraphqlEndpoint() {
//         assertNotNull(AppConstants.JAHIA_GRAPHQL_ENDPOINT);
//         assertTrue(AppConstants.JAHIA_GRAPHQL_ENDPOINT.contains("graphql"));
//     }

//     @Test
//     void testJahiaAuthToken() {
//         assertNotNull(AppConstants.JAHIA_AUTH_TOKEN);
//         assertTrue(AppConstants.JAHIA_AUTH_TOKEN.length() > 0);
//     }

//     @Test
//     void testContentTypeApplicationJson() {
//         assertEquals("application/json", AppConstants.CONTENTTYPE_APPLICATION_JSON);
//     }

//     @Test
//     void testContentTypeLiteral() {
//         assertEquals("Content-Type", AppConstants.CONTENTTYPE_LITERAL);
//     }

//     @Test
//     void testAuthorizationLiteral() {
//         assertEquals("Authorization", AppConstants.AUTHORIZATION_LITERAL);
//     }

//     @Test
//     void testQueryLiteral() {
//         assertEquals("query", AppConstants.QUERY_LITERAL);
//     }

//     @Test
//     void testVariablesLiteral() {
//         assertEquals("variables", AppConstants.VARIABLES_LITERAL);
//     }

//     @Test
//     void testBearerLiteral() {
//         assertEquals("Bearer", AppConstants.BEARER_LITERAL);
//     }

//     @Test
//     void testSpaceLiteral() {
//         assertEquals(" ", AppConstants.SPACE_LITERAL);
//     }

//     @Test
//     void testCountryUSA() {
//         assertEquals("United States of America", AppConstants.COUNTRY_USA);
//     }

//     @Test
//     void testTmvcBaseContentPath() {
//         assertEquals("/content/tmvcs/us/en", AppConstants.TMVC_BASE_CONTENT_PATH);
//     }

//     @Test
//     void testPropertiesFolderNode() {
//         assertNotNull(AppConstants.PROPERTIES_FOLDER_NODE);
//         assertTrue(AppConstants.PROPERTIES_FOLDER_NODE.contains("properties"));
//     }

//     @Test
//     void testDestinations() {
//         assertNotNull(AppConstants.DESTINATIONS);
//         assertTrue(AppConstants.DESTINATIONS.contains("destinations"));
//     }

//     @Test
//     void testVacationsPath() {
//         assertNotNull(AppConstants.VACATIONS_PATH);
//         assertTrue(AppConstants.VACATIONS_PATH.contains("vacation"));
//     }

//     @Test
//     void testEditorialPath() {
//         assertNotNull(AppConstants.EDITORIAL_PATH);
//         assertTrue(AppConstants.EDITORIAL_PATH.contains("editorial"));
//     }

//     @Test
//     void testTripadvisorApi() {
//         assertNotNull(AppConstants.TRIPADVISOR_API);
//         assertTrue(AppConstants.TRIPADVISOR_API.contains("tripadvisor"));
//     }

//     @Test
//     void testTripadvisorApiKey() {
//         assertNotNull(AppConstants.TRIPADVISOR_API_KEY);
//         assertTrue(AppConstants.TRIPADVISOR_API_KEY.length() > 0);
//     }

//     @Test
//     void testConstantsAreStatic() {
//         // Verify the class can be used statically without instantiation
//         String endpoint = AppConstants.JAHIA_GRAPHQL_ENDPOINT;
//         assertNotNull(endpoint);
//     }
// }
