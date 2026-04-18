// package com.mvw.core.models;

// import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.mvw.core.models.dto.Promotions;
// import com.mvw.core.models.dto.ResortDto;
// import com.mvw.core.models.dto.ResortPromotionsResponseDto;

// import org.apache.sling.api.SlingHttpServletRequest;
// import org.apache.sling.models.annotations.DefaultInjectionStrategy;
// import org.apache.sling.models.annotations.Model;
// import org.apache.sling.models.annotations.injectorspecific.SlingObject;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// import java.util.Collections;
// import java.util.List;

// @Model(
//         adaptables = SlingHttpServletRequest.class,
//         adapters = FullResortsListModel.class,
//         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
// )
// public class FullResortsListModel {

//     private static final Logger LOGGER = LoggerFactory.getLogger(FullResortsListModel.class);

//     @SlingObject
//     private SlingHttpServletRequest request;

//     public ResortPromotionsResponseDto getPropertiesList() {
//   LOGGER.info("FullResortsListModel mapped");
//         try {
//             Object jahiaResponseObj = request.getAttribute("ResortList");

//             if (!(jahiaResponseObj instanceof JsonNode)) {
//                 LOGGER.warn("jahiaResponse attribute is missing or not a JsonNode");
//                 return null;
//             }

//             JsonNode jsonResponse = (JsonNode) jahiaResponseObj;
//             LOGGER.info("Received Jahia response for full list {}", jsonResponse);

//             JsonNode propertiesNodesArray = jsonResponse
//                     .path("data")
//                     .path("jcr")
//                     .path("propertiesFolderNode")
//                     .path("propertiesRoot")
//                     .path("nodes");
//  JsonNode propertiesNodesArray1 = jsonResponse
//                     .path("data")
//                     .path("jcr")
//                     .path("promotions")
//                     .path("nodes");
//             if (propertiesNodesArray.isMissingNode() || !propertiesNodesArray.isArray()) {
//                 LOGGER.warn("Full resort List is missing");
//                 return null;
//             }

//             ObjectMapper mapper = new ObjectMapper();
//             List<ResortDto> propertiesList = mapper.readValue(
//                     propertiesNodesArray.toString(),
//                     new TypeReference<List<ResortDto>>() {}
//             );
// List<Promotions> propertiesList1 = mapper.readValue(
//                     propertiesNodesArray1.toString(),
//                     new TypeReference<List<Promotions>>() {}
//             );
//             LOGGER.info("Successfully mapped {}", propertiesList1);
//             ResortPromotionsResponseDto responseDto =
//         new ResortPromotionsResponseDto(propertiesList, null);
//             return responseDto;

//         } catch (Exception e) {
//             LOGGER.error("Error while mapping Jahia response", e);
//         }

//         return null;
//     }
// }