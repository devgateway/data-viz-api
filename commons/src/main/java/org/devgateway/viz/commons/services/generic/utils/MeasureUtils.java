package org.devgateway.viz.commons.services.generic.utils;

import org.devgateway.viz.commons.domain.annotations.Measures;
import org.devgateway.viz.commons.pojo.Measure;
import org.devgateway.viz.commons.pojo.Translatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeasureUtils {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    FieldUtils fieldUtils;

    @Cacheable("utils")
    public List<Measure> getMeasures(Class annotatedClass) {
        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName));

         logger.info("-------  " + this.getClass().getSimpleName() + " ------- " + methodName + " ----");


        List<Measure> measures = new ArrayList<>();
        for (Field field : fieldUtils.getFields(annotatedClass)) {
            Measures ms = AnnotationUtils.findAnnotation(field, Measures.class);
            if (ms != null) {

                String filter = fieldUtils.getFilter(field);
                measures.addAll(Arrays.stream(ms.values()).map((measure) ->
                        new Measure(
                                measure.name(),
                                measure.label(),
                                measure.expression(),
                                measure.delegate(),
                                new Translatable(measure.group()),
                                field.getName(), filter,
                                measure.position(),
                                measure.color()
                        )).collect(Collectors.toList()));
            }
        }

        return measures;
    }

}
