package org.devgateway.viz.commons.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import org.devgateway.viz.commons.domain.Category;
import org.devgateway.viz.commons.domain.Dataset;
import org.devgateway.viz.commons.domain.DatasetRecord;
import org.devgateway.viz.commons.domain.Language;
import org.devgateway.viz.commons.domain.LocaleText;
import org.devgateway.viz.commons.services.CategoryService;
import org.devgateway.viz.commons.services.generic.DatasetService;
import org.devgateway.viz.commons.services.generic.FileContentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseJSONImporter<T extends DatasetRecord> extends BaseImport<T, JsonNode> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private DatasetService datasetService;
    private FileContentService fileContentService;

    @Autowired
    CategoryService categoryService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void start(BufferedReader in, Dataset dataset) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        final Integer[] count = new Integer[1];
        count[0] = 0;
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        JsonNode main = MAPPER.readTree(sb.toString());

        JsonNode languages = main.get("languages");
        if (languages != null && languages.isArray()) {
            languages.forEach(l -> categoryService.createIfNotExist(l.asText(), Language.class));
        }

        ArrayNode array = (ArrayNode) main.get("data");
        for (int i = 0; i < array.size(); i++) {
            JsonNode obj = array.get(i);
            T record = read(obj);
            record.setDataset(dataset);
            if (record != null) {
                save(record);
                logger.info("Record counts " + (count[0]++) + " of " + array.size());
            }
        }
    }

    private List<LocaleText> extractJSONField(JsonNode row, String field) {
        try {
            JsonNode fieldNode = row.get(field);
            if (fieldNode != null && !fieldNode.isNull()) {
                List<LocaleText> trns = new ArrayList<>();
                fieldNode.fields().forEachRemaining(entry -> {
                    Language l = (Language) categoryService.createIfNotExist(entry.getKey(), Language.class);
                    trns.add(new LocaleText(entry.getValue().asText(), l));
                });
                return trns;
            }
        } catch (Exception e) {
            logger.error("Error while reading row: " + row + " - " + e.getMessage());
            return null;
        }
        return null;
    }

    public void populateCategory(DatasetRecord entity, Class<?> clazz, Method method, JsonNode row, String field) {
        try {
            List<LocaleText> translations = extractJSONField(row, field);
            String value = StringEscapeUtils.unescapeCsv(row.get(field).get("en").asText());
            Category category = categoryService.createIfNotExist(value, clazz, translations, true);
            method.invoke(entity, category);
        } catch (Exception e) {
            logger.error("Error while reading row: " + row + " - " + e.getMessage());
        }
    }
}
