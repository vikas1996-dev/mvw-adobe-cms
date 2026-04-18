package com.mvw.core.constants;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GraphqlQueriesTest {

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_NotNull() {
        assertNotNull(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC);
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_NotEmpty() {
        assertFalse(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.isEmpty());
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_ContainsQueryKeyword() {
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("query"));
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_ContainsJcr() {
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("jcr"));
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_ContainsDining() {
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("dining"));
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_ContainsActivities() {
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("activities"));
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_ContainsAmenities() {
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("amenities"));
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_ContainsVillas() {
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("villas"));
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_ContainsProperties() {
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("properties"));
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_ContainsAwards() {
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("awards"));
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_ContainsPlaces() {
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("places"));
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_ContainsAlerts() {
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("alerts"));
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_ContainsVariables() {
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("$query2"));
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("$query3"));
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("$query4"));
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("$query5"));
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("$query6"));
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("$query7"));
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("$query8"));
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("$query9"));
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_ContainsImages() {
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("images"));
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_ContainsGallery() {
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("gallery"));
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_ValidGraphqlSyntax() {
        // Basic validation of GraphQL structure
        int openBraces = 0;
        int closeBraces = 0;
        for (char c : GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.toCharArray()) {
            if (c == '{') openBraces++;
            if (c == '}') closeBraces++;
        }
        assertEquals(openBraces, closeBraces, "GraphQL query should have matching braces");
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_ContainsCoordinates() {
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("coordinateLatitude"));
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("coordinateLongitude"));
    }

    @Test
    void testJahiaGraphqlQueryGetResortByUpc_ContainsSocialMedia() {
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("twitter"));
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("facebook"));
        assertTrue(GraphqlQueries.JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC.contains("instagram"));
    }
}
