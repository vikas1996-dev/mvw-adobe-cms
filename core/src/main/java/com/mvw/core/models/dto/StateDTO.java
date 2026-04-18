package com.mvw.core.models.dto;

public class StateDTO {

    private String name;
    private String stateCode;

    public String getName() {
        return name;
    }

    public String getStateCode() {
        return stateCode;
    }

    @Override
    public String toString() {
        return "StateDTO [name=" + name + ", stateCode=" + stateCode + "]";
    }

    
}

