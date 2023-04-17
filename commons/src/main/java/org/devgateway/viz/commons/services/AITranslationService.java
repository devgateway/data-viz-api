package org.devgateway.viz.commons.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

@Service
public class AITranslationService {

    @Value("${viz.ai.translation.key}")
    String key;
    private static final Logger logger = LoggerFactory.getLogger(AITranslationService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String translate(String source, String locale) {
        try {

            String url = "https://ai-translate.pro/api/" + "c709529103297402a6e1a0d8953092ed0f6f6ba448798a8fdac0c40eaf49b7d0" + "/en-" + locale.toLowerCase(Locale.ROOT);
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();

            HttpPost request = new HttpPost(url);

            StringEntity entity = new StringEntity(source);
            request.setEntity(entity);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");


            CloseableHttpResponse response = httpClient.execute(request);

            ObjectMapper mapper = new ObjectMapper();

            HashMap<String, String> results = mapper.readValue(EntityUtils.toString(response.getEntity()), HashMap.class);


            response.close();
            logger.info(" translation of " + source + " is " + results.get("result"));
            return results.get("result");

        } catch (IOException e) {
            logger.error("Error when getting translation", e);
            return null;
        }


    }
}
