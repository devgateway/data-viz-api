package org.devgateway.viz.gateway.services.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@Component
public class SuperSetClient {

    private final RestTemplate restTemplate;

    @Value("${viz.superset.url}")
    private String supersetUrlFromProperties;

    Logger logger = Logger.getLogger(SuperSetClient.class.getName());

    public SuperSetClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .build();

        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

        restTemplate.getInterceptors().add((request, body, execution) -> {

            HttpHeaders headers = request.getHeaders();
            headers.add(HttpHeaders.ACCEPT_ENCODING, "gzip");
            headers.add(HttpHeaders.ACCEPT, "application/json");
            headers.add(HttpHeaders.CACHE_CONTROL, "max-age=0");

            return execution.execute(request, body);
        });
    }

    /**
     * Fetch list of all charts
     */
    public JsonNode fetchCharts() {
        logger.info("Fetching Charts");
        String url = supersetUrlFromProperties + "/api/v1/chart/";
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        return response.getBody();
    }

    /**
     * Fetch list of all datasets
     */
    //@Cacheable(value = "datasets")
    public JsonNode fetchDatasets() {
        logger.info("Fetching Datasets");
        String url = supersetUrlFromProperties + "/api/v1/dataset/?force=true";
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
        String url = supersetUrlFromProperties + "/api/v1/dataset/" + datasetId + "";
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        return response.getBody();
    }

    /**
     * Post a query to Superset /api/v1/chart/data
     */
    public JsonNode postChartData(JsonNode requestBody) {
        logger.info("Calling Superset API to fetch data");
        long startTime = System.currentTimeMillis();

        String url = supersetUrlFromProperties + "/api/v1/chart/data";

        logger.info("requestBody: " + requestBody.toString());
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, requestBody, JsonNode.class);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        logger.info("Time taken to fetch data: " + duration + " ms");

        return response.getBody();
    }
}
