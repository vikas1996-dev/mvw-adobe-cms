package com.mvw.core.models.dto;

import java.util.List;

public class Images {
    private String name;
    private String nodename;
    private String altText;

    //This is for Villas only.
    private String priority;
    private List<Photo> photo;
private String referenceId; 
private DefaultModel referenceID;

private Flags assetTypeRef;
private String externalRefId;
private Thumbnail thumbnail;
private boolean isVideo;



    public void setName(String name) {
    this.name = name;
}

public void setNodename(String nodename) {
    this.nodename = nodename;
}

public void setAltText(String altText) {
    this.altText = altText;
}

public void setPriority(String priority) {
    this.priority = priority;
}

public void setPhoto(List<Photo> photo) {
    this.photo = photo;
}

public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
}

    public String getName() {
        return name;
    }

    public String getNodename() {
        return nodename;
    }

    public String getAltText() {
        return altText;
    }

    public String getPriority() {
        return priority;
    }

    public List<Photo> getPhoto() {
        return photo;
    }

    public String getReferenceId() {
        return referenceId;
    }

   

    public DefaultModel getReferenceID() {
        return referenceID;
    }

    public void setReferenceID(DefaultModel referenceID) {
        this.referenceID = referenceID;
    }

    public Flags getAssetTypeRef() {
        return assetTypeRef;
    }

    public void setAssetTypeRef(Flags assetTypeRef) {
        this.assetTypeRef = assetTypeRef;
    }

    public String getExternalRefId() {
        return externalRefId;
    }

    public void setExternalRefId(String externalRefId) {
        this.externalRefId = externalRefId;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isVideo() {
        if(externalRefId!=null)
        return true;
    else
        return false;
    }

    public void setVideo(boolean isVideo) {
        this.isVideo = isVideo;
    }

    @Override
    public String toString() {
        return "Images [name=" + name + ", nodename=" + nodename + ", altText=" + altText + ", priority=" + priority
                + ", photo=" + photo + ", referenceId=" + referenceId + ", referenceID=" + referenceID
                + ", assetTypeRef=" + assetTypeRef + ", externalRefId=" + externalRefId + ", thumbnail=" + thumbnail
                + ", isVideo=" + isVideo + "]";
    }

    
    
}
