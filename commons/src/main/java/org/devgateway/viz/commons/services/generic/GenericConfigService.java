package org.devgateway.viz.commons.services.generic;

import org.apache.commons.lang3.StringUtils;
import org.devgateway.viz.commons.domain.Styles;
import org.devgateway.viz.commons.pojo.Dimension;
import org.devgateway.viz.commons.pojo.Filter;
import org.devgateway.viz.commons.pojo.Measure;
import org.devgateway.viz.commons.services.DimensionDefinitionService;
import org.devgateway.viz.commons.services.FilterDefinitionService;
import org.devgateway.viz.commons.services.MeasureDefinitionService;
import org.devgateway.viz.commons.services.generic.delegates.CachedDelegatedComputation;
import org.devgateway.viz.commons.services.generic.utils.DimensionUtils;
import org.devgateway.viz.commons.services.generic.utils.FilterUtils;
import org.devgateway.viz.commons.services.generic.utils.MeasureUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Abstract configuration service that is used to retrieve the configuration of dimensions, filters and measures.
 * If the configuration is not found in the database, it will be retrieved from the entity class based on the annotations.
 * <p>
 * Any new dimension, filter or measure will be automatically added to the database.
 *
 * @param <S>
 */
@Service
public abstract class GenericConfigService<S> {

    private static final Logger logger = LoggerFactory.getLogger(GenericConfigService.class);

    protected Class<S> annotatedClass;
    @Value("${tcdi.translations.enabled}")
    private boolean isTranslationEnabled;

    public GenericConfigService(Class<S> annotatedClass) {
        this.annotatedClass = annotatedClass;
    }

    @Autowired
    private DimensionDefinitionService dimensionDefinitionService;

    @Autowired
    private FilterDefinitionService filterDefinitionService;

    @Autowired
    MeasureDefinitionService measureDefinitionService;

    @Autowired
    private DimensionUtils dimensionUtils;

    @Autowired
    private MeasureUtils measureUtils;

    @Autowired
    private FilterUtils filterUtils;

    @Autowired
    private CachedDelegatedComputation cachedDelegatedComputation;

    public void synchronizeConfigMetadata() {
        synchronizeCategories();
        synchronizeMeasures();
        synchronizeDimensions();
        synchronizeFilters();
        if (isTranslationEnabled) {
            logger.info("Translation is enabled, calling translate() method");
            translate();
        }
    }

    protected abstract void synchronizeCategories();

    protected void synchronizeMeasures() {
        List<String> dbMeasures = measureDefinitionService.getAllMeasureDefinitions().stream().map(m -> m.getCode())
                .collect(Collectors.toList());

        List<Measure> measures = measureUtils.getMeasures(annotatedClass);

        for (Measure m : measures) {
            if (!dbMeasures.contains(m.getValue())) {
                measureDefinitionService.createIfNotExists(
                        m.getValue(),
                        m.getLabel(),
                        m.getField(),
                        m.getExpression(),
                        m.getDelegate().getCanonicalName().equals("void") ? null : m.getDelegate().getCanonicalName(),
                        m.getFilter(),
                        m.getGroup().getLabel(),
                        m.getPosition(),
                        new Styles(m.getStyles().getColor()));
            }
        }
    }


    protected void synchronizeDimensions() {
        Map<String, String> dbDimensions = dimensionDefinitionService.getAllDimensionDefinitions().stream()
                .collect(Collectors.toMap(d -> d.getCode(), d -> d.getFieldType()));
        List<Dimension> dimensions = dimensionUtils.getDimensions(annotatedClass);

        for (Dimension d : dimensions) {
            if (!dbDimensions.containsKey(d.getValue())) {
                dimensionDefinitionService.createDimensionDefinitionIfNotExists(d.getField(), d.getValue(), d.getLabel(), d.getType());
            } else {
                if (!StringUtils.equalsIgnoreCase(dbDimensions.get(d.getValue()), d.getType())) {
                    throw new RuntimeException("Dimension " + d.getLabel() + " has a different type in the database");
                }
            }
        }
    }

    protected void synchronizeFilters() {
        Map<String, String> dbFilters = filterDefinitionService.getAllFilterDefinitions().stream()
                .collect(Collectors.toMap(f -> f.getParam(), f -> f.getFieldType()));

        List<Filter> filters = filterUtils.getFilters(annotatedClass);

        for (Filter f : filters) {
            //we will only create from code during first execution
            filterDefinitionService.createFilterDefinitionIfNotExist(f.getParam(), f.getLabel(), f.getType(), f.getField());

            if (dbFilters.get(f.getParam()) != null && !dbFilters.get(f.getParam()).equalsIgnoreCase(f.getType())) {
                logger.info("Filter " + f.getParam() + " has a different type in the database");
            }

        }
    }

    protected void translate() {
    }

}
