package org.devgateway.viz.gateway.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.devgateway.viz.gateway.services.rest.SuperSetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.devgateway.viz.gateway.services.Utils.createMeasure;

@Service
public class MeasuresService {
    private final SuperSetClient superSetClient;
    // This class is currently empty, but it can be used to implement methods related to dimensions in the future.
    // For example, you might want to add methods to fetch dimensions from a database or perform calculations.
    private final Logger logger = LoggerFactory.getLogger(StatsService.class);

    private final ObjectMapper objectMapper;

    public MeasuresService(@Autowired SuperSetClient superSetClient, ObjectMapper objectMapper) {
        this.superSetClient = superSetClient;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "measures", key = "***REMOVED***datasetId")
    public List<Map<String, Object>> getMeasures(String datasetId) {
        if (datasetId == null) {
            return Collections.emptyList();
        }

        JsonNode resultNode = superSetClient.fetchDataset(datasetId).get("result");

        List<Map<String, Object>> measures = new ArrayList<>();

        for (JsonNode metric : resultNode.get("metrics")) {
            measures.add(createMeasure(metric));
        }

        return measures;
    }


}
