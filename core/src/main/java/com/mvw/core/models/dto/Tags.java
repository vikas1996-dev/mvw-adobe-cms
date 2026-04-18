package com.mvw.core.models.dto;

public class Tags {
    private String name;
    private String nodename;
    private Parent parent;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    

    @Override
    public String toString() {
        return "Tags [name=" + name + ", nodename=" + nodename + "]";
    }

    public Parent getParent() {
        return parent;
    }

    
}
