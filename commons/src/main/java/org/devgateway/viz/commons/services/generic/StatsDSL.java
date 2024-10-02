package org.devgateway.viz.commons.services.generic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.devgateway.viz.commons.domain.Category;
import org.devgateway.viz.commons.pojo.Dimension;
import org.devgateway.viz.commons.services.FilterDefinitionService;
import org.devgateway.viz.commons.services.Utils;
import org.devgateway.viz.commons.services.generic.utils.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.jpa.impl.JPAQuery;



@Service
public class StatsDSL<R extends JpaRepository, Q extends EntityPathBase, S> {

    @Autowired
    private FilterDefinitionService filterDefinitionService;

    @Autowired
    private FieldUtils fieldUtils;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager em;

    public Logger getLogger() {
        return logger;
    }

    public SimplePath gePathByDimension(Dimension dimension, Q qEntity, Class entityClass) {
        Field f = fieldUtils.getFieldByName(dimension.getField(), entityClass);
        if (f != null) {
            if (f.getType().getGenericSuperclass().getTypeName().equalsIgnoreCase(Category.class.getTypeName())) {
                return Expressions.path(String.class, qEntity, f.getName() + ".value");

            } else {
                return Expressions.path(String.class, qEntity, f.getName());

            }
        }
        logger.info("Not valid dimension " + dimension);
        return null;
    }

    public SimplePath getOrderPath(Dimension dimension, Q qEntity, Class entityClass) {
        Field f = fieldUtils.getFieldByName(dimension.getField(), entityClass);
        if (f != null) {
            if (f.getType().getGenericSuperclass().getTypeName().equalsIgnoreCase(Category.class.getTypeName())) {
                return Expressions.path(Integer.class, qEntity, f.getName() + ".position");

            } else {
                return Expressions.path(String.class, qEntity, f.getName());

            }
        }
        return null;
    }



    protected BooleanBuilder getFilters(Map<String, String> params, Q qEntity, Class entityClass) {
        BooleanBuilder builder = new BooleanBuilder();

        if (params != null) {
            for (String key : params.keySet()) {
                String field = filterDefinitionService.getField(key);
                if (field != null) {
                    Field f = fieldUtils.getFieldByName(field, entityClass);
                    if (f != null) {
                        String keyParams = params.get(key);
                        if (f != null && StringUtils.isNotBlank(keyParams)) {
                            if (f.getType().getGenericSuperclass().getTypeName().equalsIgnoreCase(Category.class.getTypeName())) {
                                builder.and(Expressions.predicate(Ops.IN, Expressions.path(List.class, qEntity, f.getName() + ".id"), Expressions.constant(Arrays.stream(keyParams.split(",")).map(o -> Long.parseLong(o)).collect(Collectors.toList()))));
                            } else {
                                builder.and(Expressions.predicate(Ops.IN, Expressions.path(List.class, qEntity, f.getName()), Expressions.constant(Utils.parseParams(params.get(key), f.getType()))));

                            }
                        }
                    }
                }
            }
        }
        return builder;

    }

    @Cacheable("stats")
    public Tuple computeStats(List<Expression> expressions, Map<String, String> params, Q qEntity, Class entityClass) {

        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName));

        logger.info("------- " + methodName + " ----");


        BooleanBuilder builder = getFilters(params, qEntity, entityClass);
        JPAQuery query = new JPAQuery<>(em);
        expressions.forEach(e -> query.select(e));
        query.select(expressions.toArray(new Expression[expressions.size()]));
        query.from(qEntity);
        query.where(builder);
        return (Tuple) query.fetchFirst();
    }

    @Cacheable("stats")
    public HashMap<String, Tuple> computeStats(List<Expression> expresions, Map<String, String> params, List<Dimension> dimensions, Q qEntity, Class entityClass) {

        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName));

        logger.info("------- "+methodName + " ----");

        JPAQuery query = new JPAQuery<>(em);
        BooleanBuilder builder = getFilters(params, qEntity, entityClass);
        List<Expression> dimensionPaths = dimensions.stream().map(s -> gePathByDimension(s, qEntity, entityClass)).collect(Collectors.toList());

        if (dimensionPaths.size() > 0) {
            List<Expression> select = new ArrayList<>(dimensionPaths);
            List<Expression> group = new ArrayList<>(dimensionPaths);
            select.addAll(expresions);
            query.select(select.toArray(new Expression[select.size()])).from(qEntity).where(builder);
            if (dimensions.size() > 0) {
                query.orderBy(new OrderSpecifier(Order.ASC, getOrderPath(dimensions.get(dimensions.size() - 1), qEntity, entityClass)));
                group.add(getOrderPath(dimensions.get(dimensions.size() - 1), qEntity, entityClass));
            }
            query.groupBy(group.toArray(new Expression[group.size()]));
            List<Tuple> data = query.fetch();

            LinkedHashMap<String, Tuple> values = new LinkedHashMap<>();
            data.forEach(tuple -> {
                String ks = String.valueOf(Arrays.stream(Arrays.copyOfRange(tuple.toArray(), 0, tuple.size() - expresions.size())).reduce((o, o2) -> o + " - " + o2.toString()).get());
                values.put(ks, tuple);
            });
            return values;
        } else {
            return null;
        }
    }

    @Cacheable("stats")
    public Long getItemsSize(Map<String, String> params, Q qEntity, Class entityClass) {
        BooleanBuilder builder = getFilters(params, qEntity, entityClass);
        JPAQuery query = new JPAQuery<>(em);
        query.from(qEntity);
        query.where(builder);

        return query.fetchCount();
    }

}
