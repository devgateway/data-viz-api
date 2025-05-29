package org.devgateway.viz.gateway.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SuperSetProxyService {

    private final ChartService chartService;
    private final DatasetService datasetService;
    private final DimensionsService dimensionService;
    private final MeasuresService measureService;

    private final CategoriesService categoriesService;

    private final StatsService statsService;

    private final FiltersService filtersService;

    public SuperSetProxyService(ChartService chartService, DatasetService datasetService, DimensionsService dimensionService, MeasuresService measureService, CategoriesService categoriesService, StatsService statsService, FiltersService filtersService) {
        this.chartService = chartService;
        this.datasetService = datasetService;
        this.dimensionService = dimensionService;
        this.measureService = measureService;
        this.categoriesService = categoriesService;
        this.statsService = statsService;
        this.filtersService = filtersService;
    }

    //fetchCharts
    public JsonNode getCharts() {
        return chartService.fetchCharts();
    }

    //fetchDatasets
    public List<Map<String, Object>> getDatasets() {
        return datasetService.fetchDatasets();
    }

    //fetchDimensions
    public List<Map<String, Object>> getDimensions(String datasetId) {
        return dimensionService.getDimensions(datasetId);
    }

    //fetchCategories
    public List<Map<String, Object>> getCategories(String datasetId) {
        return categoriesService.getCategories(datasetId);
    }


    //fetchMeasures
    public List<Map<String, Object>> getMeasures(String datasetId) {
        return measureService.getMeasures(datasetId);
    }

    //getStats
    public Object getStats(String datasetId, Map<String, String> queryParams, String groupsPath) {
        return statsService.getStats(datasetId, queryParams, groupsPath);
    }

    //fetchFilters
    public List<Map<String, Object>> getFilters(String datasetId) {
        return filtersService.getFilters(datasetId);
    }
}

