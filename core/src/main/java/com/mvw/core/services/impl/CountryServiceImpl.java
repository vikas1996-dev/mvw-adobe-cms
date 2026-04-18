package com.mvw.core.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.CountryDTO;
import com.mvw.core.services.CountryService;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.util.EntityUtils;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component(service = CountryService.class, immediate = true)
public class CountryServiceImpl implements CountryService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CountryServiceImpl.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Reference
    private MuleConfigServiceImpl muleConfigService;

    @Override
    public List<CountryDTO> getCountries() {

        String apiUrl = muleConfigService.getApiUrl();
        return fetchData(apiUrl);
    }

    @Override
    public List<CountryDTO> getStates(String countryCode) {
        // Encode the user input to prevent URL injection
        String safeCountryCode = URLEncoder.encode(countryCode, StandardCharsets.UTF_8);

        String apiUrl = muleConfigService.getApiUrl();

        String url = (safeCountryCode == null || safeCountryCode.isEmpty())
            ? apiUrl
            : apiUrl + "/" + safeCountryCode + "/states";

        return fetchData(url);
    }

    /**
     * Fetch data from external API.
     */
    private List<CountryDTO> fetchData(String url) {

        String correlationId = UUID.randomUUID().toString();
        String jwtToken = muleConfigService.getJwtToken();

        LOGGER.info("CorrelationId: {} | Calling API: {}", correlationId, url);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(8000)
                .build();

        try (CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build()) {

            HttpGet request = new HttpGet(url);
            if (jwtToken != null && !jwtToken.isEmpty()) {
                request.setHeader("AXIS-AUTH", jwtToken);
            }
            request.setHeader("x-correlation-id", correlationId);

            try (CloseableHttpResponse response = client.execute(request)) {

                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = response.getEntity() != null
                        ? EntityUtils.toString(response.getEntity())
                        : "";

                switch (statusCode) {

                    case 200:
                        CountryDTO[] result =
                                objectMapper.readValue(responseBody, CountryDTO[].class);

                        LOGGER.info("CorrelationId: {} | Successfully fetched {} records",
                                correlationId, result.length);

                        return Arrays.asList(result);

                    case 400:
                        LOGGER.warn("CorrelationId: {} | 400 Bad request from API. Response: {}",
                                correlationId, responseBody);
                        return Collections.emptyList();

                    case 404:
                        LOGGER.warn("CorrelationId: {} | 404 Not found from API",
                                correlationId);
                        return Collections.emptyList();

                    default:
                        if (statusCode >= 500) {
                            LOGGER.error("CorrelationId: {} | Server error from API. Status: {}",
                                    correlationId, statusCode);
                        } else {
                            LOGGER.warn("CorrelationId: {} | Unexpected status code: {}",
                                    correlationId, statusCode);
                        }
                        return Collections.emptyList();
                }

            }

        } catch (ConnectTimeoutException | ConnectException e) {
            LOGGER.error("CorrelationId: {} | Unable to connect to API: {}", correlationId, e.getMessage());
        } catch (SocketTimeoutException e) {
            LOGGER.error("CorrelationId: {} | API response timeout: {}", correlationId, e.getMessage());
        } catch (IOException e) {
            LOGGER.error("CorrelationId: {} | IO Exception calling API: {}", correlationId, e.getMessage());
        }

        return Collections.emptyList();
    }
}