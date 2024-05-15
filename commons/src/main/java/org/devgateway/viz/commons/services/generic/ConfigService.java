package org.devgateway.viz.commons.services.generic;

import org.apache.commons.lang3.StringUtils;
import org.devgateway.viz.commons.pojo.Dimension;
import org.devgateway.viz.commons.pojo.Filter;
import org.devgateway.viz.commons.pojo.Measure;
import org.devgateway.viz.commons.services.DimensionDefinitionService;
import org.devgateway.viz.commons.services.FilterDefinitionService;
import org.devgateway.viz.commons.services.MeasureDefinitionService;
import org.devgateway.viz.commons.services.generic.utils.DimensionUtils;
import org.devgateway.viz.commons.services.generic.utils.FilterUtils;
import org.devgateway.viz.commons.services.generic.utils.MeasureUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Abstract configuration service that is used to retrieve the configuration of dimensions, filters and measures.
 * If the configuration is not found in the database, it will be retrieved from the entity class based on the annotations.
 * <p>
 * Any new dimension, filter or measure will be automatically added to the database.
 */

@Service
public class ConfigService {

    private final DimensionDefinitionService dimensionDefinitionService;

    private final FilterDefinitionService filterDefinitionService;

    private final MeasureDefinitionService measureDefinitionService;

    @Autowired
    DimensionUtils dimensionUtils;

    @Autowired
    FilterUtils filterUtils;

    @Autowired
    MeasureUtils measureUtils;

    public ConfigService(DimensionDefinitionService dimensionDefinitionService, FilterDefinitionService filterDefinitionService, MeasureDefinitionService measureDefinitionService) {
        this.dimensionDefinitionService = dimensionDefinitionService;
        this.filterDefinitionService = filterDefinitionService;
        this.measureDefinitionService = measureDefinitionService;

    }

    public void synchronizeConfigMetadata(Class annotatedClass) {
        synchronizeMeasures(annotatedClass);
        synchronizeDimensions(annotatedClass);
        synchronizeFilters(annotatedClass);

    }

    protected void synchronizeMeasures(Class annotatedClass) {
        List<String> dbMeasures = measureDefinitionService.getAllMeasureDefinitions().
                stream().map(m -> m.getValue()).collect(Collectors.toList());
        List<Measure> measures = measureUtils.getMeasures(annotatedClass);

        for (Measure m : measures) {
            if (!dbMeasures.contains(m.getValue())) {
                measureDefinitionService.createIfNotExists(m.getValue(),
                        m.getLabel(), m.getField(), m.getExpression(),
                        m.getDelegate().getCanonicalName(), m.getFilter(), m.getGroup().getLabel(), m.getPosition(), m.getStyles(), m.getLabels());
            }
        }
    }

    protected void synchronizeDimensions(Class annotatedClass) {
        Map<String, String> dbDimensions = dimensionDefinitionService
                .getAllDimensionDefinitions()
                .stream().collect(Collectors.toMap(d -> d.getCode(), d -> d.getFieldType()));

        List<Dimension> dimensions = dimensionUtils.getDimensions(annotatedClass);

        for (Dimension d : dimensions) {
            if (!dbDimensions.containsKey(d.getValue())) {
                dimensionDefinitionService.createDimensionDefinitionIfNotExists(d.getField(), d.getValue(), d.getLabel(), d.getType());
            } else {
                if (!StringUtils.equalsIgnoreCase(dbDimensions.get(d.getValue()), d.getType())) {
                    throw new RuntimeException("Dimension " + d.getValue() + " has a different type in the database");
                }
            }
        }
    }

    protected void synchronizeFilters(Class annotatedClass) {
        Map<String, String> dbFilters = filterDefinitionService
                .getAllFilterDefinitions()
                .stream()
                .collect(Collectors.toMap(f -> f.getParam(), f -> f.getFieldType()));

        List<Filter> filters = filterUtils.getFilters(annotatedClass);

        for (Filter f : filters) {
            if (!dbFilters.containsKey(f.getParam())) {
                filterDefinitionService.createFilterDefinitionIfNotExist(f.getParam(), f.getLabel(), f.getType(), f.getField());
            } else {
                if (!StringUtils.equalsIgnoreCase(dbFilters.get(f.getParam()), f.getType())) {
                    throw new RuntimeException("Filter " + f.getParam() + " has a different type in the database");
                }
            }
        }
    }

}
