package org.devgateway.viz.gateway.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
public class SupersetService {

    private final RestTemplate restTemplate;

    public SupersetService() {
        this.restTemplate = new RestTemplate();
    }

    public Object fetchCharts(String url) {
        return restTemplate.getForObject(url, Object.class);
    }

    public Object fetchDatasets(String url) {
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("result");

        return results.stream().map(dataset -> Map.of(
                "id", dataset.get("id"),
                "value", dataset.get("table_name"),
                "label", dataset.get("table_name")
        )).toList();
    }

    public Object fetchDimensions(String datasetId) {
        // Add logic for fetching dimensions
        return null;
    }

    public Object fetchFilters(String datasetId) {
        // Add logic for fetching filters
        return null;
    }

    public Object fetchCategories(String datasetId) {
        // Add logic for fetching categories
        return null;
    }

    public Object fetchMeasures(String datasetId) {
        // Add logic for fetching measures
        return null;
    }
}