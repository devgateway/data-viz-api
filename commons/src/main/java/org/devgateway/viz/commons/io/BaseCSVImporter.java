package org.devgateway.viz.commons.io;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.devgateway.viz.commons.domain.Dataset;
import org.devgateway.viz.commons.domain.DatasetRecord;
import org.devgateway.viz.commons.domain.FileContent;
import org.devgateway.viz.commons.services.generic.DatasetService;
import org.devgateway.viz.commons.services.generic.FileContentService;
import org.devgateway.viz.commons.services.generic.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class BaseCSVImporter<T extends DatasetRecord> extends BaseImport<T, String[]> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void start(BufferedReader in, Dataset dataset) throws IOException {
        CSVReader reader = new CSVReader(in);
        reader.skip(1);
        final Integer[] count = new Integer[1];
        count[0] = 0;
        String[] values = null;
        try {
            while ((values = reader.readNext()) != null) {
                T record = read(values);
                record.setDataset(dataset);
                if (record != null) {
                    logger.info("Record counts " + (count[0]++));
                    save(record);
                }
            }
        } catch (CsvValidationException e) {
            logger.error("Error reading CSV file", e);
            throw new IOException("Error reading CSV file", e);
        }
    }
}
