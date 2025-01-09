package org.devgateway.viz.gateway.controllers;

import org.devgateway.viz.gateway.services.SupersetService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*") // Allow all origins
public class SupersetController {

    private final SupersetService supersetService;

    @Value("${superset.url}")
    private String supersetUrl;

    public SupersetController(SupersetService supersetService) {
        this.supersetService = supersetService;
    }

    @GetMapping("/charts")
    public Object getCharts() {
        return supersetService.fetchCharts(supersetUrl + "/api/v1/chart/");
    }

    @GetMapping("/datasets")
    public Object getDatasets() {
        return supersetService.fetchDatasets(supersetUrl + "/api/v1/dataset/");
    }

    @GetMapping("/dimensions")
    public Object getDimensions(@RequestParam(required = false) String datasetId) {
        if (datasetId == null || datasetId.isEmpty()) {
            return List.of();
        }
        return supersetService.fetchDimensions(datasetId);
    }

    @GetMapping("/filters")
    public Object getFilters(@RequestParam(required = false) String datasetId) {
        if (datasetId == null || datasetId.isEmpty()) {
            return List.of();
        }
        return supersetService.fetchFilters(datasetId);
    }

    @GetMapping("/categories")
    public Object getCategories(@RequestParam(required = false) String datasetId) {
        if (datasetId == null || datasetId.isEmpty()) {
            return List.of();
        }
        return supersetService.fetchCategories(datasetId);
    }

    @GetMapping("/measures")
    public Object getMeasures(@RequestParam(required = false) String datasetId) {
        if (datasetId == null || datasetId.isEmpty()) {
            return List.of();
        }
        return supersetService.fetchMeasures(datasetId);
    }
}