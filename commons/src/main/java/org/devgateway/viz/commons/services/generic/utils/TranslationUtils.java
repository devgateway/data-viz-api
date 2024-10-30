package org.devgateway.viz.commons.services.generic.utils;

import org.devgateway.viz.commons.domain.LocaleText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TranslationUtils {
    private static final Logger logger = LoggerFactory.getLogger(TranslationUtils.class);


    public static String getTranslatedLabel(String defaultValue, List<LocaleText> labels, String locale) {
        if (locale == null) {
            logger.info("No locale selected, returing default label");
            return defaultValue;
        }
        List<LocaleText> filtered = labels.stream().filter(localeText -> localeText.getLanguage().getValue().equalsIgnoreCase(locale)).collect(Collectors.toList());
        if (filtered.size() > 0) {

            return filtered.iterator().next().getText();
        } else {
            logger.info("No translation found for " + defaultValue + " returning default value");
            return defaultValue;
        }
    }


}
