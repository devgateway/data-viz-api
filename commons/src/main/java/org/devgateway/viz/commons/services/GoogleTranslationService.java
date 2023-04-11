package org.devgateway.viz.commons.services;


import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GoogleTranslationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${google.cloud.key}")
    String key;

    @Value("${google.translation.enable}")
    Boolean enable;

    @Value("${google.translation.app}")
    String app;

    public String translate(String source, String locale) {
        List<TranslationsResource> values = translate(Arrays.asList(source), locale);
        if (values.size() > 0) {
            return values.iterator().next().getTranslatedText();
        }
        return null;
    }

    public List<TranslationsResource> translate(List<String> source, String locale) {
        try {
            if (enable) {
                logger.info("Calling google cloud translations, this operation may produce billing charges ");
                // See comments on
                //   https://developers.google.com/resources/api-libraries/documentation/translate/v2/java/latest/
                // on options to set
                Translate t = new Translate.Builder(com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(), com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance(), null)
                        //Need to update this to your App-Name
                        .setApplicationName(app).build();

                Translate.Translations.List list = t.new Translations().list(source, locale);

                list.setKey(key);
                list.setFormat("text");
                TranslationsListResponse response = list.execute();

                return response.getTranslations();
            } else {
                logger.warn("Translation are disabled!!");
                return new ArrayList<TranslationsResource>();
            }

        } catch (Exception e) {
            logger.error("Error when translating values");
        }
        return null;
    }


}
