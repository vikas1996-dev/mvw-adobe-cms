package com.mvw.core.models.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class Dining {

    private String name;
    private String nodename;
    private List<Images> images;
    private String description;
    private String typeOnOffSite;
    private String typeCuisine;
    private String typeAtmosphere;
    private Object mobileOrdering;
    private String hours;
    private Object phone;
    private String priority;
    private Object urlMenu;

    /* ========= 🔥 REQUIRED FOR MODAL GALLERY ========= */
    public String getImagesJson() {
        try {
            return new ObjectMapper().writeValueAsString(images);
        } catch (Exception e) {
            return "[]";
        }
    }

    public String getName() { return name; }
    public String getNodename() { return nodename; }
    public List<Images> getImages() { return images; }
    public String getDescription() { return description; }
    public String getTypeOnOffSite() { return typeOnOffSite; }
    public String getTypeCuisine() { return typeCuisine; }
    public String getTypeAtmosphere() { return typeAtmosphere; }
    public Object getMobileOrdering() { return mobileOrdering; }
    public String getHours() { return hours; }
    public Object getPhone() { return phone; }
    public String getPriority() { return priority; }
    public Object getUrlMenu() { return urlMenu; }

    @Override
    public String toString() {
        return "Dining [name=" + name + ", nodename=" + nodename + ", images=" + images + ", description=" + description
                + ", typeOnOffSite=" + typeOnOffSite + ", typeCuisine=" + typeCuisine + ", typeAtmosphere="
                + typeAtmosphere + ", mobileOrdering=" + mobileOrdering + ", hours=" + hours + ", phone=" + phone
                + ", priority=" + priority + ", urlMenu=" + urlMenu + "]";
    }

    
}
