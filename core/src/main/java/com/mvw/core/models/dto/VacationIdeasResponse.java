package com.mvw.core.models.dto;

import java.util.List;

public class VacationIdeasResponse {
    private List<ArticleDto> articles;
    private List<String> vacationIdeas;
    public List<ArticleDto> getArticles() {
        return articles;
    }
    public void setArticles(List<ArticleDto> articles) {
        this.articles = articles;
    }
    public List<String> getVacationIdeas() {
        return vacationIdeas;
    }
    public void setVacationIdeas(List<String> vacationIdeas) {
        this.vacationIdeas = vacationIdeas;
    }

    
}
