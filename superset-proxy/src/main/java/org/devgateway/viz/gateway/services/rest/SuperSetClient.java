package org.devgateway.viz.gateway.services.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    public JsonNode fetchDatasets() {
        logger.info("Fetching Datasets");
        String url = supersetUrlFromProperties + "/api/v1/dataset/?force=true";
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        return response.getBody();
    }

    /**
     * Fetch a single dataset by ID
     */
    @Cacheable(value = "dataset", key = "***REMOVED***datasetId")
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
 /*
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
*/
    public JsonNode postChartData(JsonNode requestBody) {
        logger.info("Calling Superset API to fetch data (async-aware)");
        long startTime = System.currentTimeMillis();

        String submitUrl = supersetUrlFromProperties + "/api/v1/chart/data";

        logger.info("Request body: " + requestBody.toString());
        ResponseEntity<JsonNode> submitResponse = restTemplate.postForEntity(submitUrl, requestBody, JsonNode.class);

        if (submitResponse.getStatusCode() != HttpStatus.OK && submitResponse.getStatusCode() != HttpStatus.ACCEPTED) {
            throw new RuntimeException("Failed to submit query: " + submitResponse.getStatusCode());
        }

        JsonNode submitBody = submitResponse.getBody();

        // CASE 1: Superset returned result immediately (from cache or fast query)
        if (submitBody.has("result")) {
            logger.info("Received result immediately (likely from cache).");
            return submitBody;
        }

        // CASE 2: Async job was created, need to poll
        String jobId = submitBody.path("job_id").asText();
        if (jobId == null || jobId.isEmpty()) {
            throw new RuntimeException("No job_id received and no result present.");
        }

        String resultUrl = supersetUrlFromProperties + "/api/v1/chart/data/" + jobId + "/result";

        int maxRetries = 10;
        int delayMs = 1000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            logger.info("Polling for result (attempt " + attempt + ")");

            ResponseEntity<JsonNode> resultResponse = restTemplate.getForEntity(resultUrl, JsonNode.class);

            if (resultResponse.getStatusCode() == HttpStatus.OK) {
                JsonNode resultBody = resultResponse.getBody();
                if (resultBody != null && resultBody.has("result")) {
                    logger.info("Async result is ready.");
                    return resultBody;
                }
            }

            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Polling interrupted", e);
            }
        }

        throw new RuntimeException("Timed out waiting for async result from Superset.");
    }


}
