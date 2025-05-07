package org.devgateway.viz.gateway.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.devgateway.viz.gateway.services.rest.SuperSetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FiltersService {
    private final Logger logger = LoggerFactory.getLogger(StatsService.class);

    private final SuperSetClient superSetClient;
    private final ObjectMapper objectMapper;

    public FiltersService(@Autowired SuperSetClient superSetClient, ObjectMapper objectMapper) {
        this.superSetClient = superSetClient;
        this.objectMapper = objectMapper;
    }

    public List<Map<String, Object>> getFilters(String datasetId) {
        JsonNode root = superSetClient.fetchDataset(datasetId);

        if (root == null || !root.has("result")) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> filters = new ArrayList<>();

        for (JsonNode column : root.get("result").get("columns")) {
            if (column.path("filterable").asBoolean(false)) {
                String colName = column.path("column_name").asText();
                String verboseName = column.path("verbose_name").isNull() ? colName : column.path("verbose_name").asText();

                Map<String, Object> filter = new HashMap<>();
                filter.put("field", colName);
                filter.put("label", verboseName);
                filter.put("type", colName);
                filter.put("labels", new HashMap<>());
                filter.put("param", colName);
                filters.add(filter);
            }
        }
        return filters;
    }

}
