package com.mvw.core.constants;

public class GraphqlQueries {

    public static String JAHIA_GRAPHQL_QUERY_GET_RESORT_BY_UPC = "query (\n" +
            "  $query2: String!, \n" +
            "  $query3: String!, \n" +
            "  $query4: String!, \n" +
            "  $query5: String!, \n" +
            "  $query6: String!,\n" +
            "  $query7: String!,\n" +
            "  $query8: String!,\n" +
            "  $query9: String!) {\n" +
            "  jcr(workspace: LIVE) {\n" +
            "    dining: nodesByQuery(query: $query6) {\n" +
            "      nodes {\n" +
            "        name: value(name: \"name\", language:\"en\")\n" +
            "        nodename:value(name:\"j:nodename\")\n" +
            "        images: refNodes(name: \"images\") {\n" +
            "          name: value(name: \"name\", language:\"en\")\n" +
            "          nodename: value(name: \"j:nodename\")\n" +
            "          altText: value(name: \"altText\", language:\"en\")\n" +
            "          photo: refNodes(name: \"photo\") {\n" +
            "            name\n" +
            "            path\n" +
            "            width: value(name: \"j:width\")\n" +
            "            ratio: value(name: \"ratio\")\n" +
            "            height: value(name: \"j:height\")\n" +
            "          }\n" +
            "        }\n" +
            "        description:value(name:\"description\", language:\"en\")\n" +
            "        typeOnOffSite: value(name: \"typeOnOffSite\")\n" +
            "        typeCuisine: value(name: \"typeCuisine\")\n" +
            "        typeAtmosphere: value(name: \"typeAtmosphere\")\n" +
            "mobileOrdering: value(name: \"mobileOrdering\")\n" +
            "        hours: value(name: \"hours\", language:\"en\")\n" +
            "        phone: value(name: \"phone\")\n" +
            "        priority: value(name: \"priority\")\n" +
            "        urlMenu: value(name: \"urlMenu\")\n" +
            "      }\n" +
            "    }\n" +
            "    activities: nodesByQuery(query: $query5) {\n" +
            "      nodes {\n" +
            "        name: value(name: \"name\", language:\"en\")\n" +
            "        nodename:value(name:\"j:nodename\")\n" +
            "        images: refNodes(name: \"images\") {\n" +
            "          name: value(name: \"name\", language:\"en\")\n" +
            "          nodename: value(name: \"j:nodename\")\n" +
            "          altText: value(name: \"altText\", language:\"en\")\n" +
            "          photo: refNodes(name: \"photo\") {\n" +
            "            name\n" +
            "            path\n" +
            "            width: value(name: \"j:width\")\n" +
            "            ratio: value(name: \"ratio\")\n" +
            "            height: value(name: \"j:height\")\n" +
            "          }\n" +
            "        }\n" +
            "        description: value(name:\"description\", language:\"en\")\n" +
            "        longDescription: value(name:\"longDescription\", language:\"en\")\n" +
            "        shortDescription: value(name:\"shortDescription\", language:\"en\")\n" +
            "        icon: value(name:\"icon\", language:\"en\")\n" +
            "        priority: value(name:\"priority\")\n" +
            "        activityType: value(name:\"activityType\")\n" +
            "        activityCategories: refNode(name: \"activityCategories\") {\n" +
            "          name: value(name: \"jcr:title\", language:\"en\")\n" +
            "        }\n" +
            "        phone:value(name:\"phone\")\n" +
            "        hours: value(name: \"hours\", language:\"en\")\n" +
            "      }\n" +
            "    }\n" +
            "    amenities: nodesByQuery(query: $query3) {\n" +
            "      nodes {\n" +
            "        name: value(name: \"name\", language:\"en\")\n" +
            "        nodename:value(name:\"j:nodename\")\n" +
            "        shortTitle:value(name:\"shortTitle\", language:\"en\")\n" +
            "        description:value(name:\"description\", language:\"en\")\n" +
            "        priority: value(name:\"priority\")\n" +
            "        icon: value(name:\"icon\")\n" +
            "        mvcsIcon: value(name:\"mvcsIcon\")\n" +
            "        featured: value(name:\"featured\")\n" +
            "      }\n" +
            "    }\n" +
            "    villas: nodesByQuery(query: $query4) {\n" +
            "      nodes {\n" +
            "        name: value(name: \"name\" language:\"en\")\n" +
            "        nodename:value(name:\"j:nodename\")\n" +
            "        sleeps:value(name:\"sleepingCapacity\")\n" +
            "        squareFootage:value(name:\"squareFootage\")\n" +
            "        view:value(name:\"availableViews\" language:\"en\")\n" +
            "        gallery: refNodes(name:\"gallery\") {\n" +
            "          images: refNodes(name: \"images\") {\n" +
            "            name: value(name: \"name\" language:\"en\")\n" +
            "            nodename: value(name: \"j:nodename\")\n" +
            "            altText: value(name: \"altText\", language:\"en\")\n" +
            "            priority: value(name:\"priority\")\n" +
            "              photo: refNodes(name: \"photo\") {\n" +
            "                name\n" +
            "                path\n" +
            "                width: value(name: \"j:width\")\n" +
            "                ratio: value(name: \"ratio\")\n" +
            "                height: value(name: \"j:height\")\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "        images: refNodes(name: \"images\") {\n" +
            "          name: value(name: \"jcr:title\", language:\"en\")\n" +
            "          nodename: value(name: \"j:nodename\")\n" +
            "          altText: value(name: \"altText\", language:\"en\")\n" +
            "            photo: refNodes(name: \"photo\") {\n" +
            "              name\n" +
            "              path\n" +
            "              width: value(name: \"j:width\")\n" +
            "              ratio: value(name: \"ratio\")\n" +
            "              height: value(name: \"j:height\")\n" +
            "            }\n" +
            "          referenceId:value(name:\"referenceID\")\n" +
            "        }\n" +
            "        description: value(name: \"description\", language: \"en\")\n" +
            "      }\n" +
            "    }\n" +
            "    properties: nodesByQuery(query: $query2) {\n" +
            "      nodes {\n" +
            "        coordinateLatitude: value(name: \"coordinateLatitude\")\n" +
            "        coordinateLongitude: value(name: \"coordinateLongitude\")\n" +
            "        checkIn: value(name: \"checkIn\", language:\"en\")\n" +
            "        checkOut: value(name: \"checkOut\", language:\"en\")\n" +
            "        club: refNodes(name: \"club\") {\n" +
            "            name: value(name: \"jcr:title\",language:\"en\")\n" +
            "            clubDescription: value(name: \"clubDescription\",language:\"en\")\n" +
            "            code: value(name: \"code\", language:\"en\")\n" +
            "}\n" +
            "        name: value(name: \"name\" language:\"en\")\n" +
            "        nodename:value(name:\"j:nodename\")\n" +
            "        description: value(name: \"description\", language:\"en\")\n" +
            "        longDescription: value(name: \"longDescription\", language:\"en\")\n" +
            "        slug: value(name: \"slug\")\n" +
            "        universalPropertyCode: value(name: \"universalPropertyCode\")\n" +
            "        flags: refNodes(name: \"flags\") {\n" +
            "          name: value(name: \"name\")\n" +
            "          nodename:value(name:\"j:nodename\")\n" +
            "          }\n" +
            "        dcmBrand: refNode(name: \"dcmBrand\") {\n" +
            "          name: value(name: \"name\")\n" +
            "          nodename:value(name:\"j:nodename\")\n" +
            "          images: refNodes(name: \"images\") {\n" +
            "            name: value(name: \"jcr:title\", language:\"en\")\n" +
            "            nodename: value(name: \"j:nodename\")\n" +
            "            altText: value(name: \"altText\", language:\"en\")\n" +
            "            photo: refNodes(name: \"photo\") {\n" +
            "              name\n" +
            "              path\n" +
            "              width: value(name: \"j:width\")\n" +
            "              ratio: value(name: \"ratio\")\n" +
            "              height: value(name: \"j:height\")\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "        path\n" +
            "        address1 :value(name:\"address1\", language:\"en\")\n" +
            "        address2 :value(name:\"address2\", language:\"en\")\n" +
            "        city: value(name:\"city\", language:\"en\")\n" +
            "        state:value(name:\"state\", language:\"en\")\n" +
            "        country:value(name:\"country\", language:\"en\")\n" +
            "        continent:value(name:\"continent\", language:\"en\")\n" +
            "        region:value(name:\"region\", language:\"en\")\n" +
            "        phoneMain:value(name:\"phoneMain\")\n" +
            "        accessibility: values(name:\"accessibility\")\n" +
            "        announcements:values(name:\"announcements\")\n" +
            "        policies:refNode(name: \"policies\") {\n" +
            "          path\n" +
            "        }\n" +
            "        twitter:value(name:\"twitter\")\n" +
            "        facebook:value(name:\"facebook\")\n" +
            "        instagram:value(name:\"instagram\")\n" +
            "        pinterest:value(name:\"pinterest\")\n" +
            "        youtube:value(name:\"youtube\")\n" +
            "        tripadvisor:value(name:\"tripadvisor\")\n" +
            "        tripAdvisorId:value(name:\"tripAdvisorId\")\n" +
            "        videos: refNodes(name: \"videos\") {\n" +
            "          name: value(name: \"name\" language:\"en\")\n" +
            "          nodename: value(name: \"j:nodename\")\n" +
            "          altText: value(name: \"altText\" )\n" +
            "          externalRefId: value(name: \"externalRefId\")\n" +
            "          referenceId: value(name: \"referenceID\")\n" +
            "          thumbnail: refNode(name: \"thumbnail\") {\n" +
            "            path\n" +
            "            width: value(name: \"j:width\")\n" +
            "            ratio: value(name: \"ratio\")\n" +
            "            height: value(name: \"j:height\")\n" +
            "          }\n" +
            "        }\n" +
            "        images: refNodes(name: \"images\") {\n" +
            "          name: value(name: \"name\" language:\"en\")\n" +
            "          nodename: value(name: \"j:nodename\")\n" +
            "          photo: refNodes(name: \"photo\") {\n" +
            "            name\n" +
            "            path\n" +
            "            width: value(name: \"j:width\")\n" +
            "            ratio: value(name: \"ratio\")\n" +
            "            height: value(name: \"j:height\")\n" +
            "          }\n" +
            "        }\n" +
            "        documents: refNodes(name:\"documents\") {\n" +
            "          document: refNode(name: \"document\") {\n" +
            "            name: value(name: \"name\" language:\"en\")\n" +
            "            nodename: value(name: \"j:nodename\")\n" +
            "            path\n" +
            "          }\n" +
            "        }\n" +
            "        gallery: refNodes(name:\"gallery\") {\n" +
            "          images: refNodes(name: \"images\") {\n" +
            "            name: value(name: \"name\" language:\"en\")\n" +
            "            nodename: value(name: \"j:nodename\")\n" +
            "            altText: value(name: \"altText\")\n" +
            "            priority: value(name: \"priority\")\n" +
            "              photo: refNodes(name: \"photo\") {\n" +
            "              name\n" +
            "              path\n" +
            "              width: value(name: \"j:width\")\n" +
            "              ratio: value(name: \"ratio\")\n" +
            "              height: value(name: \"j:height\")\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "    awards: nodesByQuery(query: $query7) {\n" +
            "      nodes {\n" +
            "        name: value(name: \"name\", language:\"en\")\n" +
            "        nodename: value(name:\"j:nodename\")\n" +
            "        awardLink: value(name:\"awardLink\")\n" +
            "        description: value(name: \"description\", language:\"en\")\n" +
            "        priority: value(name:\"priority\")\n" +
            "        images: refNodes(name: \"images\") {\n" +
            "          name: value(name: \"jcr:title\", language:\"en\")\n" +
            "          nodename: value(name: \"j:nodename\")\n" +
            "          altText: value(name: \"altText\", language:\"en\")\n" +
            "          photo: refNodes(name: \"photo\") {\n" +
            "            name\n" +
            "            path\n" +
            "            width: value(name: \"j:width\")\n" +
            "            ratio: value(name: \"ratio\")\n" +
            "            height: value(name: \"j:height\")\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "    places: nodesByQuery(query: $query8) {\n" +
            "      nodes {\n" +
            "        name: value(name: \"name\", language:\"en\")\n" +
            "        nodename: value(name:\"j:nodename\")\n" +
            "        description: value(name: \"description\", language:\"en\")\n" +
            "      }\n" +
            "    }\n" +
            "    alerts: nodesByQuery(query: $query9) {\n" +
            "      nodes {\n" +
            "        name: value(name: \"name\", language:\"en\")\n" +
            "        nodename: value(name:\"j:nodename\")\n" +
            "        description: value(name: \"description\", language:\"en\")\n" +
            "        priority: value(name:\"priority\")\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
