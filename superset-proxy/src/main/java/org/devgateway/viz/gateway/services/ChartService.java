package org.devgateway.viz.gateway.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.devgateway.viz.gateway.services.rest.SuperSetClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChartService {

    private final SuperSetClient superSetClient;
    private final ObjectMapper objectMapper;

    public ChartService(@Autowired SuperSetClient superSetClient, ObjectMapper objectMapper) {
        this.superSetClient = superSetClient;
        this.objectMapper = objectMapper;
    }

    public JsonNode fetchCharts() {
        return superSetClient.fetchCharts();
    }
}
