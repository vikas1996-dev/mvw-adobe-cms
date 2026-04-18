package com.mvw.core.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResortListDto {
    private String name;
    private String continent;
    private String country;
    private String region;
    private String state;
    private String city;
    private String slug;
    private Tags dcmBrand;
    

    public Tags getDcmBrand() {
        return dcmBrand;
    }

    @JsonProperty("dcmBrand")
    public void setDcmBrand(Tags dcmBrand) {
        this.dcmBrand = dcmBrand;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    private LocaleInfo locale;
    private LocaleInfo subLocale;

   
    public LocaleInfo getLocale() {
        return locale;
    }

    @JsonProperty("locale")
    public void setLocale(LocaleInfo locale) {
        this.locale = locale;
    }

    
    public LocaleInfo getSubLocale() {
        return subLocale;
    }

    @JsonProperty("subLocale")
    public void setSubLocale(LocaleInfo subLocale) {
        this.subLocale = subLocale;
    }

    // ---------------- INNER CLASSES ----------------

    public static class LocaleInfo {
        private String name;
        private String priority;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

}
