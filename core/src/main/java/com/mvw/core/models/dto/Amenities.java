package com.mvw.core.models.dto;

import java.util.List;

public class Amenities {
    private String name;
    private String nodename;
    private String shortTitle;
    private String description;
    private String priority;
    private String icon;
    private String mvcsIcon;
    private String featured;
 private List<Images> images;


    public List<Images> getImages() {
        return images;
    }
    public String getName() {
        return name;
    }

    public String getNodename() {
        return nodename;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public String getIcon() {
        return icon;
    }

    public String getMvcsIcon() {
        return mvcsIcon;
    }

    public String getFeatured() {
        return featured;
    }
    @Override
    public String toString() {
        return "Amenities [name=" + name + ", nodename=" + nodename + ", shortTitle=" + shortTitle + ", description="
                + description + ", priority=" + priority + ", icon=" + icon + ", mvcsIcon=" + mvcsIcon + ", featured="
                + featured + ", images=" + images + "]";
    }
    
   
    
}
