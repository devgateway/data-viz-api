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
public class CategoriesService {
    private final SuperSetClient superSetClient;

    private final DimensionsService dimensionsService;

    private final Logger logger = LoggerFactory.getLogger(StatsService.class);

    private final ObjectMapper objectMapper;

    public CategoriesService(@Autowired SuperSetClient superSetClient, ObjectMapper objectMapper, DimensionsService dimensionsService) {
        this.superSetClient = superSetClient;
        this.objectMapper = objectMapper;
        this.dimensionsService = dimensionsService;

    }

    //@Cacheable(value = "categories", key = "#datasetId")
    public List<Map<String, Object>> getCategories(String datasetId) {
        if (datasetId == null) {
            return Collections.emptyList();
        }

        //get dataset metdata
        JsonNode result = superSetClient.fetchDataset(datasetId).get("result");

        List<Map<String, Object>> dimensions = extractDimensions(result);
        List<String> measures = extractUniqueMeasures(result);
        List<Map<String, Object>> categories = new ArrayList<>();

        for (Map<String, Object> dim : dimensions) {
            String field = (String) dim.get("field");
            Map<String, Object> category = new HashMap<>();
            category.put("type", field);
            List<Map<String, Object>> items = new ArrayList<>();

            for (String value : dimensionsService.fetchDistinctDimensionValues(field, datasetId)) {
                items.add(createItem(field, value,
                        Constants.COLORS.get(items.size() % Constants.COLORS.size())));
            }
            category.put("items", items);
            categories.add(category);
        }
        return categories;
    }


}
