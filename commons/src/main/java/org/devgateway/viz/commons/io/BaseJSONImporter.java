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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseJSONImporter<T extends DatasetRecord> extends BaseImport<T, JSONObject> {


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
        JSONObject main = new JSONObject(sb.toString());

        JSONArray languages = main.getJSONArray("languages");
        if (languages != null) {
            languages.forEach(l -> categoryService.createIfNotExist(l.toString(), Language.class));
        }

        JSONArray array = main.getJSONArray("data");
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            T record = read(obj);
            record.setDataset(dataset);
            if (record != null) {
                save(record);
                logger.info("Record counts " + (count[0]++) + " of " + array.length());
            }
        }
    }

    private List<LocaleText> extractJSONField(JSONObject row, String field) {
        try {
            if (row.get(field) != null) {
                List<LocaleText> trns = new ArrayList<>();
                ((JSONObject) row.get(field)).keySet().forEach(k -> {
                    Language l = (Language) categoryService.createIfNotExist(k.toString(), Language.class);
                    trns.add(new LocaleText(((JSONObject) row.get(field)).get(k).toString(), l));
                });
                return trns;
            }
        } catch (JSONException e) {
            logger.error("Error while reading row: " + row + " - " + e.getMessage());
            return null;
        }
        return null;
    }

    public void populateCategory(DatasetRecord entity, Class<?> clazz, Method method, JSONObject row, String field) {
        try {
            List<LocaleText> translations = extractJSONField(row, field);
            String value = StringEscapeUtils.unescapeCsv(((JSONObject) row.get(field)).get("en").toString());
            Category category = categoryService.createIfNotExist(value, clazz, translations, true);
            method.invoke(entity, category);
        } catch (Exception e) {
            logger.error("Error while reading row: " + row + " - " + e.getMessage());
        }
    }
}
