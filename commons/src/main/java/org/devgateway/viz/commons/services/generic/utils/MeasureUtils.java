package org.devgateway.viz.commons.services.generic.utils;

import org.devgateway.viz.commons.domain.Language;
import org.devgateway.viz.commons.domain.LocaleText;
import org.devgateway.viz.commons.domain.annotations.Dimension;
import org.devgateway.viz.commons.domain.annotations.Measures;
import org.devgateway.viz.commons.domain.annotations.Translation;
import org.devgateway.viz.commons.pojo.Measure;
import org.devgateway.viz.commons.pojo.Translatable;
import org.devgateway.viz.commons.services.CategoryService;
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

    @Autowired
    CategoryService categoryService;

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
                Arrays.stream(ms.values()).forEach(m -> {
                    List<LocaleText> translations = new ArrayList<>();
                    if (m.translations() != null) {
                        for (Translation t : m.translations()) {
                            Language l = (Language) categoryService.createIfNotExist(t.lang(), Language.class);
                            translations.add(new LocaleText(t.value(), l));
                        }
                    }
                    Measure measure = new Measure(
                            m.name(),
                            m.label(),
                            m.expression(),
                            m.delegate(),
                            new Translatable(m.group()),
                            field.getName(), filter,
                            m.position(),
                            m.color(),
                            translations);
                    measures.add(measure);
                });
            }
        }
        return measures;
    }

}
