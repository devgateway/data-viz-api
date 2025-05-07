package org.devgateway.viz.gateway.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.devgateway.viz.gateway.services.rest.SuperSetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.devgateway.viz.gateway.services.Utils.*;

@Service
public class DimensionsService {
    private final SuperSetClient superSetClient;
    // This class is currently empty, but it can be used to implement methods related to dimensions in the future.
    // For example, you might want to add methods to fetch dimensions from a database or perform calculations.
    private final Logger logger = LoggerFactory.getLogger(StatsService.class);

    private final ObjectMapper objectMapper;

    public DimensionsService(@Autowired SuperSetClient superSetClient, ObjectMapper objectMapper) {
        this.superSetClient = superSetClient;
        this.objectMapper = objectMapper;
    }

    //@Cacheable(cacheNames = "dimensions", key = "#datasetId")
    public List<Map<String, Object>> getDimensions(String datasetId) {
        JsonNode root = superSetClient.fetchDataset(datasetId);

        if (root == null || !root.has("result")) {
            return Collections.emptyList();
        }
        return extractDimensions(root.get("result"));
    }


    @Cacheable(value = "distinctDimensionValues", key = "#field + #datasetId")
    public Set<String> fetchDistinctDimensionValues(String field, String datasetId) {

        logger.info("Fetching distinct values for field: " + field + " from dataset: " + datasetId);

        Set<String> uniqueValues = new HashSet<>();

        Map<String, Object> datasource = createDatasource(datasetId);
        Map<String, Object> query1 = createQuery(field);

        JsonNode requestNode = objectMapper.valueToTree(createSupersetRequest(datasource, Collections.singletonList(query1)));

        JsonNode supersetResp = superSetClient.postChartData(requestNode);

        if (supersetResp != null && supersetResp.has("result") && supersetResp.get("result").isArray()) {
            for (JsonNode row : supersetResp.get("result").get(0).get("data")) {
                if (row.has(field) && !row.get(field).isNull()) {
                    uniqueValues.add(row.get(field).asText());
                }
            }
        }
        return uniqueValues;
    }
}
