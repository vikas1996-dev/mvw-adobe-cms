package com.mvw.core.models.dto;

import java.util.List;

public class Destination {

    private String nodename;
    private String title;
    private List<String> structuredContent;

    public String getNodename() {
        return nodename;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getStructuredContent() {
        return structuredContent;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStructuredContent(List<String> structuredContent) {
        this.structuredContent = structuredContent;
    }
}
