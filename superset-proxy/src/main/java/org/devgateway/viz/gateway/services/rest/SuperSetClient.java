package org.devgateway.viz.gateway.services.rest;

import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.logging.Logger;

@Component
public class SuperSetClient {

    // private final RestTemplate restTemplate;

    @Value("${viz.superset.url}")
    private String supersetUrlFromProperties;

    private final HttpComponentsClientHttpRequestFactory httpClient;

    private String csrfToken;
    private String cookies;

    //TODO: add constructor initiating restTemplate and httpClient
    public SuperSetClient() {
        this.httpClient = new HttpComponentsClientHttpRequestFactory(HttpClients.custom().build());

    }

    private void addHeaders(RestTemplate restTemplate) {
        if (csrfToken == null || cookies == null) {
            login();
        }

        restTemplate.getInterceptors().add((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.add("X-CSRFToken", csrfToken);
            headers.add(HttpHeaders.COOKIE, cookies);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.add(HttpHeaders.ACCEPT_ENCODING, "gzip");
            headers.add(HttpHeaders.CACHE_CONTROL, "max-age=0");

            return execution.execute(request, body);
        });


    }

    public HashMap<String, String> login() {
        try {


            // 1. Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<Void> request = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate(httpClient);
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
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("result")) {
                    String csrfToken = (String) responseBody.get("result");
                    this.csrfToken = csrfToken;
                    System.out.println("CSRF Token: " + csrfToken);
                }

                // 4. Extract cookies from headers
                List<String> setCookie = response.getHeaders().get(HttpHeaders.SET_COOKIE);
                if (setCookie != null) {
                    String cookies = String.join("; ", setCookie);
                    this.cookies = cookies;
                    System.out.println("Cookies: " + cookies);
                }


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

        RestTemplate restTemplate = new RestTemplate(httpClient);
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);

        return response.getBody();
    }

    /**
     * Fetch list of all datasets
     */

    public JsonNode fetchDatasets() {
        logger.info("Fetching Datasets");
        String url = supersetUrlFromProperties + "/api/v1/dataset/?force=true";

        HashMap<String, String> loginResult = login();
        RestTemplate restTemplate = new RestTemplate(httpClient);
        addHeaders(restTemplate);


        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        return response.getBody();
    }


    /**
     * Fetch a single dataset by ID
     */
    @Cacheable(value = "dataset", key = "#datasetId")
    public JsonNode fetchDataset(String datasetId) {

        RestTemplate restTemplate = new RestTemplate(httpClient);
        addHeaders(restTemplate);


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
        RestTemplate restTemplate = new RestTemplate(httpClient);
        addHeaders(restTemplate);


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
