package org.devgateway.viz.gateway.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.devgateway.viz.gateway.common.Constants;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class SupersetProxyService {

    private final SupersetApiClient supersetApiClient;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(SupersetProxyService.class);

    @Autowired
    public SupersetProxyService(SupersetApiClient supersetApiClient) {
        this.supersetApiClient = supersetApiClient;
        this.objectMapper = new ObjectMapper();
    }

    public JsonNode fetchCharts(String supersetUrl) {
        return supersetApiClient.fetchCharts(supersetUrl);
    }

    public List<Map<String, Object>> fetchDatasets(String supersetUrl) {
        JsonNode root = supersetApiClient.fetchDatasets(supersetUrl);
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

    public List<Map<String, Object>> fetchDimensions(String supersetUrl, String datasetId) {
        JsonNode root = supersetApiClient.fetchDataset(supersetUrl, datasetId);
        if (root == null || !root.has("result")) {
            return Collections.emptyList();
        }
        return extractDimensions(root.get("result"));
    }

    public List<Map<String, Object>> fetchFilters(String supersetUrl, String datasetId) {
        JsonNode root = supersetApiClient.fetchDataset(supersetUrl, datasetId);
        if (root == null || !root.has("result")) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> filters = new ArrayList<>();
        for (JsonNode column : root.get("result").get("columns")) {
            if (column.path("type").asText("").contains("STRING") && column.path("filterable").asBoolean(false)) {
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

    public List<Map<String, Object>> fetchMeasures(String supersetUrl, String datasetId) {
        if (datasetId == null) {
            return Collections.emptyList();
        }

        JsonNode resultNode = supersetApiClient.fetchDataset(supersetUrl, datasetId).get("result");

        List<Map<String, Object>> measures = new ArrayList<>();

        for (JsonNode metric : resultNode.get("metrics")) {
            measures.add(createMeasure(metric));
        }

        return measures;
    }

    @Cacheable("categories")
    public List<Map<String, Object>> fetchCategories(String supersetUrl, String datasetId) {
        if (datasetId == null) {
            return Collections.emptyList();
        }

        JsonNode result = supersetApiClient.fetchDataset(supersetUrl, datasetId).get("result");
        List<Map<String, Object>> dimensions = extractDimensions(result);
        List<String> measures = extractUniqueMeasures(result);
        List<Map<String, Object>> categories = new ArrayList<>();

        for (Map<String, Object> dim : dimensions) {
            String field = (String) dim.get("field");
            Map<String, Object> category = new HashMap<>();
            category.put("type", field);
            List<Map<String, Object>> items = new ArrayList<>();
            for (String value : fetchDistinctDimensionValues(supersetUrl, field, datasetId)) {
                items.add(createItem(field, value,
                        Constants.COLORS.get(items.size() % Constants.COLORS.size())));
            }
            category.put("items", items);
            categories.add(category);
        }
        return categories;
    }

    private Set<String> fetchDistinctDimensionValues(String supersetUrl, String field, String datasetId) {
        Set<String> uniqueValues = new HashSet<>();

        Map<String, Object> datasource = createDatasource(datasetId);
        Map<String, Object> query1 = createQuery(field);
        JsonNode requestNode = objectMapper.valueToTree(createSupersetRequest(datasource, Collections.singletonList(query1)));
        logger.info(requestNode.toString());
        JsonNode supersetResp = supersetApiClient.postChartData(supersetUrl, requestNode);

        if (supersetResp != null && supersetResp.has("result") && supersetResp.get("result").isArray()) {
            for (JsonNode row : supersetResp.get("result").get(0).get("data")) {
                if (row.has(field) && !row.get(field).isNull()) {
                    uniqueValues.add(row.get(field).asText());
                }
            }
        }
        return uniqueValues;
    }

    private List<Map<String, Object>> extractDimensions(JsonNode result) {
        List<Map<String, Object>> dimensions = new ArrayList<>();
        for (JsonNode column : result.get("columns")) {
            if (column.path("type").asText("").contains("STRING") && column.path("groupby").asBoolean(false)) {
                String colName = column.path("column_name").asText();
                String verboseName = column.path("verbose_name").isNull() ? colName : column.path("verbose_name").asText();

                Map<String, Object> dim = new HashMap<>();
                dim.put("field", colName);
                dim.put("label", verboseName);
                dim.put("type", colName);
                dim.put("labels", new HashMap<>());
                dim.put("value", colName);
                dimensions.add(dim);
            }
        }
        return dimensions;
    }

    private List<String> extractUniqueMeasures(JsonNode result) {
        List<String> measures = new ArrayList<>();
        for (JsonNode metric : result.get("metrics")) {
            if (metric.has("metric_name")) {
                measures.add(metric.get("metric_name").asText());
            }
        }
        return measures;
    }

    @Cacheable("stats")
    public Object getStats(String supersetUrl, String datasetId, Map<String, String> queryParams, String groupsPath) throws Exception {
        if (datasetId == null || groupsPath == null || groupsPath.isEmpty()) {
            return Collections.emptyList();
        }

        JsonNode requestBody = buildSupersetDataRequest(supersetUrl, datasetId, queryParams, groupsPath);
        JsonNode supersetResponse = supersetApiClient.postChartData(supersetUrl, requestBody);

        JsonNode resultArray = supersetResponse.get("result");
        if (resultArray == null || !resultArray.isArray() || resultArray.isEmpty()) {
            return Collections.emptyList();
        }

        JsonNode queriesNode = requestBody.get("queries");
        if (queriesNode.isArray() && !queriesNode.isEmpty()) {
            return transformData(resultArray, queriesNode.get(0).get("groupby"), queriesNode.get(0).get("metrics"), groupsPath.split("/"));
        }
        return Collections.emptyList();
    }

    private JsonNode buildSupersetDataRequest(String supersetUrl, String datasetId, Map<String, String> queryParams, String groupsPath) {
        List<String> measuresArr = extractUniqueMeasures(supersetApiClient.fetchDataset(supersetUrl, datasetId).get("result"));

        Map<String, Object> datasource = createDatasource(datasetId);

        List<Map<String, Object>> queries = new ArrayList<>();
        String[] groupsArray = groupsPath.split("/");

        Map<String, Object> queryForDimension1 = createQuery(new String[] {groupsArray[0]}, measuresArr, queryParams);
        queries.add(queryForDimension1);
        if (groupsArray.length > 1) {
            Map<String, Object> queryForDimension2 = createQuery(groupsArray, measuresArr, queryParams);
            queries.add(queryForDimension2);
        }

        return objectMapper.valueToTree(createSupersetRequest(datasource, queries));
    }

    private Object transformData(JsonNode data, JsonNode dimensionsNode, JsonNode metricsNode, String[] dimensionsArray) {
        JsonNode resultsForFirstDimension = data.get(0);

        Map<String, Object> transformed = createTransformedData();
        List<Map<String, Object>> measuresList = new ArrayList<>();
        List<Map<String, Object>> typesList = new ArrayList<>();
        transformed.put("metadata", createMetadata(measuresList, typesList));

        transformed.put("itemsSize", resultsForFirstDimension.get("data").size());
        transformed.put("count", resultsForFirstDimension.get("data").size());

        if (!resultsForFirstDimension.has("data") || resultsForFirstDimension.get("data").isEmpty()) {
            return transformed;
        }

        transformed.put("children", new ArrayList<>());


        for (JsonNode metric : metricsNode) {
            measuresList.add(createMeasure(metric.asText(), metric.asText()));
        }

        List<String> dimList = Arrays.asList(dimensionsArray);
        for (String dim : dimList) {
            typesList.add(createType(dim));
        }

        for (JsonNode row : resultsForFirstDimension.get("data")) {
            for (String dim : dimList) {
                if (row.has(dim)) {
                    Map<String, Object> dataItem = createDataItem(dim, row.get(dim).asText(), metricsNode, row);
                    ((List<Map<String, Object>>) transformed.get("children")).add(dataItem);
                }
            }
        }

    JsonNode resultsForSecondDimension = data.get(1);
    if (resultsForSecondDimension != null && resultsForSecondDimension.has("data")) {
        for (JsonNode row : resultsForSecondDimension.get("data")) {
            String parentDim = dimensionsArray[0];
            String childDim = dimensionsArray[1];
            if (row.has(childDim)) {
                Map<String, Object> dataItem = createDataItem(childDim, row.get(childDim).asText(), metricsNode, row);
                for (Map<String, Object> child : (List<Map<String, Object>>) transformed.get("children")) {
                    if (child.get("value").equals(row.get(parentDim).asText())) {
                        ((List<Map<String, Object>>) child.computeIfAbsent("children", k -> new ArrayList<>())).add(dataItem);
                    }
                }
            }
        }
    }



    return transformed;
}

    private Map<String, Object> createDatasource(String datasetId) {
        Map<String, Object> datasource = new HashMap<>();
        datasource.put("id", datasetId);
        datasource.put("type", "table");
        return datasource;
    }

    private Map<String, Object> createQuery(String field) {
        Map<String, Object> query = new HashMap<>();
        query.put("groupby", Collections.singletonList(field));
        query.put("columns", Collections.emptyList());
        query.put("metrics", Collections.emptyList());
        return query;
    }

    private Map<String, Object> createQuery(String[] groupArray, List<String> measuresArr, Map<String, String> queryParams) {
        Map<String, Object> query = new HashMap<>();
        query.put("groupby", Arrays.asList(groupArray));
        query.put("columns", Arrays.asList(groupArray));
        query.put("metrics", measuresArr);
        query.put("row_limit", Constants.ROW_LIMIT);

        List<Map<String, Object>> filters = new ArrayList<>();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            if (!Constants.SPECIAL_PARAMS.contains(entry.getKey())) {
                Map<String, Object> filter = new HashMap<>();
                filter.put("col", entry.getKey());
                filter.put("op", "in");
                filter.put("val", Arrays.asList(entry.getValue().split(",")));
                filters.add(filter);
            }
        }
        query.put("filters", filters);
        return query;
    }

    private Map<String, Object> createSupersetRequest(Map<String, Object> datasource, List<Map<String, Object>> queries) {
        Map<String, Object> supersetRequest = new HashMap<>();
        supersetRequest.put("datasource", datasource);
        supersetRequest.put("queries", queries);
        return supersetRequest;
    }

    private Map<String, Object> createTransformedData() {
        Map<String, Object> transformed = new HashMap<>();
        transformed.put("type", "total");
        transformed.put("value", "total");
        transformed.put("count", 0);
        return transformed;
    }

    private Map<String, Object> createMetadata(List<Map<String, Object>> measuresList, List<Map<String, Object>> typesList) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("measures", measuresList);
        metadata.put("types", typesList);
        return metadata;
    }

    private Map<String, Object> createMeasure(JsonNode metric) {
        String label = metric.get("verbose_name") != null ? metric.get("verbose_name").asText() : metric.get("metric_name").asText();
        return createMeasure(metric.get("metric_name").asText(), label);
    }

    private Map<String, Object> createMeasure(String name, String label) {
        Map<String, Object> measure = new HashMap<>();
        measure.put("label", label);
        measure.put("labels", new HashMap<>());
        measure.put("value", name);
        measure.put("group", createGroup(Constants.MEASURE_GROUP_LABEL));
        measure.put("styles", createStyles(Constants.DEFAULT_COLOR));
        measure.put("position", 0);
        measure.put("enabled", null);
        return measure;
    }

    private Map<String, Object> createGroup(String label) {
        Map<String, Object> group = new HashMap<>();
        group.put("label", label);
        group.put("labels", new HashMap<>());
        return group;
    }

    private Map<String, Object> createStyles(String color) {
        Map<String, Object> styles = new HashMap<>();
        styles.put("color", color);
        return styles;
    }

    private Map<String, Object> createType(String dim) {
        Map<String, Object> type = new HashMap<>();
        type.put("dimension", dim);
        type.put("category", dim);
        type.put("items", new ArrayList<>());
        return type;
    }

    private Map<String, Object> createDataItem(String dim, String value, JsonNode metricsNode, JsonNode row) {
        Map<String, Object> dataItem = new HashMap<>();
        dataItem.put("type", dim);
        dataItem.put("value", value);

        for (JsonNode metric : metricsNode) {
            String metricName = metric.asText();
            dataItem.put(metricName, row.has(metricName) ? row.get(metricName).asDouble() : 0.0);
        }

        return dataItem;
    }

    private Map<String, Object> createItem(String field, String value, String color) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", value);
        item.put("code", value);
        item.put("value", value);
        item.put("position", 0);
        item.put("parent", null);
        item.put("type", field);
        item.put("labels", new HashMap<>());
        item.put("descriptions", new HashMap<>());
        item.put("categoryStyle", createStyles(color));
        return item;
    }
}