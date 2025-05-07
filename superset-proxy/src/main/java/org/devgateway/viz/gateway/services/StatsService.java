package org.devgateway.viz.gateway.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.devgateway.viz.gateway.common.Constants;
import org.devgateway.viz.gateway.services.rest.SuperSetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.devgateway.viz.gateway.services.Utils.*;

@Service
//@Cacheable("superset")
public class StatsService {
    private final Logger logger = LoggerFactory.getLogger(StatsService.class);

    private final SuperSetClient superSetClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public StatsService(SuperSetClient superSetClient) {
        this.superSetClient = superSetClient;
        this.objectMapper = new ObjectMapper();
    }

    //@Cacheable("stats")
    public Object getStats(String datasetId, Map<String, String> queryParams, String groupsPath) throws Exception {
        if (datasetId == null || datasetId.equalsIgnoreCase("null") || datasetId.isEmpty()) {
            return Collections.emptyList();
        }

        JsonNode requestBody = buildSupersetDataRequest(datasetId, queryParams, groupsPath);

        //TODO log the time taken by the next line
        long startTime = System.nanoTime();
        JsonNode supersetResponse = superSetClient.postChartData(requestBody);
        long endTime = System.nanoTime();
        logger.info("Time taken to fetch data from Superset: " + (endTime - startTime) / 1_000_000 + " ms");

        JsonNode resultArray = supersetResponse.get("result");
        if (resultArray == null || !resultArray.isArray() || resultArray.isEmpty()) {
            return Collections.emptyList();
        }

        JsonNode queriesNode = requestBody.get("queries");
        if (queriesNode.isArray() && !queriesNode.isEmpty()) {
            return transformData(resultArray, queriesNode.get(0).get("groupby"), queriesNode.get(0).get("metrics"),
                    getGroupsArray(groupsPath));
        }
        return Collections.emptyList();
    }

    private JsonNode buildSupersetDataRequest(String datasetId, Map<String, String> queryParams, String groupsPath) {

        List<String> measuresArr = extractUniqueMeasures(superSetClient.fetchDataset(datasetId).get("result"));

        Map<String, Object> datasource = createDatasource(datasetId);

        List<Map<String, Object>> queries = new ArrayList<>();

        String[] groupsArray = getGroupsArray(groupsPath);

        if (groupsArray.length > 0) {
            Map<String, Object> queryForDimension1 = createQuery(new String[]{groupsArray[0]}, measuresArr, queryParams);
            queries.add(queryForDimension1);
        }

        if (groupsArray.length > 1) {
            Map<String, Object> queryForDimension2 = createQuery(groupsArray, measuresArr, queryParams);
            queries.add(queryForDimension2);
        }

        //add query for overall data
        Map<String, Object> queryForOverall = createQuery(new String[]{}, measuresArr, queryParams);
        queries.add(queryForOverall);

        return objectMapper.valueToTree(createSupersetRequest(datasource, queries));
    }

    private Object transformData(JsonNode data, JsonNode dimensionsNode, JsonNode metricsNode, String[] dimensionsArray) {
        Map<String, Object> transformed = createTransformedData();
        List<Map<String, Object>> measuresList = new ArrayList<>();
        List<Map<String, Object>> typesList = new ArrayList<>();
        transformed.put("metadata", createMetadata(measuresList, typesList));
        transformed.put("children", new ArrayList<>());

        JsonNode resultsForFirstDimension = data.get(0);
        if (resultsForFirstDimension == null || !resultsForFirstDimension.has("data")) {
            return transformed;
        }

        for (JsonNode metric : metricsNode) {
            measuresList.add(createMeasure(metric.asText(), metric.asText()));
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
            transformed.put("itemsSize", overallData.get("data").get(0).get("count").asInt());
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


}