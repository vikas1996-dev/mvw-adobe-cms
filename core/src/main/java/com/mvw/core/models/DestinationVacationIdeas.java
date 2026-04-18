package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.ArticleDto;
import com.mvw.core.models.dto.Images;
import com.mvw.core.models.dto.Tags;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;
import com.mvw.core.utils.ImagePathUtils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Model(adaptables = SlingHttpServletRequest.class,
       adapters = DestinationVacationIdeas.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DestinationVacationIdeas {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SlingObject
    private SlingHttpServletRequest request;

    @OSGiService
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    private String baseImagePath;

    @PostConstruct
    protected void init() {
        baseImagePath = jahiaApiConfigServiceImpl.getApiImagePath();
    }

    public List<ArticleDto> getPropertiesList() throws IOException {
        logger.info("Fetching articles for destination...");

        JsonNode jsonResponse = (JsonNode) request.getAttribute("destinationResponse");
        String name = (String) request.getAttribute("nodeName");
        logger.info("Destination node: {}", name);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode propertiesNodesArray = jsonResponse.path("data").path("jcr").path("articles").path("nodes");

        List<ArticleDto> propertiesList = mapper.readValue(propertiesNodesArray.toString(),
                new TypeReference<List<ArticleDto>>() {});

        propertiesList = filterByDestinationNodeName(propertiesList, name);

        List<ArticleDto> top5Articles = propertiesList.stream()
                .filter(a -> a.getCreated() != null)
                .sorted(Comparator.comparing(ArticleDto::getCreated).reversed())
                .limit(5)
                .collect(Collectors.toList());

        // populate vacation ideas
        populateVacationIdeas(top5Articles);

        // prepend base path to all article images
        prependBasePathToArticleImages(top5Articles);

        return top5Articles;
    }

    private void prependBasePathToArticleImages(List<ArticleDto> articles) {
        if (articles == null || baseImagePath == null) return;

        for (ArticleDto article : articles) {
            Images images = article.getImages();
            if (images != null && images.getPhoto() != null) {
                ImagePathUtils.prependBasePath(List.of(images), baseImagePath);
            }
        }
    }

    public static List<ArticleDto> filterByDestinationNodeName(
            List<ArticleDto> articles,
            String destinationNodeName) {

        if (articles == null || destinationNodeName == null) {
            return List.of();
        }

        return articles.stream()
                .filter(article -> article.getDestinations() != null && article.getImages() != null)
                .filter(article -> article.getDestinations().stream()
                        .anyMatch(dest -> destinationNodeName.equalsIgnoreCase(dest.getNodename())))
                .collect(Collectors.toList());
    }

    public static void populateVacationIdeas(List<ArticleDto> articles) {
        if (articles == null) return;

        articles.forEach(article -> {
            if (article.getDcmTags() == null) {
                article.setVacationIdeas(List.of());
                return;
            }

            List<String> vacationIdeas = article.getDcmTags().stream()
                    .filter(tag -> tag.getParent() != null)
                    .filter(tag -> "vacation-life".equalsIgnoreCase(tag.getParent().getName()))
                    .map(Tags::getName)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            article.setVacationIdeas(vacationIdeas);
        });
    }
}