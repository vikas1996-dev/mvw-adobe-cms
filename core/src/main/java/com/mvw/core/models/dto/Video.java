package com.mvw.core.models.dto;

public class Video {

    private String name;

    private String nodename;

    public void setName(String name) {
        this.name = name;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public void setExternalRefId(String externalRefId) {
        this.externalRefId = externalRefId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    private String altText;

    private String externalRefId;

    private String referenceId;

    private Thumbnail thumbnail;

    public String getName() {
        return name;
    }

    public String getNodename() {
        return nodename;
    }

    public String getAltText() {
        return altText;
    }

    public String getExternalRefId() {
        return externalRefId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    @Override
    public String toString() {
        return "Video [name=" + name + ", nodename=" + nodename + ", altText=" + altText + ", externalRefId="
                + externalRefId + ", referenceId=" + referenceId + ", thumbnail=" + thumbnail + "]";
    }


    
}
