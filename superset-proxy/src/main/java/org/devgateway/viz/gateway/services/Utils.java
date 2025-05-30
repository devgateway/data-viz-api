package org.devgateway.viz.gateway.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.devgateway.viz.gateway.common.Constants;

import java.util.*;

public class Utils {

    public static Map<String, Object> createDatasource(String datasetId) {
        Map<String, Object> datasource = new HashMap<>();
        datasource.put("id", datasetId);
        datasource.put("type", "table");
        return datasource;
    }


    public static Map<String, Object> createQuery(String field) {
        Map<String, Object> query = new HashMap<>();
        if (field != null && !field.isEmpty()) {
            query.put("groupby", Collections.singletonList(field));
            query.put("columns", Collections.emptyList());
        }

        query.put("metrics", Collections.emptyList());
        return query;
    }

    public static Map<String, Object> createQuery(String[] groupArray, List<String> measuresArr,
            Set<String> filterableColumns, Map<String, String> queryParams) {
        SortedMap<String, Object> query = new TreeMap<>(); // must be sorted to ensure consistent order for caching purposes
        if (groupArray.length > 0) {
            query.put("columns", Arrays.asList(groupArray));
        }
        query.put("metrics", measuresArr);
        query.put("row_limit", Constants.ROW_LIMIT);

        List<Map<String, Object>> filters = new ArrayList<>();
        SortedMap<String, String> sortedQueryParams = new TreeMap<>(queryParams); // for consistent order, needed for caching
        for (Map.Entry<String, String> entry : sortedQueryParams.entrySet()) {
            String columnName = entry.getKey();
            if (!Constants.SPECIAL_PARAMS.contains(columnName) && filterableColumns.contains(columnName)) {
                //remove entry value if it is equals to -9007199254740991
                List values = Arrays.asList(entry.getValue().split(",")).stream().filter(
                        value -> !value.equals("-9007199254740991")).toList();
                if (!values.isEmpty()) {
                    Map<String, Object> filter = new HashMap<>();
                    filter.put("col", columnName);
                    filter.put("op", "in");
                    filter.put("val", Arrays.asList(entry.getValue().split(",")));
                    filters.add(filter);
                }
            }
        }
        query.put("filters", filters);
        return query;
    }

    public static Map<String, Object> createSupersetRequest(Map<String, Object> datasource, List<Map<String, Object>> queries) {
        Map<String, Object> supersetRequest = new HashMap<>();
        supersetRequest.put("datasource", datasource);
        supersetRequest.put("queries", queries);
        supersetRequest.put("force", "true");
        return supersetRequest;
    }

    public static Map<String, Object> createTransformedData() {
        Map<String, Object> transformed = new HashMap<>();
        transformed.put("type", "total");
        transformed.put("value", "total");
        transformed.put("count", 0);
        return transformed;
    }

    public static Map<String, Object> createMetadata(List<Map<String, Object>> measuresList, List<Map<String, Object>> typesList) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("measures", measuresList);
        metadata.put("types", typesList);
        return metadata;
    }

    public static Map<String, Object> createMeasure(JsonNode metric) {
        String label = metric.get("verbose_name") != null ? metric.get("verbose_name").asText() : metric.get("metric_name").asText();
        return createMeasure(metric.get("metric_name").asText(), label);
    }

    public static Map<String, Object> createMeasure(String name, String label) {
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

    public static Map<String, Object> createGroup(String label) {
        Map<String, Object> group = new HashMap<>();
        group.put("label", label);
        group.put("labels", new HashMap<>());
        return group;
    }

    public static Map<String, Object> createStyles(String color) {
        Map<String, Object> styles = new HashMap<>();
        styles.put("color", color);
        return styles;
    }

    public static Map<String, Object> createType(String dim) {
        Map<String, Object> type = new HashMap<>();
        type.put("dimension", dim);
        type.put("category", dim);
        type.put("items", new HashSet<>());
        return type;
    }

    public static Map<String, Object> createDataItem(String dim, String value, JsonNode metricsNode, JsonNode row) {
        Map<String, Object> dataItem = new HashMap<>();
        dataItem.put("type", dim);
        dataItem.put("value", value);

        for (JsonNode metric : metricsNode) {
            String metricName = metric.asText();
            dataItem.put(metricName, row.hasNonNull(metricName) ? row.get(metricName).asDouble() : null);
        }

        return dataItem;
    }

    public static Map<String, Object> createItem(String field, String value, String color) {
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

    public static String[] getGroupsArray(String groupsPath) {
        return groupsPath != null && !groupsPath.trim().isEmpty() ?
                groupsPath.split("/") : new String[]{};
    }



    public static List<String> extractUniqueMeasures(JsonNode result) {
        List<String> measures = new ArrayList<>();
        for (JsonNode metric : result.get("metrics")) {
            if (metric.has("metric_name")) {
                measures.add(metric.get("metric_name").asText());
            }
        }
        return measures;
    }

    public static Set<String> extractFilterableColumns(JsonNode result) {
        Set<String> columnNames = new LinkedHashSet<>();
        for (JsonNode column : result.get("columns")) {
            if (column.path("filterable").asBoolean(true)) {
                String name = column.path("column_name").asText();
                columnNames.add(name);
            }
        }
        return columnNames;
    }

    public static List<Map<String, Object>> extractDimensions(JsonNode result) {
        List<Map<String, Object>> dimensions = new ArrayList<>();
        for (JsonNode column : result.get("columns")) {
            if (column.path("groupby").asBoolean(false)) {
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
}
