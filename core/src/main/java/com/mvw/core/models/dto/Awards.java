package com.mvw.core.models.dto;

import java.util.List;

public class Awards {
    private String name;
    private String nodename;
    private String awardLink;
    private String description;
    private String priority;
    private List<Images> images;

    public String getName() {
        return name;
    }

    public String getNodename() {
        return nodename;
    }

    public String getAwardLink() {
        return awardLink;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public List<Images> getImages() {
        return images;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public void setAwardLink(String awardLink) {
        this.awardLink = awardLink;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Awards [name=" + name + ", nodename=" + nodename + ", awardLink=" + awardLink + ", description="
                + description + ", priority=" + priority + ", images=" + images + "]";
    }

}
