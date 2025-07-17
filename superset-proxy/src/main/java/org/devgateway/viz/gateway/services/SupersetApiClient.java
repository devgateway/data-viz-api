package org.devgateway.viz.gateway.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class SupersetApiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${viz.superset.url}")
    private String supersetUrlFromProperties;

    @Value("${viz.superset.username}")
    private String supersetUsername;

    @Value("${viz.superset.password}")
    private String supersetPassword;

    private String accessToken;
    private Instant tokenExpiration;

    Logger logger = Logger.getLogger(SupersetApiClient.class.getName());

    public SupersetApiClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .build();

        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        this.objectMapper = new ObjectMapper();
        ensureValidToken();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add(HttpHeaders.ACCEPT_ENCODING, "gzip");
            request.getHeaders().add(HttpHeaders.ACCEPT, "application/json");

            // Add authorization header if token is available
            if (accessToken != null) {
                request.getHeaders().setBearerAuth(accessToken);
            }

            return execution.execute(request, body);
        });
    }

    /**
     * Login to Superset and get an access token
     */
    private void login() {
        try {
            String url = supersetUrlFromProperties + "/security/login";

            // Create login request body
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("username", supersetUsername);
            requestBody.put("password", supersetPassword);
            requestBody.put("provider", "db");
            requestBody.put("refresh", true);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

            // Make the request
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    JsonNode.class
            );

            // Extract token and set expiration (default to 1 hour if not specified)
            JsonNode responseBody = response.getBody();
            if (responseBody != null && responseBody.has("access_token")) {
                accessToken = responseBody.get("access_token").asText();
                // Set token expiration to 1 hour from now (typical default)
                tokenExpiration = Instant.now().plusSeconds(3600);
                logger.info("Successfully obtained Superset access token");
            } else {
                logger.warning("Failed to obtain Superset access token");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error logging into Superset", e);
        }
    }

    /**
     * Check if token is expired and refresh if needed
     */
    private void ensureValidToken() {
        // If token is null or expired, get a new one
        if (accessToken == null || (tokenExpiration != null && Instant.now().isAfter(tokenExpiration))) {
            logger.info("Superset token is null or expired, obtaining new token");
            login();
        }
    }


    /**
     * Fetch list of all charts
     */
    public JsonNode fetchCharts() {
        ensureValidToken();
        String url = supersetUrlFromProperties + "/chart/";
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        return response.getBody();
    }

    /**
     * Fetch list of all datasets
     */
    @Cacheable(value = "datasets")
    public JsonNode fetchDatasets() {
        ensureValidToken();
        String url = supersetUrlFromProperties + "/dataset/?force=true";
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        return response.getBody();
    }

    /**
     * Fetch a single dataset by ID
     */
    @Cacheable(value = "dataset", key = "#datasetId")
    public JsonNode fetchDataset(String datasetId) {
        logger.info("Fetching Datasets");
        if (datasetId == null || datasetId.equalsIgnoreCase("null") || datasetId.isEmpty()) {
            //return emtpy json
            return null;
        }
        ensureValidToken();
        String url = supersetUrlFromProperties + "/dataset/" + datasetId;
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        return response.getBody();
    }

    /**
     * Post a query to Superset /api/v1/chart/data
     */
    public JsonNode postChartData(JsonNode requestBody) {
        logger.info("Calling Superset API to fetch data");
        long startTime = System.currentTimeMillis();

        ensureValidToken();
        String url = supersetUrlFromProperties + "/chart/data";

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, requestBody, JsonNode.class);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        logger.info("Time taken to fetch data: " + duration + " ms");

        return response.getBody();
    }

    /**
     * Get the current access token
     * @return The current access token or null if not authenticated
     */
    public String getAccessToken() {
        ensureValidToken();
        return accessToken;
    }

    /**
     * Check if the client is authenticated
     * @return true if authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return accessToken != null &&
               tokenExpiration != null &&
               Instant.now().isBefore(tokenExpiration);
    }
}
