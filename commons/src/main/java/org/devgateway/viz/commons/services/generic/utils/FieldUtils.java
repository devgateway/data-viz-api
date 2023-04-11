package org.devgateway.viz.commons.services.generic.utils;

import org.devgateway.viz.commons.domain.annotations.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class FieldUtils {

    private static final Logger logger = LoggerFactory.getLogger(FieldUtils.class.getClass());

    @Cacheable("utils")
    public Field[] getFields(Class annotatedClass) {

        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName));

        logger.info("------- "+methodName + " ----");

        Field[] classFields = annotatedClass.getDeclaredFields();
        Field[] parentField = annotatedClass.getSuperclass().getDeclaredFields();

        ArrayList<Field> all = new ArrayList<>();
        Collections.addAll(all, classFields);
        Collections.addAll(all, parentField);

        return all.toArray(new Field[all.size()]);
    }

    @Cacheable("utils")
    public Field getFieldByName(String field, Class annotatedClass) {
        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName));

        logger.info("------- "+methodName + " ----");

        Field[] all = getFields(annotatedClass);
        List<Field> filtered = Arrays.stream(all).filter(field1 -> field1.getName().equalsIgnoreCase(field)).collect(Collectors.toList());
        return filtered.isEmpty() ? null : filtered.iterator().next();
    }

    /**
     * If the field involved in the measure is also a filter -> returns filter param name
     **/
    @Cacheable("utils")
    public String getFilter(Field field) {
        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName));

        logger.info("------- "+methodName + " ----");

        if (field.isAnnotationPresent(Filter.class)) {
            Filter[] f = field.getAnnotationsByType(Filter.class);
            return f[0].param();
        }

        return null;
    }
}
