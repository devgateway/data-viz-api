package org.devgateway.viz.gateway.services.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.logging.Logger;

@Component
public class SuperSetClient {

    private final Logger logger = Logger.getLogger(SuperSetClient.class.getName());

    @Value("${viz.superset.url}")
    private final String supersetUrlFromProperties;

    private final RestTemplate restTemplate;

    //TODO: add constructor initiating restTemplate and httpClient
    public SuperSetClient(@Value("${viz.superset.url}") String supersetUrlFromProperties) {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);
        cm.setDefaultMaxPerRoute(50);

        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(cm)
                .build();

        HttpComponentsClientHttpRequestFactory httpClient = new HttpComponentsClientHttpRequestFactory(client);
        this.restTemplate = new RestTemplate(httpClient);
        this.supersetUrlFromProperties = supersetUrlFromProperties;
        restTemplate.getInterceptors().add((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();

            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set(HttpHeaders.ACCEPT_ENCODING, "gzip");
            headers.set(HttpHeaders.CACHE_CONTROL, "max-age=0");

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
    @Cacheable("superset-datasets")
    public JsonNode fetchDatasets() {
        logger.info("Fetching Datasets");
        String url = supersetUrlFromProperties + "/api/v1/dataset/?force=true";
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        return response.getBody();
    }

    /**
     * Fetch a single dataset by ID
     */
    @Cacheable("superset-dataset")
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

    @Cacheable("superset-chart-data")
    public JsonNode postChartData(JsonNode requestBody) {
        logger.info("Posting chart data to Superset API");
        String datasourceId = requestBody.get("datasource").get("id").asText();
        logger.info("Calling Superset API to fetch data for datasource ID:" + datasourceId);

        String submitUrl = supersetUrlFromProperties + "/api/v1/chart/data";

        ResponseEntity<JsonNode> submitResponse = restTemplate.postForEntity(submitUrl, requestBody, JsonNode.class);

        if (submitResponse.getStatusCode() != HttpStatus.OK && submitResponse.getStatusCode() != HttpStatus.ACCEPTED) {
            throw new RuntimeException("Failed to submit query: " + submitResponse.getStatusCode());
        }

        JsonNode responseBody = submitResponse.getBody();
        if (responseBody.has("result")) {
            return responseBody;
        } else {
            throw new RuntimeException("Async fetching from Superset not supported.");
        }
    }
}
