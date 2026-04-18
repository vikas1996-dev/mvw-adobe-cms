package com.mvw.core.models.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResortInfoItem {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResortInfoItem.class);

    private String name;        // from headertext.description
    private String description; // from htmlcontent.description
private String icon;


    public String getIcon() {
    return icon;
}

public void setIcon(String icon) {
    this.icon = icon;
}

    public ResortInfoItem() {}

    public ResortInfoItem(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        LOGGER.debug("ResortInfoItem getName(): {}", name);
        return name;
    }

    public void setName(String name) {
        this.name = name;
        
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    

    /* =========================
       Policy helper methods
       ========================= */

    public boolean isServiceAnimalPolicy() {
       
        return name != null && name.toLowerCase().contains("service animal");
    }

    public boolean isCashlessResort() {
       
        return name != null && name.toLowerCase().contains("cashless");
    }

     public boolean isVacationOwnerShip() {
       
        return name != null && name.toLowerCase().contains("vacation-ownership-header");
    }
    @Override
    public String toString() {
        return "ResortInfoItem [name=" + name + ", description=" + description + "]";
    }

}