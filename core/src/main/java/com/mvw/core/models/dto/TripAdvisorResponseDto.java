package com.mvw.core.models.dto;

import java.util.List;

public class TripAdvisorResponseDto {
    private Integer count;
    private List<TripAdvisorDto> list;
    public Integer getCount() {
        return count;
    }
    public void setCount(Integer count) {
        this.count = count;
    }
    public List<TripAdvisorDto> getList() {
        return list;
    }
    public void setList(List<TripAdvisorDto> list) {
        this.list = list;
    }

    
}
