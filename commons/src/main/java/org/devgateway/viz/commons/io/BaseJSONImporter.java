package org.devgateway.viz.commons.io;

import java.io.BufferedReader;
import java.io.IOException;
import org.devgateway.viz.commons.domain.Dataset;
import org.devgateway.viz.commons.domain.DatasetRecord;
import org.devgateway.viz.commons.services.generic.DatasetService;
import org.devgateway.viz.commons.services.generic.FileContentService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseJSONImporter<T extends DatasetRecord> extends BaseImport<T, JSONObject> {


    @Autowired
    private DatasetService datasetService;
    private FileContentService fileContentService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void start(BufferedReader in, Dataset dataset) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        final Integer[] count = new Integer[1];
        count[0] = 0;
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        JSONArray array = new JSONArray(sb.toString());
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
}
