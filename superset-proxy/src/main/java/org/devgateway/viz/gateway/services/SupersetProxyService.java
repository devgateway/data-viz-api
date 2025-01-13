package org.devgateway.viz.gateway.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SupersetProxyService {

    private final SupersetApiClient supersetApiClient;
    private final ObjectMapper objectMapper;

    Logger logger = LoggerFactory.getLogger(SupersetProxyService.class);

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
        JsonNode results = root.get("result");
        if (results.isArray()) {
            for (JsonNode ds : results) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", ds.get("id").asInt());
                data.put("value", ds.get("table_name").asText());
                data.put("label", ds.get("table_name").asText());
                datasets.add(data);
            }
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
        
        JsonNode result = root.get("result");
        if (result.has("columns")) {
            JsonNode columns = result.get("columns");
            if (columns.isArray()) {
                for (JsonNode column : columns) {
                    String colType = column.path("type").asText("");
                    boolean isFilterable = column.path("filterable").asBoolean(false);
                    if (colType.contains("STRING") && isFilterable) {
                        String colName = column.path("column_name").asText();
                        String verboseName = column.path("verbose_name").isNull()
                                ? colName
                                : column.path("verbose_name").asText();
    
                        Map<String, Object> filter = new HashMap<>();
                       filter.put("field", colName);
                       filter.put("label", verboseName);
                       filter.put("type", colName);
                       filter.put("labels", new HashMap<>());
                       filter.put("param", colName);
                        filters.add(filter);
                    }
                }
            }
        }

        return filters;
    }

    public List<Map<String, Object>> fetchMeasures(String supersetUrl, String datasetId) {        
        if (datasetId == null) {
            return Collections.emptyList();
        }
        JsonNode resultNode = supersetApiClient.fetchDataset(supersetUrl, datasetId).get("result");
        List<String> measuresList = extractUniqueMeasures(resultNode);

        List<Map<String, Object>> measures = new ArrayList<>();
        for (String measure : measuresList) {
            Map<String, Object> data = new HashMap<>();
            data.put("label", measure);
            data.put("labels", new HashMap<>());
            data.put("value", measure);

            Map<String, Object> group = new HashMap<>();
            group.put("label", "Population");
            group.put("labels", new HashMap<>());
            data.put("group", group);

            Map<String, Object> styles = new HashMap<>();
            styles.put("color", "#555");
            data.put("styles", styles);

            data.put("position", 0);
            data.put("enabled", null);
            measures.add(data);
        }

        return measures;
    }

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
            Set<String> uniqueValues = fetchDistinctDimensionValues(supersetUrl, field, datasetId);
            if (uniqueValues != null) {
                for (String value : uniqueValues) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", value);
                    item.put("code", value);
                    item.put("value", value);
                    item.put("position", 0);
                    item.put("parent", null);
                    item.put("type", field);
                    item.put("labels", new HashMap<>());
                    item.put("descriptions", new HashMap<>());

                    Map<String, Object> categoryStyle = new HashMap<>();
                    categoryStyle.put("color", "#BA4747");
                    item.put("categoryStyle", categoryStyle);

                    items.add(item);
                }
            }
            category.put("items", items);
            categories.add(category);
        }
        return categories;
    }

    private Set<String> fetchDistinctDimensionValues(String supersetUrl, String field, String datasetId) {
        Set<String> uniqueValues = new HashSet<>();

        Map<String, Object> datasource = new HashMap<>();
        datasource.put("id", datasetId);
        datasource.put("type", "table");

        Map<String, Object> query1 = new HashMap<>();
        query1.put("groupby", Collections.singletonList(field));
        query1.put("columns", Collections.emptyList());
        query1.put("metrics", Collections.emptyList());

        List<Map<String, Object>> queries = new ArrayList<>();
        queries.add(query1);
        
        Map<String, Object> supersetRequest = new HashMap<>();
        supersetRequest.put("datasource", datasource);
        supersetRequest.put("queries", queries);

        JsonNode requestNode = objectMapper.valueToTree(supersetRequest);
        JsonNode supersetResp = supersetApiClient.postChartData(supersetUrl, requestNode);

        if (supersetResp != null && supersetResp.has("result") && supersetResp.get("result").isArray()) {
            JsonNode resultArr = supersetResp.get("result");
            if (resultArr.size() > 0) {
                JsonNode result = resultArr.get(0);
                if (result != null && result.has("data")) {
                    for (JsonNode row : result.get("data")) {
                        if (row.has(field) && !row.get(field).isNull()) {
                            uniqueValues.add(row.get(field).asText());
                        }
                    }
                }
            }
        }

        return uniqueValues;

    }

   private List<Map<String, Object>> extractDimensions(JsonNode result) {
        List<Map<String, Object>> dimensions = new ArrayList<>();
        if (result != null && result.has("columns")) {
            for (JsonNode column : result.get("columns")) {
                String colType = column.path("type").asText("");
                boolean isGroupBy = column.path("groupby").asBoolean(false);
                if (colType.contains("STRING") && isGroupBy) {
                    String colName = column.path("column_name").asText();
                    String verboseName = column.path("verbose_name").isNull()
                            ? colName
                            : column.path("verbose_name").asText();

                    Map<String, Object> dim = new HashMap<>();
                    dim.put("field", colName);
                    dim.put("label", verboseName);
                    dim.put("type", colName);
                    dim.put("labels", new HashMap<>());
                    dim.put("value", colName);
                    dimensions.add(dim);
                }
            }
        }

        return dimensions;
    }

    private List<String> extractUniqueMeasures(JsonNode result) {
        List<String> measures = new ArrayList<>();
        if (result != null && result.has("metrics")) {
            for (JsonNode metric : result.get("metrics")) {
                if (metric.has("metric_name")) {
                    measures.add(metric.get("metric_name").asText());
                }
            }
        }
        return measures;
    }


    public Object getStats(String supersetUrl,    
        String datasetId,
        Map<String, String> queryParams,
        String groupsPath // e.g. "gender/country"
) throws Exception {
    if (datasetId == null || groupsPath == null || groupsPath.isEmpty()) {
        return Collections.emptyList();
    }

    JsonNode requestBody = buildSupersetDataRequest(supersetUrl, datasetId, queryParams, groupsPath);
    JsonNode supersetResponse = supersetApiClient.postChartData(supersetUrl, requestBody);

    
   JsonNode resultArray = supersetResponse.get("result");
    if (resultArray == null || !resultArray.isArray() || resultArray.size() == 0) {
        return Collections.emptyList();
    }
    JsonNode firstResult = resultArray.get(0);

    
    JsonNode queriesNode = requestBody.get("queries");
    if (queriesNode.isArray() && queriesNode.size() > 0) {
        JsonNode q0 = queriesNode.get(0);
        JsonNode groupbyNode = q0.get("groupby");
        JsonNode metricsNode = q0.get("metrics");
        return transformData(firstResult, groupbyNode, metricsNode);
    }

    return Collections.emptyList();
}

private JsonNode buildSupersetDataRequest(String supersetUrl, String datasetId, Map<String, String> queryParams, String groupsPath) {
    List<String> measuresArr = Collections.emptyList();
    JsonNode result = supersetApiClient.fetchDataset(supersetUrl, datasetId).get("result");
    measuresArr = extractUniqueMeasures(result);

    Map<String, Object> datasource = new HashMap<>();
    datasource.put("id", datasetId);
    datasource.put("type", "table");

    Map<String, Object> query1 = new HashMap<>();
    if (groupsPath != null && !groupsPath.isEmpty()) {
        String[] groupArray = groupsPath.split("/");
        query1.put("groupby", Arrays.asList(groupArray));
        query1.put("columns", Arrays.asList(groupArray));
    } else {
        query1.put("groupby", Collections.emptyList());
        query1.put("columns", Collections.emptyList());
    }

    query1.put("metrics", measuresArr);

    List<Map<String, Object>> filters = new ArrayList<>();
    for (Map.Entry<String, String> entry : queryParams.entrySet()) {
        String key = entry.getKey();
        // Exclude known special keys
        if (!"row_limit".equals(key) && !"datasetId".equals(key)) {
            String[] values = entry.getValue().split(",");
            Map<String, Object> filter = new HashMap<>();
            filter.put("col", key);
            filter.put("op", "in");
            filter.put("val", Arrays.asList(values));
            filters.add(filter);
        }
    }
    query1.put("filters", filters);

    // Build final
    Map<String, Object> supersetRequest = new HashMap<>();
    supersetRequest.put("datasource", datasource);
    supersetRequest.put("queries", Collections.singletonList(query1));

    return objectMapper.valueToTree(supersetRequest);
}


private Map<String, Object> transformData(JsonNode data, JsonNode dimensionsNode, JsonNode metricsNode) {
    Map<String, Object> transformed = new HashMap<>();
    transformed.put("type", "total");
    transformed.put("value", "total");
    transformed.put("count", 0);

    Map<String, Object> metadata = new HashMap<>();
    List<Map<String, Object>> measuresList = new ArrayList<>();
    List<Map<String, Object>> typesList = new ArrayList<>();
    metadata.put("measures", measuresList);
    metadata.put("types", typesList);
    transformed.put("metadata", metadata);

    List<Map<String, Object>> children = new ArrayList<>();
    transformed.put("children", children);
    transformed.put("itemsSize", 0);

    // If no data or no rows
    if (data == null || !data.has("data")) {
        return transformed;
    }

    // handle metrics
    if (metricsNode != null && metricsNode.isArray()) {
        for (JsonNode metric : metricsNode) {
            String metricName = metric.asText();
            Map<String, Object> measureObj = new HashMap<>();
            measureObj.put("label", metricName);
            measureObj.put("labels", new HashMap<>());
            measureObj.put("value", metricName);

            Map<String, Object> groupObj = new HashMap<>();
            groupObj.put("label", "Overall");
            groupObj.put("labels", new HashMap<>());
            measureObj.put("group", groupObj);

            Map<String, Object> stylesObj = new HashMap<>();
            stylesObj.put("color", "#555");
            measureObj.put("styles", stylesObj);

            measureObj.put("position", 0);
            measureObj.put("enabled", null);
            measuresList.add(measureObj);
        }
    }

    // handle dimensions
    List<String> dimList = new ArrayList<>();
    if (dimensionsNode != null && dimensionsNode.isArray()) {
        for (JsonNode dim : dimensionsNode) {
            dimList.add(dim.asText());
        }
    }
    // create "types" placeholders
    for (String dim : dimList) {
        Map<String, Object> typeObj = new HashMap<>();
        typeObj.put("dimension", dim);
        typeObj.put("category", dim);
        typeObj.put("items", new ArrayList<>());
        typesList.add(typeObj);
    }

    // populate data
    JsonNode rows = data.get("data");
    if (rows.isArray()) {
        for (JsonNode row : rows) {
            // For each dimension, add an item to 'children'
            for (String dim : dimList) {
                if (row.has(dim)) {
                    Map<String, Object> dataItem = new HashMap<>();
                    dataItem.put("type", dim);
                    dataItem.put("value", row.get(dim).asText());

                    // also add metrics
                    if (metricsNode != null && metricsNode.isArray()) {
                        for (JsonNode metric : metricsNode) {
                            String metricName = metric.asText();
                            double val = row.has(metricName) ? row.get(metricName).asDouble() : 0.0;
                            dataItem.put(metricName, val);
                        }
                    }
                    children.add(dataItem);
                }
            }
        }
        transformed.put("itemsSize", rows.size());
        transformed.put("count", rows.size());
    }

    return transformed;
}

}

