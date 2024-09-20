package org.devgateway.viz.commons.services.generic;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import org.devgateway.viz.commons.domain.Category;
import org.devgateway.viz.commons.domain.QCategory;
import org.devgateway.viz.commons.pojo.*;
import org.devgateway.viz.commons.services.DimensionDefinitionService;
import org.devgateway.viz.commons.services.MeasureDefinitionService;
import org.devgateway.viz.commons.services.generic.delegates.CachedDelegatedComputation;
import org.devgateway.viz.commons.services.generic.delegates.Delegate;
import org.devgateway.viz.commons.services.generic.utils.FieldUtils;
import org.devgateway.viz.commons.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/***
 * @author sdimunzio
 * @param <R> Jpa Repository of stat entity
 * @param <Q> (QueryDSL Generated )Class of stat entity
 * @param <S> Entity Class
 */
public abstract class GenericStatsAPIServiceBase<R extends JpaRepository, Q extends EntityPathBase, S> {

    public static final String TOTAL = "total";

    public static final String ITEMS_SIZE = "itemsSize";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    MeasureDefinitionService measureDefinitionService;

    @Autowired
    DimensionDefinitionService dimensionDefinitionService;

    protected Q querydslType;

    protected Class<S> annotatedClass;

    private HashMap<String, Delegate> delegates = new HashMap<>();

    @Autowired
    private StatsDSL statsDSL;

    @Autowired
    private FieldUtils fieldUtils;

    @Autowired
    private CachedDelegatedComputation cachedDelegatedComputation;

    public GenericStatsAPIServiceBase(Q querydslType, Class<S> annotatedClass) {
        this.querydslType = querydslType;
        this.annotatedClass = annotatedClass;
    }

    @Cacheable("stats")
    public Response stats(Map<String, String> params, List<String> dimensionsNames) {
        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames.findFirst().map(StackWalker.StackFrame::getMethodName));

        logger.info("-------  " + this.getClass().getSimpleName() + " ------- " + methodName + " ----");

        final String locale = params.get("locale");
        if (locale != null) {
            logger.info("Language set to " + params.get("locale"));
        }


        List<MeasureMetadata> measures = measureDefinitionService.getMeasuresMetadata();
        //List<Measure> measures = measureDefinitionService.getMeasures().stream().filter(measure -> measure.getEnabled()!=Boolean.FALSE).collect(Collectors.toList());

        List<MeasureMetadata> delegatedMeasures = measures.stream().filter(measure -> measure.isDelegated()).collect(Collectors.toList());

        List<MeasureMetadata> nonDelegatedMeasures = measures.stream().filter(measure -> !measure.isDelegated()).collect(Collectors.toList());

        /*Collect and create deletates */
        delegatedMeasures.stream().forEach(measure -> {
            try {
                if (delegates.get(measure.getValue()) == null) {
                    if (delegates.get(measure) == null) {

                        Delegate d = measure.getDelegate().getDeclaredConstructor(String.class, StatsDSL.class).newInstance(measure.getFilter(), statsDSL);
                        delegates.put(measure.getValue(), d);

                    }
                }
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                logger.error("Error when trying to construct a delegate");
            }
        });

        // List<Dimension> dimensions = dimensionDefinitionService.getDimensionsByNames(dimensionsNames);

        if (dimensionsNames.size() != dimensionsNames.size()) {
            logger.warn("Not valid dimensionsNames provided");
            return new Response();
        }

        Integer i = 0;
        HashMap<String, Response> map = new HashMap<>();
        Response root = new Response();

        //Get expressions from non delegated measures
        List<Expression> expressions = nonDelegatedMeasures.stream().map(measure -> strToExpression(measure.getExpression(), measure.getField())).collect(Collectors.toList());
        //Compute values for expressions
        Tuple sum = statsDSL.computeStats(expressions, params, querydslType, annotatedClass);
        int idx = 0;
        for (MeasureMetadata s : measures.stream().filter(measure -> !measure.isDelegated()).collect(Collectors.toList())) {
            root.addAttrValues(new AttrValue(s.getValue(), sum.get(idx++, Double.class)));
        }
        root.setType(TOTAL);
        root.setValue(TOTAL);


        /*Compute delegated values*/
        delegatedMeasures.stream().forEach(measure -> {
            // Number total = delegates.get(measure.getValue()).computeStats(params);
            Number total = cachedDelegatedComputation.computeStats(delegates.get(measure.getValue()), params);
            root.addAttrValues(new AttrValue(measure.getValue(), total));
        });

        map.put("", root);
        List<Dimension> sub = new ArrayList<>();

        while (sub.size() < dimensionsNames.size()) {
            String name = dimensionsNames.get(i++);
            //Dimension d = dimensions.get(i++);
            Dimension d = dimensionDefinitionService.getDimensionsByName(name);
            sub.add(d);
            HashMap<String, Tuple> totals = statsDSL.computeStats(expressions, params, sub, querydslType, annotatedClass);
            // Compute delegates
            final HashMap<String, HashMap> subValues = new HashMap<>();

            delegatedMeasures.stream().forEach(measure -> {
                subValues.put(measure.getValue(), delegates.get(measure.getValue()).computeStats(totals.keySet(), params, sub));
            });

            for (String tKey : totals.keySet()) {
                Tuple tuple = totals.get(tKey);
                Response r = new Response();
                r.setType(d.getValue());
                r.setValue(tuple.get(tuple.size() - expressions.size() - 1, String.class));
                int ix = sub.size();

                //                                                   Dimension, First Measure,  Second Measure,
                //tuple contains dimensions and all measures values  Female,    11845,          48334436,
                for (MeasureMetadata s : nonDelegatedMeasures) {
                    //Add measure value to response object
                    r.addAttrValues(new AttrValue(s.getValue(), tuple.get(ix++, Double.class)));
                }
                for (MeasureMetadata s : delegatedMeasures) {
                    HashMap<String, Number> vals = subValues.get(s.getValue());
                    //Add measure value to response object
                    r.addAttrValues(new AttrValue(s.getValue(), vals.get(tKey)));
                }

                String parentKey = "";
                //parent key is always the last key before expressions
                for (int j = 0; j < tuple.size() - expressions.size() - 1; j++) {
                    parentKey = parentKey.concat(tuple.get(j, Object.class).toString()).toUpperCase();
                }
                Response parent = map.get(parentKey);
                parent.addChild(r);
                String key = "";
                for (int j = 0; j < tuple.size() - expressions.size(); j++) {
                    key = key.concat(tuple.get(j, Object.class).toString()).toUpperCase();
                }
                logger.info("_key =" + key);
                map.put(key, r);
            }
        }

        //computed custom measures
        root.addAttrValues(new AttrValue("metadata", getMetadata(sub)));

        root.addAttrValues(new AttrValue(ITEMS_SIZE, statsDSL.getItemsSize(params, querydslType, annotatedClass)));
        return root;
    }

    private HashMap<String, Object> getMetadata(List<Dimension> dimensions) {
        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames.findFirst().map(StackWalker.StackFrame::getMethodName));

        logger.info("-------  " + this.getClass().getSimpleName() + " ------- " + methodName + " ----");
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("measures", measureDefinitionService.getMeasures());
        metadata.put("types", getDimensionCategories(dimensions));
        return metadata;
    }

    public List<Type> getDimensionCategories(List<Dimension> ds) {
        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames.findFirst().map(StackWalker.StackFrame::getMethodName));

        logger.info("-------  " + this.getClass().getSimpleName() + " ------- " + methodName + " ----");
        return ds.stream().map(s -> {
            String cat = s.getType();
            JPAQuery query = new JPAQuery<>(em);
            query.select(QCategory.category).from(QCategory.category).where(QCategory.category.type.equalsIgnoreCase(cat));
            List<Category> values = query.createQuery().getResultList();
            //List<AttrValue> styles = values.stream().map(category -> new AttrValue(category.getValue(), category.getCategoryStyle(), category.getLabels())).collect(Collectors.toList());
            return new Type(s.getValue(), cat, values);
        }).collect(Collectors.toList());
    }

    private Expression strToExpression(String expresion, String field) {
        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames.findFirst().map(StackWalker.StackFrame::getMethodName));

        logger.info("-------  " + this.getClass().getSimpleName() + " ------- " + methodName + " ----");
        try {
            Field f = fieldUtils.getFieldByName(field, annotatedClass);
            if (f.getType().isAssignableFrom(Long.class) || f.getType().isAssignableFrom(Integer.class) || f.getType().isAssignableFrom(Double.class)) {

                NumberPath numberPath = Expressions.numberPath(Double.class, querydslType, field);
                return (Expression) numberPath.getClass().getMethod(expresion).invoke(numberPath);
            }

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Error when creating expression", e);
        }
        //TODO: throws an exception
        return null;
    }
}

