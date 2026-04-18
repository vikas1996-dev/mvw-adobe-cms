package com.mvw.core.models.dto;


public class Thumbnail {

     private String path;

    private String width;

     private String height;

     private String ratio;

     

    public void setPath(String path) {
        this.path = path;
    }

     public void setWidth(String width) {
         this.width = width;
     }

     public void setHeight(String height) {
         this.height = height;
     }

     public void setRatio(String ratio) {
         this.ratio = ratio;
     }

    public String getPath() {
        return path;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public String getRatio() {
        return ratio;
    }

    @Override
    public String toString() {
        return "Thumbnail [path=" + path + ", width=" + width + ", height=" + height + ", ratio=" + ratio + "]";
    }

    
}
