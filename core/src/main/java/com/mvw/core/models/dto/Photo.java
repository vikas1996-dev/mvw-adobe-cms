package com.mvw.core.models.dto;

public class Photo {

    private String name;
    private String path;
    private String width;
    private String ratio;
    private String height;
    private String referenceId;
    private String altText;

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getWidth() {
        return width;
    }

    public String getRatio() {
        return ratio;
    }

    public String getHeight() {
        return height;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    @Override
    public String toString() {
        return "Photo [name=" + name +
                ", path=" + path +
                ", width=" + width +
                ", ratio=" + ratio +
                ", height=" + height +
                ", referenceId=" + referenceId +

                "]";
    }
}
