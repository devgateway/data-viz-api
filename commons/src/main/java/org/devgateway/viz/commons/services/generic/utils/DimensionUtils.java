package org.devgateway.viz.commons.services.generic.utils;

import org.devgateway.viz.commons.domain.annotations.Dimension;
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
public class DimensionUtils {
    private static final Logger logger = LoggerFactory.getLogger(DimensionUtils.class.getClass());


    @Autowired
    FieldUtils fieldUtils;

    @Cacheable("utils")
    public List<org.devgateway.viz.commons.pojo.Dimension> getDimensions(Class annotatedClass) {

        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName));

        logger.info("------- "+methodName + " ----");

        List<org.devgateway.viz.commons.pojo.Dimension> list = new ArrayList<>();
        for (Field field : fieldUtils.getFields(annotatedClass)) {
            if (field.isAnnotationPresent(Dimension.class)) {
                Dimension[] d;
                // TODO: Here we could read more complex labels from the annotation like {en: "Spain", es: "España"}
                d = field.getAnnotationsByType(Dimension.class);
                list.add(new org.devgateway.viz.commons.pojo.Dimension(field.getName(), field.getName(), d[0].label(),null, field.getType().getSimpleName()));
            }
        }

        return list;
    }

}
