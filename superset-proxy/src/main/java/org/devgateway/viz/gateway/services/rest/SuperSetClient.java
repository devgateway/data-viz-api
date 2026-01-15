package org.devgateway.viz.gateway.services.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.devgateway.viz.gateway.services.CountEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;

@Component
public class SuperSetClient {

    private final Logger logger = Logger.getLogger(SuperSetClient.class.getName());

    @Value("${viz.superset.url}")
    private String supersetUrlFromProperties;

    @Value("${viz.superset.warmUpTop}")
    private int warmUpTopN;

    private final RestTemplate restTemplate;

    private final Cache supersetChartDataCache;
    private final Cache supersetChartDataCacheStats;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    //TODO: add constructor initiating restTemplate and httpClient
    public SuperSetClient(
            CacheManager cacheManager,
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper) {

        supersetChartDataCache = cacheManager.getCache("superset-chart-data");
        supersetChartDataCacheStats = cacheManager.getCache("superset-chart-data-stats");

        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);
        cm.setDefaultMaxPerRoute(50);

        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(cm)
                .build();

        HttpComponentsClientHttpRequestFactory httpClient = new HttpComponentsClientHttpRequestFactory(client);
        this.restTemplate = new RestTemplate(httpClient);
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
        String qParam = "(page:0,page_size:200)";
        URI uri = UriComponentsBuilder
                .fromHttpUrl(supersetUrlFromProperties + "/api/v1/dataset/")
                .queryParam("q", qParam)
                .queryParam("force", true)
                .build()
                .encode()
                .toUri();

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(uri, JsonNode.class);
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

    public ObjectNode postChartData(JsonNode requestBody) {
        return postChartData(requestBody, false);
    }

    public ObjectNode postChartData(JsonNode requestBody, boolean force) {
        ObjectNode chartData;
        chartData = force ? null : supersetChartDataCache.get(requestBody, ObjectNode.class);
        if (chartData != null) {
            chartData.put("isCached", true);
        } else {
            chartData = postChartDataDirect(requestBody);
            chartData.put("cachedAt", Instant.now().toString());
            supersetChartDataCache.put(requestBody, chartData);
            chartData.put("isCached", false);
        }

        String reqKey = requestBody.toString();
        Integer cnt = supersetChartDataCacheStats.get(reqKey, Integer.class);
        supersetChartDataCacheStats.put(reqKey, 1 + (cnt == null ? 0 : cnt));

        return chartData;
    }

    private ObjectNode postChartDataDirect(JsonNode requestBody) {
        String datasourceId = requestBody.get("datasource").get("id").asText();
        logger.info("Calling Superset API to fetch data for datasource ID: " + datasourceId);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Request body: " + requestBody.toPrettyString());
        }

        String submitUrl = supersetUrlFromProperties + "/api/v1/chart/data";

        ResponseEntity<JsonNode> submitResponse = restTemplate.postForEntity(submitUrl, requestBody, JsonNode.class);

        if (submitResponse.getStatusCode() != HttpStatus.OK && submitResponse.getStatusCode() != HttpStatus.ACCEPTED) {
            throw new RuntimeException("Failed to submit query: " + submitResponse.getStatusCode());
        }

        ObjectNode responseBody = (ObjectNode) submitResponse.getBody();
        if (responseBody.has("result")) {
            return responseBody;
        } else {
            throw new RuntimeException("Async fetching from Superset not supported.");
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void warmUpAndReset() {
        warmUp();

        supersetChartDataCacheStats.clear();
    }

    public void warmUp() {
        logger.info("Warming up the cache");

        PriorityQueue<CountEntry> topQ = new PriorityQueue<>();

        String prefix = "superset-chart-data-stats::";
        ScanOptions options = ScanOptions.scanOptions().match(prefix + "*").count(100).build();
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                String key = cursor.next().substring(prefix.length());
                Integer cnt = supersetChartDataCacheStats.get(key, Integer.class);
                if (cnt != null) {
                    if (topQ.size() < warmUpTopN) {
                        topQ.offer(new CountEntry(key, cnt));
                    } else if (cnt > topQ.peek().getCount()) {
                        topQ.poll();
                        topQ.offer(new CountEntry(key, cnt));
                    }
                }
            }
        }

        logger.info("Number of charts to be warmed up: " + topQ.size());

        for (CountEntry e : topQ) {
            warmUp(e.getKey());
        }

        logger.info("Finished cache warm up");
    }

    private void warmUp(String responseBody) {
        try {
            JsonNode requestBody = objectMapper.readTree(responseBody);
            postChartData(requestBody, true);
        } catch (RuntimeException | JsonProcessingException e) {
            logger.log(Level.SEVERE, "Failed to warm up chart. Request body: " + responseBody, e);
        }
    }
}
