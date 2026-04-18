package com.mvw.core.models.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

public class ResortInfoMapper {

    /**
     * Converts a flat JSON array of headertext/htmlcontent into a list of ResortInfoItem
     */
    public static List<ResortInfoItem> mapHeaderAndHtml(JsonNode textFieldsNode) {
        if (textFieldsNode == null || !textFieldsNode.isArray()) {
            return Collections.emptyList();
        }

        // key: folderName (between resort-information and nodename), value: headertext/htmlcontent
        Map<String, String> headers = new HashMap<>();
        Map<String, String> contents = new HashMap<>();

        for (JsonNode node : textFieldsNode) {
            String path = node.path("path").asText();
            String description = node.path("description").asText();

            // Extract folder name: between "resort-information" and "headertext/htmlcontent"
            String folderName = extractFolderName(path);
            if (folderName == null) continue;

            String nodename = node.path("nodename").asText();
            if ("headertext".equals(nodename)) {
                headers.put(folderName, description);
            } else if ("htmlcontent".equals(nodename)) {
                contents.put(folderName, description);
            }
        }

        // Combine header and htmlcontent
        List<ResortInfoItem> items = new ArrayList<>();
        for (String folder : headers.keySet()) {
            String header = headers.get(folder);
            String content = contents.get(folder);
            if (content != null) { // only add if htmlcontent exists
                items.add(new ResortInfoItem(header, content));
            }
        }

        return items;
    }

    private static String extractFolderName(String path) {
        // /sites/vistana/contents/properties/KOHNLKO/welcome-kit/resort-information/trash-removal/headertext
        String marker = "/resort-information/";
        int start = path.indexOf(marker);
        if (start == -1) return null;

        start += marker.length();
        int end = path.indexOf("/", start);
        if (end == -1) return null;

        return path.substring(start, end); // returns "trash-removal"
    }
}
