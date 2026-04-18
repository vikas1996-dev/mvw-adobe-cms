package com.mvw.core.models.dto;

import java.util.List;

public class ArticleDto {

    private String name;
    private String nodename;
    private String created;
    private String shortDescription;
    private String url;

    private List<Tags> dcmTags;
    private List<Destination> destinations;
    private Images images; // nullable in your JSON
private List<String> vacationIdeas;


    // ---- getters & setters ----

    public String getName() {
        return name;
    }

    public String getNodename() {
        return nodename;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getUrl() {
        return url;
    }

    public List<Tags> getDcmTags() {
        return dcmTags;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    public Images getImages() {
        return images;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDcmTags(List<Tags> dcmTags) {
        this.dcmTags = dcmTags;
    }

    public void setDestinations(List<Destination> destinations) {
        this.destinations = destinations;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "ArticleDto [name=" + name + ", nodename=" + nodename + ", shortDescription=" + shortDescription
                + ", url=" + url + ", dcmTags=" + dcmTags + ", destinations=" + destinations + ", images=" + images
                + "]";
    }

    public List<String> getVacationIdeas() {
        return vacationIdeas;
    }

    public void setVacationIdeas(List<String> vacationIdeas) {
        this.vacationIdeas = vacationIdeas;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    
}
 
