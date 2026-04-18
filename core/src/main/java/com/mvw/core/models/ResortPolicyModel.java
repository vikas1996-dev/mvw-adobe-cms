package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.DefaultModel;
import com.mvw.core.models.dto.Places;
import com.mvw.core.models.dto.ResortInfoItem;
import com.mvw.core.models.dto.ResortInfoMapper;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = ResortPolicyModel.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ResortPolicyModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResortPolicyModel.class);

    @SlingObject
    private SlingHttpServletRequest request;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Main method used by HTL
     */
    public List<ResortInfoItem> getResortInfoItems() {

        JsonNode jsonResponse = (JsonNode) request.getAttribute("jahiaResponse");

        if (jsonResponse == null) {
            LOGGER.warn("jahiaResponse attribute is missing");
            return Collections.emptyList();
        }

        List<ResortInfoItem> items = new ArrayList<>();

        try {
            /* ---------- PARKING (from places) ---------- */
            JsonNode placesNodes = jsonResponse
                    .path("data")
                    .path("jcr")
                    .path("places")
                    .path("nodes");

            List<Places> placesList = MAPPER.readValue(
                    placesNodes.toString(),
                    new TypeReference<List<Places>>() {}
            );

            items.addAll(getParkingInfoItems(placesList));

            /* ---------- DEFAULT SMOKING POLICY ---------- */
            JsonNode defaultSmokingNode = jsonResponse
                    .path("data")
                    .path("jcr")
                    .path("defaultSmokingPolicy")
                    .path("copy")
                    .path("textFields");

            items.addAll(ResortInfoMapper.mapHeaderAndHtml(defaultSmokingNode));

            /* ---------- SPECIFIC RESORT INFO ---------- */
            JsonNode specificResortInfoNode = jsonResponse
                    .path("data")
                    .path("jcr")
                    .path("specificResortInfo")
                    .path("copy")
                    .path("textFields");
List<ResortInfoItem> itemss =ResortInfoMapper.mapHeaderAndHtml(specificResortInfoNode);
            items.addAll(getServiceAnimalAndCashlessPolicies(itemss));
 Map<String, String> iconMap = loadIconMap();
        applyIcons(items, iconMap);
        } catch (Exception e) {
            LOGGER.error("Error building Resort Info Items", e);
        }

        return items;
    }

    /**
     * Filters parking-related places and maps them to ResortInfoItem
     */
    private List<ResortInfoItem> getParkingInfoItems(List<Places> placesList) {

        if (placesList == null || placesList.isEmpty()) {
            return Collections.emptyList();
        }

        return placesList.stream()
                .filter(place -> place.getNodename() != null)
                .filter(place -> place.getNodename().toLowerCase().contains("parking"))
                .map(place -> new ResortInfoItem(
                        place.getName(),
                        place.getDescription()
                ))
                .collect(Collectors.toList());
    }

    public List<ResortInfoItem> getServiceAnimalAndCashlessPolicies(
        List<ResortInfoItem> items) {

    if (items == null || items.isEmpty()) {
        return Collections.emptyList();
    }

    return items.stream()
            .filter(item ->
                    item.isServiceAnimalPolicy() ||
                    item.isCashlessResort()
            )
            .collect(Collectors.toList());
}

 private List<DefaultModel> getIcons() throws IOException{

        JsonNode jsonResponse = (JsonNode) request.getAttribute("staticData");
        ObjectMapper mapper = new ObjectMapper();

        JsonNode destinationsNode = jsonResponse
                .path("data")
                .path("jcr")
                .path("staticContent")
                .path("textFields")
                .path("documents");

        List<DefaultModel> propertiesList = mapper.readValue(
                destinationsNode.toString(),
                new TypeReference<List<DefaultModel>>() {}
        );

        return propertiesList;
    }

    private static final Set<String> ICON_NODE_NAMES = Set.of(
        "parking-icon",
        "smoking-icon",
        "service-animal-icon",
        "no-resort-fees-icon"
);




public Map<String, String> getIconPropertiesMap(List<DefaultModel> models) {

    if (models == null || models.isEmpty()) {
        return Collections.emptyMap();
    }

    Map<String, String> result = new HashMap<>();

    for (DefaultModel model : models) {
        if (model.getNodename() != null &&
            ICON_NODE_NAMES.contains(model.getNodename())) {

            result.put(model.getNodename(), model.getProperties());
        }
    }

    return result;
}

private Map<String, String> loadIconMap() {

    try {
        List<DefaultModel> icons = getIcons();
        Map<String, String> iconMap = getIconPropertiesMap(icons);

        LOGGER.info("Loaded icon map: {}", iconMap);

        return iconMap;
    } catch (IOException e) {
        LOGGER.error("Unable to load icon data", e);
        return Collections.emptyMap();
    }
}

private void applyIcons(
        List<ResortInfoItem> items,
        Map<String, String> iconMap) {

    if (items == null || items.isEmpty() || iconMap.isEmpty()) {
        return;
    }
for (ResortInfoItem item : items) {

    LOGGER.debug("Processing policy item: name='{}'", item.getName());

    if (item.isServiceAnimalPolicy()) {
        item.setIcon(iconMap.get("service-animal-icon"));
        LOGGER.info("Assigned SERVICE ANIMAL icon to '{}'", item.getName());

    } else if (item.isCashlessResort()) {
        item.setIcon(iconMap.get("no-resort-fees-icon"));
        LOGGER.info("Assigned CASHLESS icon to '{}'", item.getName());

    } else if (item.getName() != null &&
               item.getName().toLowerCase().contains("parking")) {

        item.setIcon(iconMap.get("parking-icon"));
        LOGGER.info("Assigned PARKING icon to '{}'", item.getName());

    } else if (item.getName() != null &&
               item.getName().toLowerCase().contains("smoking")) {

        item.setIcon(iconMap.get("smoking-icon"));
        LOGGER.info("Assigned SMOKING icon to '{}'", item.getName());

    } else {
        LOGGER.debug("No icon matched for '{}'", item.getName());
    }
}

}



}
