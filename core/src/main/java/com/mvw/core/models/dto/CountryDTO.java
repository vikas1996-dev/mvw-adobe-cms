package com.mvw.core.models.dto;

import java.util.List;

public class CountryDTO {

    private String country;
    private String countryCode;
    private List<StateDTO> states;

    public String getCountry() {
        return country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public List<StateDTO> getStates() {
        return states;
    }

    @Override
    public String toString() {
        return "CountryDTO [country=" + country + ", countryCode=" + countryCode + ", states=" + states + "]";
    }

    
}
