package com.mvw.core.services.impl;

import com.mvw.core.services.TripAdvisorService;
import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = TripAdvisorService.class, immediate = true)
public class TripAdvisorServiceImpl implements TripAdvisorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TripAdvisorServiceImpl.class);

    @Reference
    TripAdvisorConfigServiceImpl tripAdvisorConfigServiceImpl;

    @Override
    public String getLocationDetails(String locationId) {
        String body = "";
        try {
            String url = tripAdvisorConfigServiceImpl.getApiUrl() + "/location/" + locationId
                + "/details?language=en&currency=USD&key=" + tripAdvisorConfigServiceImpl.getApiKey();
            LOGGER.info("TripAdvisorURL {}", url);
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .GET()
                .build();

            java.net.http.HttpResponse<String> response = java.net.http.HttpClient.newHttpClient()
                .send(request,
                    java.net.http.HttpResponse.BodyHandlers.ofString());
            body = response.body();
            LOGGER.info("TripAdvisorURL response{}", body);
            return body;

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Tripadvisor API call failed", e);
        }
        return body;
    }
}
