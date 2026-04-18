package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.Dining;
import com.mvw.core.models.dto.ResortInfoItem;
import com.mvw.core.models.dto.ResortInfoMapper;
import com.mvw.core.models.dto.DefaultModel;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = ResortSmokingModel.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ResortSmokingModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResortSmokingModel.class);

    @SlingObject
    private SlingHttpServletRequest request;

    public List<ResortInfoItem> getResortInfoItems() {
    JsonNode textFieldsNode = request.getAttribute("jahiaResponse") != null
            ? ((JsonNode) request.getAttribute("jahiaResponse"))
                    .path("data")
                    .path("jcr")
                    .path("defaultSmokingPolicy")
                    .path("copy")
                    .path("textFields")
            : null;

    return ResortInfoMapper.mapHeaderAndHtml(textFieldsNode);
}

}
