package com.mvw.core.models.dto;

import java.util.List;


public class Destinations {
    private String name;
    private String nodename;
    private List<Images> images;
    private Region region;
    private int resortCount;

    // --- getters/setters ---
    public int getResortCount() {
        return resortCount;
    }

    public void setResortCount(int resortCount) {
        this.resortCount = resortCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }


}
