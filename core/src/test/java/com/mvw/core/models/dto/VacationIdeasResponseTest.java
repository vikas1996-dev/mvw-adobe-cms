package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VacationIdeasResponseTest {

    private VacationIdeasResponse response;

    @BeforeEach
    void setUp() {
        response = new VacationIdeasResponse();
    }

    @Test
    void testSetAndGetArticles() {
        List<ArticleDto> articles = new ArrayList<>();
        ArticleDto article = new ArticleDto();
        article.setName("Beach Vacation Tips");
        articles.add(article);
        
        response.setArticles(articles);
        
        assertEquals(articles, response.getArticles());
        assertEquals(1, response.getArticles().size());
    }

    @Test
    void testSetAndGetVacationIdeas() {
        List<String> ideas = new ArrayList<>();
        ideas.add("Beach");
        ideas.add("Golf");
        ideas.add("Spa");
        
        response.setVacationIdeas(ideas);
        
        assertEquals(ideas, response.getVacationIdeas());
        assertEquals(3, response.getVacationIdeas().size());
    }

    @Test
    void testNullValues() {
        assertNull(response.getArticles());
        assertNull(response.getVacationIdeas());
    }
}
