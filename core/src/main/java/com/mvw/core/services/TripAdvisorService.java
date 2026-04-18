package com.mvw.core.services;

public interface TripAdvisorService {

    /**
     * Fetch Tripadvisor location details
     *
     * @param locationId Tripadvisor location ID
     * @return API response as JSON string
     */
    String getLocationDetails(String locationId);
}
 