package org.devgateway.viz.gateway.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.devgateway.viz.gateway.common.Constants;
import org.devgateway.viz.gateway.services.rest.SuperSetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.devgateway.viz.gateway.services.Utils.*;

@Service
public class StatsService {
    private final Logger logger = LoggerFactory.getLogger(StatsService.class);

    private final SuperSetClient superSetClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public StatsService(SuperSetClient superSetClient) {
        this.superSetClient = superSetClient;
        this.objectMapper = new ObjectMapper();
    }

    public Object getStats(String datasetId, Map<String, String> queryParams, String groupsPath) {

        logger.info("Getting stats for datasetId: " + datasetId);

        if (datasetId == null || datasetId.equalsIgnoreCase("null") || datasetId.isEmpty()) {
            return Collections.emptyList();
        }

        JsonNode datasetResult = getDatasetResult(datasetId);
        if (datasetResult == null) {
            logger.warn("Dataset metadata not found for datasetId: {}", datasetId);
            return Collections.emptyList();
        }

        Map<String, String> labelMap = buildLabelMap(datasetResult);
        JsonNode requestBody = buildSupersetDataRequest(datasetResult, datasetId, queryParams, groupsPath);

        boolean force = "true".equals(queryParams.get("force"));

        //logger.info("Superset request body: " + requestBody.toPrettyString());

        ObjectNode supersetResponse = superSetClient.postChartData(requestBody, force);

        boolean isCached = supersetResponse.get("isCached").asBoolean();
        String cachedAt = supersetResponse.get("cachedAt").asText();
        JsonNode resultArray = supersetResponse.get("result");

        if (resultArray == null || !resultArray.isArray() || resultArray.isEmpty()) {
            logger.warn("Superset response is empty or not an array");
            return Collections.emptyList();
        }

        JsonNode queriesNode = requestBody.get("queries");
        if (queriesNode.isArray() && !queriesNode.isEmpty()) {
            return transformData(resultArray,
                    queriesNode.get(0).get("groupby"),
                    queriesNode.get(0).get("metrics"),
                    getGroupsArray(groupsPath),
                    labelMap,
                    isCached,
                    cachedAt);
        }
        logger.warn("Returning an Empty list");
        return Collections.emptyList();
    }

    private JsonNode getDatasetResult(String datasetId) {
        JsonNode dataset = superSetClient.fetchDataset(datasetId);
        if (dataset == null || !dataset.has("result")) {
            return null;
        }
        return dataset.get("result");
    }

    private Map<String, String> buildLabelMap(JsonNode datasetResult) {
        Map<String, String> labels = new HashMap<>();

        JsonNode columns = datasetResult.get("columns");
        if (columns != null && columns.isArray()) {
            for (JsonNode column : columns) {
                String internalName = column.path("column_name").asText(null);
                if (internalName != null && !internalName.isEmpty()) {
                    String label = column.path("verbose_name").isTextual() && !column.path("verbose_name").asText().isEmpty()
                            ? column.path("verbose_name").asText()
                            : internalName;
                    labels.put(internalName, label);
                }
            }
        }

        JsonNode metrics = datasetResult.get("metrics");
        if (metrics != null && metrics.isArray()) {
            for (JsonNode metric : metrics) {
                String internalName = metric.path("metric_name").asText(null);
                if (internalName != null && !internalName.isEmpty()) {
                    String label = metric.path("verbose_name").isTextual() && !metric.path("verbose_name").asText().isEmpty()
                            ? metric.path("verbose_name").asText()
                            : internalName;
                    labels.put(internalName, label);
                }
            }
        }

        return labels;
    }

    private JsonNode buildSupersetDataRequest(JsonNode datasetResult, String datasetId, Map<String, String> queryParams, String groupsPath) {

        List<String> measuresArr = extractUniqueMeasures(datasetResult);
        Set<String> filterableColumns = extractFilterableColumns(datasetResult);

        Map<String, Object> datasource = createDatasource(datasetId);

        List<Map<String, Object>> queries = new ArrayList<>();

        String[] groupsArray = getGroupsArray(groupsPath);

        if (groupsArray.length > 0) {
            Map<String, Object> queryForDimension1 = createQuery(new String[]{groupsArray[0]}, measuresArr, filterableColumns, queryParams);
            queries.add(queryForDimension1);
        }

        if (groupsArray.length > 1) {
            Map<String, Object> queryForDimension2 = createQuery(groupsArray, measuresArr, filterableColumns, queryParams);
            queries.add(queryForDimension2);
        }

        //add query for overall data
        Map<String, Object> queryForOverall = createQuery(new String[]{}, measuresArr, filterableColumns, queryParams);
        queries.add(queryForOverall);

        return objectMapper.valueToTree(createSupersetRequest(datasource, queries));
    }

    private Object transformData(JsonNode data, JsonNode dimensionsNode, JsonNode metricsNode, String[] dimensionsArray,
                                 Map<String, String> labelMap, boolean isCached, String cachedAt) {
        Map<String, Object> transformed = createTransformedData();
        List<Map<String, Object>> measuresList = new ArrayList<>();
        List<Map<String, Object>> typesList = new ArrayList<>();
        transformed.put("metadata", createMetadata(measuresList, typesList));
        transformed.put("children", new ArrayList<>());
        transformed.put("isCached", isCached);
        transformed.put("cachedAt", cachedAt);

        JsonNode resultsForFirstDimension = data.get(0);
        if (resultsForFirstDimension == null || !resultsForFirstDimension.has("data")) {
            return transformed;
        }

        for (JsonNode metric : metricsNode) {
            String metricName = metric.asText();
            measuresList.add(createMeasure(metricName, labelMap.getOrDefault(metricName, metricName)));
        }

        List<String> dimList = Arrays.asList(dimensionsArray);
        for (String dim : dimList) {
            typesList.add(createType(dim));
        }

        JsonNode overallData = data.get(data.size() - 1);
        if (overallData != null && overallData.has("data")) {
            for (JsonNode metric : metricsNode) {
                transformed.put(metric.asText(), overallData.get("data").get(0).get(metric.asText()).asDouble());
            }

            JsonNode dim1Data = data.get(0);

            transformed.put("itemsSize", dim1Data.get("rowcount").asInt());
        }

        if (dimList.isEmpty()) {
            return transformed;
        }
        String firstDim = dimensionsArray[0];
        for (JsonNode row : resultsForFirstDimension.get("data")) {
            if (row.has(firstDim)) {
                typesList.stream()
                        .filter(t -> t.get("dimension").equals(firstDim))
                        .findFirst()
                        .ifPresent(type -> ((Set<Map<String, Object>>) type.computeIfAbsent("items", k -> new HashSet<>()))
                                .add(createItem(firstDim, row.get(firstDim).asText(), Constants.COLORS.get(0))));

                Map<String, Object> dataItem = createDataItem(firstDim, row.get(firstDim).asText(), metricsNode, row);
                ((List<Map<String, Object>>) transformed.get("children")).add(dataItem);
            }
        }

        if (dimList.size() == 2) {
            JsonNode resultsForSecondDimension = data.get(1);
            if (resultsForSecondDimension != null && resultsForSecondDimension.has("data")) {
                String secondDim = dimensionsArray[1];
                for (JsonNode row : resultsForSecondDimension.get("data")) {
                    if (row.has(secondDim)) {
                        typesList.stream()
                                .filter(t -> t.get("dimension").equals(secondDim))
                                .findFirst()
                                .ifPresent(type -> ((Set<Map<String, Object>>) type.computeIfAbsent("items", k -> new HashSet<>()))
                                        .add(createItem(secondDim, row.get(secondDim).asText(), Constants.COLORS.get(0))));

                        Map<String, Object> dataItem = createDataItem(secondDim, row.get(secondDim).asText(), metricsNode, row);
                        for (Map<String, Object> child : (List<Map<String, Object>>) transformed.get("children")) {
                            if (child.get("value").equals(row.get(firstDim).asText())) {
                                ((List<Map<String, Object>>) child.computeIfAbsent("children", k -> new ArrayList<>())).add(dataItem);
                            }
                        }
                    }
                }
            }
        }

        return transformed;
    }

    public void warmUp() {
        superSetClient.warmUp();
    }
}