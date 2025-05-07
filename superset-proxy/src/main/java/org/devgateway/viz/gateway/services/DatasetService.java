package org.devgateway.viz.gateway.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.devgateway.viz.gateway.services.rest.SuperSetClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DatasetService {

    private final SuperSetClient superSetClient;
    private final ObjectMapper objectMapper;

    public DatasetService(@Autowired SuperSetClient superSetClient, ObjectMapper objectMapper) {
        this.superSetClient = superSetClient;
        this.objectMapper = objectMapper;
    }


    public List<Map<String, Object>> fetchDatasets() {
        JsonNode root = superSetClient.fetchDatasets();
        if (root == null || !root.has("result")) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> datasets = new ArrayList<>();
        for (JsonNode ds : root.get("result")) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", ds.get("id").asInt());
            data.put("value", ds.get("table_name").asText());
            data.put("label", ds.get("table_name").asText());
            datasets.add(data);
        }
        return datasets;
    }


}
