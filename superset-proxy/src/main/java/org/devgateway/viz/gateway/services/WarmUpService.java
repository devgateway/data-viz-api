package org.devgateway.viz.gateway.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class WarmUpService {

    private final Logger logger = LoggerFactory.getLogger(WarmUpService.class);

    private final SuperSetProxyService supersetProxyService;
    private final DimensionsService dimensionsService;

    public WarmUpService(SuperSetProxyService supersetProxyService, DimensionsService dimensionsService) {
        this.supersetProxyService = supersetProxyService;
        this.dimensionsService = dimensionsService;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void warmUp() {
        logger.info("Warming up...");

        warmUpETLITSBirthReg();
        warmUpAADGGBirthReg();
        warmUpAnimalsReg();
        warmUpETLITSPopulation();
        warmUpESSTrends();

        logger.info("Finished warming up.");
    }

    private void warmUpAADGGBirthReg() {
        logger.info("Dataset 66: PG - AADGG - Animal Registration Data (10 years)");

        Set<String> fullFilterColumns = Set.of("birth_year", "primary_breed_name", "owner_region");

        getCategories("66");

        getStats("66");
        getStats("66", fullFilterColumns);
        getStats("66", Map.of("sex", "Female"), fullFilterColumns);
        getStats("66", Map.of("sex", "Male"), fullFilterColumns);
        getStats("66", "birth_year/primary_breed_name", fullFilterColumns);
        getStats("66", "birth_year/sex", fullFilterColumns);
        getStats("66", "owner_region_code", fullFilterColumns);
        getStats("66", "owner_zone_code", fullFilterColumns);
        getStats("66", "sex/primary_breed_name", fullFilterColumns);
    }

    private void warmUpESSTrends() {
        logger.info("Dataset 61: PG-ESS-Trends");

        getStats("61", "year/species");

        getStats("61", "year/sex", Map.of("species", "Cattle"));
        getStats("61", "year/sex", Map.of("species", "Sheep"));
        getStats("61", "year/sex", Map.of("species", "Goat"));
        getStats("61", "year/sex", Map.of("species", "Camel"));

        String lastYear = dimensionsService.fetchDistinctDimensionValues("year", "61").stream()
                .max(String::compareTo)
                .orElse(Year.now().toString());

        getStats("61", "region_code/species", Map.of("year", lastYear));

        getStats("61", "region_code/species", Map.of("species", "Cattle", "year", lastYear));
        getStats("61", "region_code/species", Map.of("species", "Sheep", "year", lastYear));
        getStats("61", "region_code/species", Map.of("species", "Goat", "year", lastYear));
        getStats("61", "region_code/species", Map.of("species", "Camel", "year", lastYear));

        getStats("61", "region/species", Map.of("species", "Cattle", "year", lastYear));
        getStats("61", "region/species", Map.of("species", "Sheep", "year", lastYear));
        getStats("61", "region/species", Map.of("species", "Goat", "year", lastYear));
        getStats("61", "region/species", Map.of("species", "Camel", "year", lastYear));
    }

    private void warmUpAnimalsReg() {
        logger.info("Dataset 70: Public - ET-LITS & AADGG - All Animals Registered and Tagged");

        getCategories("70");
        getStats("70", "owner_zone_code");
    }

    private void warmUpETLITSPopulation() {
        logger.info("Dataset 50: Public - ET-LITS - Population by Species");

        getStats("50", Map.of("species", "Cattle"));
        getStats("50", Map.of("species", "Sheep"));
        getStats("50", Map.of("species", "Goat"));
        getStats("50", Map.of("species", "Camel"));
        getStats("50", Set.of("species"));
    }

    private void warmUpETLITSBirthReg() {
        logger.info("Dataset 43: PG-ETLITS-Birth Registration Data 10 years");

        Set<String> fullFilterColumns = Set.of("species", "birth_year", "sex", "birth_region");

        getCategories("43");

        getStats("43", fullFilterColumns);
        getStats("43", Map.of("species", "Cattle"));
        getStats("43", "birth_year/primary_breed_name", fullFilterColumns);
        getStats("43", "birth_year/species", fullFilterColumns);
        getStats("43", "owner_region_code", fullFilterColumns);
        getStats("43", "primary_breed_name", fullFilterColumns);
    }

    private void getStats(String datasetId) {
        getStats(datasetId, Map.of());
    }

    private void getStats(String datasetId, Map<String, String> queryParams) {
        getStats(datasetId, "", queryParams);
    }

    private void getStats(String datasetId, String groupsPath) {
        getStats(datasetId, groupsPath, Map.of());
    }

    private void getStats(String datasetId, String groupsPath, Map<String, String> queryParams) {
        getStats(datasetId, groupsPath, queryParams, Set.of());
    }

    private void getStats(String datasetId, Set<String> fullFilterColumns) {
        getStats(datasetId, "", fullFilterColumns);
    }

    private void getStats(String datasetId, String groupsPath, Set<String> fullFilterColumns) {
        getStats(datasetId, groupsPath, Map.of(), fullFilterColumns);
    }

    private void getStats(String datasetId, Map<String, String> queryParams, Set<String> fullFilterColumns) {
        getStats(datasetId, "", queryParams, fullFilterColumns);
    }

    private void getStats(String datasetId, String groupsPath, Map<String, String> queryParams, Set<String> fullFilterColumns) {
        Map<String, String> fullParams = new LinkedHashMap<>(queryParams);
        fullParams.put("dvzProxyDatasetId", datasetId);
        fullParams.putAll(fullFilters(datasetId, fullFilterColumns));

        String fullParamsOneLine = fullParams.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .sorted()
                .collect(Collectors.joining(";"));
        logger.info("Fetching stats for dataset: " + datasetId + " with groupsPath: " + groupsPath + " and params: " + fullParamsOneLine);

        supersetProxyService.getStats(datasetId, fullParams, groupsPath);
    }

    private Map<String, String> fullFilters(String datasetId, Set<String> fields) {
        Map<String, String> fullParams = new LinkedHashMap<>();
        for (String field : fields) {
            fullParams.put(field, String.join(",", new TreeSet<>(dimensionsService.fetchDistinctDimensionValues(field, datasetId))));
        }
        return fullParams;
    }

    private void getCategories(String datasetId) {
        logger.info("Fetching categories for dataset: " + datasetId);
        supersetProxyService.getCategories(datasetId);
    }
}
