package org.devgateway.viz.commons.services.generic.utils;

import org.devgateway.viz.commons.domain.annotations.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FilterUtils {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    FieldUtils fieldUtils;

    @Cacheable("utils")
    public List<org.devgateway.viz.commons.pojo.Filter> getFilters(Class annotatedClass) {
        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName));

         logger.info("-------  " + this.getClass().getSimpleName() + " ------- " + methodName + " ----");

        List<org.devgateway.viz.commons.pojo.Filter> list = new ArrayList<>();
        for (Field field : fieldUtils.getFields(annotatedClass)) {
            if (field.isAnnotationPresent(Filter.class)) {
                Filter[] d;
                d = field.getAnnotationsByType(Filter.class);

                list.add(new org.devgateway.viz.commons.pojo.Filter(d[0].param(), d[0].label(), null, field.getType().getSimpleName(), field.getName()));
            }
        }
        return list;
    }
}
