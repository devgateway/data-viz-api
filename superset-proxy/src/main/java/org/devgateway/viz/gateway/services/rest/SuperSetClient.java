package org.devgateway.viz.gateway.services.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
@Component
public class SuperSetClient {

    // private final RestTemplate restTemplate;

    @Value("${viz.superset.url}")
    private final String supersetUrlFromProperties;

    private final HttpComponentsClientHttpRequestFactory httpClient;
    private final RestTemplate restTemplate;

    private final AtomicReference<String> lastId = new AtomicReference<>("0");

    //TODO: add constructor initiating restTemplate and httpClient
    public SuperSetClient(@Value("${viz.superset.url}") String supersetUrlFromProperties) {
        this.httpClient = new HttpComponentsClientHttpRequestFactory(HttpClients.custom().build());
        this.restTemplate = new RestTemplate(httpClient);
        this.supersetUrlFromProperties = supersetUrlFromProperties;
        restTemplate.getInterceptors().add((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();

            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set(HttpHeaders.ACCEPT_ENCODING, "gzip");
            headers.set(HttpHeaders.CACHE_CONTROL, "max-age=0");

            return execution.execute(request, body);
        });
        this.login();
    }


    private HashMap<String, String> login() {
        try {


            // 1. Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<Void> request = new HttpEntity<>(headers);


            // 2. Create RestTemplate with custom request factory
            // 2. Send GET request to CSRF endpoint
            ResponseEntity<Map> response = restTemplate.exchange(
                    supersetUrlFromProperties + "/api/v1/security/csrf_token/",
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            // 3. Extract CSRF token from JSON body
            if (response.getStatusCode() == HttpStatus.OK) {
                final String csrfToken;
                final String cookies;
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("result")) {
                    csrfToken = (String) responseBody.get("result");

                } else {
                    csrfToken = null;
                }

                // 4. Extract cookies from headers
                List<String> setCookie = response.getHeaders().get(HttpHeaders.SET_COOKIE);
                if (setCookie != null) {
                    cookies = String.join("; ", setCookie);


                } else {
                    cookies = null;
                }

                restTemplate.getInterceptors().add((pRequest, body, execution) -> {
                    HttpHeaders postHeaders = pRequest.getHeaders();
                    postHeaders.set("X-CSRFToken", csrfToken);
                    postHeaders.set(HttpHeaders.COOKIE, cookies);
                    return execution.execute(pRequest, body);
                });

            } else {
                System.out.println("Failed to get CSRF token: " + response.getStatusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    Logger logger = Logger.getLogger(SuperSetClient.class.getName());


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

  /*public JsonNode postChartData(JsonNode requestBody) {
        logger.info("Calling Superset API to fetch data");
        long startTime = System.currentTimeMillis();

        String url = supersetUrlFromProperties + "/api/v1/chart/data";
        setHeaders(restTemplate);

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, requestBody, JsonNode.class);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        logger.info("Time taken to fetch data: " + duration + " ms");

        return response.getBody();
    }*/
    public JsonNode postChartData(JsonNode requestBody) {


        String datasourceId = requestBody.get("datasource").get("id").asText();
        logger.info("Calling Superset API to fetch data (async-aware) DS ID:" + datasourceId);

        String submitUrl = supersetUrlFromProperties + "/api/v1/chart/data";

        //logger.info("Request body: " + requestBody.toString());

        ResponseEntity<JsonNode> submitResponse = restTemplate.postForEntity(submitUrl, requestBody, JsonNode.class);

        if (submitResponse.getStatusCode() != HttpStatus.OK && submitResponse.getStatusCode() != HttpStatus.ACCEPTED) {

            throw new RuntimeException("Failed to submit query: " + submitResponse.getStatusCode());
        }

        JsonNode submitBody = submitResponse.getBody();
        // CASE 1: Superset returned result immediately (from cache or fast query)
        if (submitBody.has("result")) {
            logger.info("Received result immediately (likely from cache).");
            return submitBody;

        } else {

            // CASE 2: Superset returned async query ID
            String channelId = submitBody.get("channel_id").asText();
            logger.info("Waiting for async result from Superset. Channel ID: " + channelId);

            String job_id = submitBody.get("job_id").asText();
            String events = supersetUrlFromProperties + "/api/v1/async_event?last_id=" + lastId.get();


            int maxRetries = 50;
            int baseDelayMs = 300;

            for (int attempt = 1; attempt <= maxRetries; attempt++) {

                logger.info("attempt ***REMOVED***" + attempt + "DS ID: " + datasourceId + ", Job ID:" + job_id);
                logger.info("Last ID --->" + lastId.get());

                JsonNode results = restTemplate.getForEntity(events, JsonNode.class).getBody();
                ArrayNode rs = (ArrayNode) results.get("result");
                final JsonNode[] cachedResults = new JsonNode[1];

                if (rs.size() == 0) {
                    logger.info("No async event responses received yet. Waiting for " + baseDelayMs + " ms");

                } else if (rs.size() > 0) {
                    logger.info("Result size: " + rs.size());

                    logger.info("Received async event responses: " + rs.size());
                    logger.info("looking for job id" + job_id);

                    rs.elements().forEachRemaining(e -> {
                        if (e.get("job_id").asText().equals(job_id)) {

                            if (e.get("status").asText().equalsIgnoreCase("done")) {
                                logger.info("Async query completed successfully." + "DS ID: " + datasourceId + ", Job ID:" + job_id);
                                String finalResultURL = e.get("result_url").asText();

                                lastId.set(e.get("id").asText());
                                //lastId = e.get("id").asText();

                                cachedResults[0] = restTemplate.getForEntity(supersetUrlFromProperties + finalResultURL, JsonNode.class).getBody();

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

                int delayMs = baseDelayMs + (attempt * 100);

                try {
                    logger.info("Waiting for " + delayMs + " ms before next attempt. " + channelId);
                    Thread.sleep(delayMs);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Polling interrupted", e);
                }
            }
            logger.info("Timeout while waiting for async result. DS ID:" + datasourceId + " Job ID:" + job_id);
            //TODO create json node with empty results
            return null;
            // throw new RuntimeException("Timeout while waiting for async result. DS ID:" + datasourceId + " Job ID:" + job_id);

        }

    }


}
