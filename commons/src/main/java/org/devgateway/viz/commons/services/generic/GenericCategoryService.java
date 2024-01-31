package org.devgateway.viz.commons.services.generic;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang.StringUtils;
import org.devgateway.viz.commons.domain.Category;
import org.devgateway.viz.commons.domain.QCategory;
import org.devgateway.viz.commons.pojo.CategoryResponse;
import org.devgateway.viz.commons.pojo.Response;
import org.devgateway.viz.commons.services.CategoryService;
import org.devgateway.viz.commons.services.FilterDefinitionService;
import org.devgateway.viz.commons.services.Utils;
import org.devgateway.viz.commons.services.generic.utils.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/***
 * @param <R> Jpa Repository of stat entity
 * @param <Q> (QueryDSL Generated )Class of stat entity
 * @param <S> Entity Class
 */
public abstract class GenericCategoryService<R extends JpaRepository, Q extends EntityPathBase, S> {

    protected Q querydslType;

    protected Class<S> annotatedClass;

    @Autowired
    private FieldUtils fieldUtils;

    @Autowired
    private FilterDefinitionService filterDefinitionService;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CategoryService categoryService;

    public GenericCategoryService(Q querydslType, Class<S> annotatedClass) {
        this.querydslType = querydslType;
        this.annotatedClass = annotatedClass;
    }

    @Cacheable("categories")
    public ResponseEntity<Collection<CategoryResponse>> categories(Map<String, String> params) {

        List<Category> categories = categoryService.getAllCategories();

        List<Category> categoriesFiltered = categories.stream().filter(category -> {
            String field = filterDefinitionService.getFieldFromFieldType(category.getType());
            if (category.getType() != null && field != null) {
                BooleanBuilder builder = getFilters(params, querydslType, annotatedClass);
                JPAQuery query = new JPAQuery<>(em);
                query.select(Expressions.path(List.class, querydslType, field + ".id"));
                query.from(querydslType);
                query.where(builder);
                List<Long> dataIds = query.fetch();
                return dataIds.contains(category.getId());
            } else {
                return true;
            }
        }).collect(Collectors.toList());

        HashMap<String, CategoryResponse> categoriesMap = new HashMap<>();
        categoriesFiltered.forEach((Category o) -> {
            if (categoriesMap.get(o.getType()) == null) {
                categoriesMap.put(o.getType(), new CategoryResponse(o.getType()));
            }
            categoriesMap.get(o.getType()).getItems().add(o);
        });

        return new ResponseEntity<>(categoriesMap.values(), HttpStatus.OK);
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

}
