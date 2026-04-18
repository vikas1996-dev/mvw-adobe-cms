package com.mvw.core.models.dto;

import java.util.List;

public class Activities {
    private String name;
    private String nodename;
    private List<Images> images;
    private String description;
    private String longDescription;
    private Object shortDescription;
    private Object icon;
    private String priority;
    private String activityType;
    private Object activityCategories;
    private String phone;
    private Object hours;

    public String getName() {
        return name;
    }

    public String getNodename() {
        return nodename;
    }

    public List<Images> getImages() {
        return images;
    }

    public String getDescription() {
        return description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public Object getShortDescription() {
        return shortDescription;
    }

    public Object getIcon() {
        return icon;
    }

    public String getPriority() {
        return priority;
    }

    public String getActivityType() {
        return activityType;
    }

    public Object getActivityCategories() {
        return activityCategories;
    }

    public String getPhone() {
        return phone;
    }

    public Object getHours() {
        return hours;
    }
}
