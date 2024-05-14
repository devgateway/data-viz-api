package org.devgateway.viz.commons.services.generic.utils;

import org.devgateway.viz.commons.domain.Language;
import org.devgateway.viz.commons.domain.LocaleText;
import org.devgateway.viz.commons.domain.annotations.Dimension;
import org.devgateway.viz.commons.domain.annotations.Translation;
import org.devgateway.viz.commons.services.CategoryService;
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

    @Autowired
    CategoryService categoryService;

    @Cacheable("utils")
    public List<org.devgateway.viz.commons.pojo.Dimension> getDimensions(Class annotatedClass) {

        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName));

        logger.info("------- " + methodName + " ----");

        List<org.devgateway.viz.commons.pojo.Dimension> list = new ArrayList<>();
        for (Field field : fieldUtils.getFields(annotatedClass)) {
            if (field.isAnnotationPresent(Dimension.class)) {
                Dimension[] d;
                // Here we can read more complex labels from the annotation like {"en": "Spain", "es": "España"}
                List<LocaleText> translations = new ArrayList<>();
                d = field.getAnnotationsByType(Dimension.class);
                if (d[0].translations() != null) {
                    for (Translation t : d[0].translations()) {
                        Language l = (Language) categoryService.createIfNotExist(t.lang(), Language.class);
                        translations.add(new LocaleText(t.value(), l));
                    }
                }
                list.add(new org.devgateway.viz.commons.pojo.Dimension(field.getName(), field.getName(), d[0].label(), translations, field.getType().getSimpleName()));
            }
        }
        return list;
    }

}
