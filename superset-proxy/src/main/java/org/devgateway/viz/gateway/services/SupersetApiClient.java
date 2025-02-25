package org.devgateway.viz.gateway.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@Component
public class SupersetApiClient {

    private final RestTemplate restTemplate;

    @Value("${superset.url}")
    private String supersetUrlFromProperties;

    Logger logger = Logger.getLogger(SupersetApiClient.class.getName());

    public SupersetApiClient() {
        this.restTemplate = new RestTemplate();
    }


    /**
     * Fetch list of all charts
     */
    public JsonNode fetchCharts() {
        String url = supersetUrlFromProperties + "/api/v1/chart/";
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        return response.getBody();
    }

    /**
     * Fetch list of all datasets
     */
    public JsonNode fetchDatasets() {
        String url = supersetUrlFromProperties + "/api/v1/dataset/";
        logger.info("Fetching datasets from " + url);
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        return response.getBody();
    }

    /**
     * Fetch a single dataset by ID
     */
    public JsonNode fetchDataset(String datasetId) {
        String url = supersetUrlFromProperties + "/api/v1/dataset/" + datasetId;
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        return response.getBody();
    }

    /**
     * Post a query to Superset /api/v1/chart/data
     */
    public JsonNode postChartData(JsonNode requestBody) {
        String url = supersetUrlFromProperties + "/api/v1/chart/data";
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, requestBody, JsonNode.class);
        return response.getBody();
    }
}
