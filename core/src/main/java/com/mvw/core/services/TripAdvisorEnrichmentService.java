package com.mvw.core.services;


import org.apache.sling.api.resource.ResourceResolver;

import com.mvw.core.models.dto.ResortDto;

import java.util.List;

public interface TripAdvisorEnrichmentService {

    public List<ResortDto> enrichWithTripAdvisorParallel(ResourceResolver resolver, List<ResortDto> resorts);
}
