package com.mvw.core.models.dto;

import java.util.List;

public class DcmBrand {
    private String name;
    private String nodename;
    private List<Images> images;

    public String getName() {
        return name;
    }

    public String getNodename() {
        return nodename;
    }

    public List<Images> getImages() {
        return images;
    }
}
