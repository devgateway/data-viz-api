package org.devgateway.viz.gateway.services.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

        // CASE 2: Superset returned async query ID
        String channelId = submitBody.get("channel_id").asText();
        logger.info("Waiting for async result from Superset. Channel ID: " + channelId);

        String job_id = submitBody.get("job_id").asText();

        String events = supersetUrlFromProperties + "/api/v1/async_event/";

        int maxRetries = 50;
        int baseDelayMs = 300;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {

            JsonNode results = restTemplate.getForEntity(events, JsonNode.class).getBody();

            ArrayNode rs = (ArrayNode) results.get("result");
            final JsonNode[] cachedResults = new JsonNode[1];
            if (rs.size() > 0) {
                rs.elements().forEachRemaining(e -> {
                    if (e.get("job_id").asText().equals(job_id)) {
                        logger.info("Received async event response: " + e);
                        if (e.get("status").asText().equals("done")) {

                            logger.info("Async query completed successfully. Result: " + e.get("result"));
                            String finalResultURL = e.get("result_url").asText();
                             cachedResults[0] = restTemplate.getForEntity(supersetUrlFromProperties + finalResultURL, JsonNode.class)
                                    .getBody();

                        } else if (e.get("status").asText().equals("failed")) {
                            throw new RuntimeException("Async query failed: " + e);
                        }
                    }
                });
                if (cachedResults[0] != null) {
                    logger.info("Returning Cached Results");
                    return cachedResults[0];
                }
            }

            //
            int delayMs = Math.min(1000, baseDelayMs + (attempt * 100));

            logger.info("Waiting for " + delayMs + " ms before next attempt.");
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Polling interrupted", e);
            }
        }

        // wait for results
        // This is a blocking call. You may want to implement a timeout or a non-blocking approach.

        logger.info("Waiting for async result from Superset. Channel ID: " + channelId);

        throw new RuntimeException("Timeout while waiting for async result. job_id: " + job_id);

    }


}
