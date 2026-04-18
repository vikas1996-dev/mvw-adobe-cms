package com.mvw.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.constants.AppConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ContentFragmentUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Map<String, Object> getTripAdvisorContentFragmentData(ResourceResolver resolver, String parentFolderPath, List<String> keys) {
        Resource parentFolderRes = resolver.getResource(parentFolderPath);
        Iterable<Resource> childContent = Optional.ofNullable(parentFolderRes).map(Resource::getChildren).orElse(Collections.emptyList());

        ArrayNode tripAdvisorArray = MAPPER.createArrayNode();

        for (Resource eachChildCf: childContent) {
            ObjectNode eachTripAdvisorObjNode = getDataForEachTripAdvisor(resolver, eachChildCf, keys);
            if (!eachTripAdvisorObjNode.isEmpty()) {
                tripAdvisorArray.add(eachTripAdvisorObjNode);
            }
        }
        ObjectNode tripAdvisorFinalNode = MAPPER.createObjectNode();
        tripAdvisorFinalNode.set("tripAdvisorData", tripAdvisorArray);

        return MAPPER.convertValue(tripAdvisorFinalNode, new TypeReference<Map<String, Object>>() {});
    }

    private static ObjectNode getDataForEachTripAdvisor(ResourceResolver resolver,
                                               Resource childContentFragmentRes, List<String> keys) {

        return Optional.ofNullable(childContentFragmentRes)
            .map(Resource::getPath)
            .map(path -> path.concat("/jcr:content/data/master"))
            .map(resolver::getResource)
            .map(Resource::getValueMap)
            .map(vm -> buildJsonFromValueMap(vm, keys))
            .orElseGet(MAPPER::createObjectNode);
    }

    private static ObjectNode buildJsonFromValueMap(ValueMap valueMap, List<String> keys) {
        String tripAdvisorId = valueMap.get(AppConstants.TRIPADVISOR_CF_MODEL_KEY_TRIP_ADVISOR_ID, StringUtils.EMPTY);
        ObjectNode json = MAPPER.createObjectNode();
        if (StringUtils.isNotEmpty(tripAdvisorId)) {
            keys.forEach(eachKey -> json.put(eachKey, valueMap.get(eachKey, StringUtils.EMPTY)));
        }

        return json;
    }
}
