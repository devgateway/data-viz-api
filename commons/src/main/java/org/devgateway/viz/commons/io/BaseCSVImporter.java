package org.devgateway.viz.commons.io;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.devgateway.viz.commons.domain.Dataset;
import org.devgateway.viz.commons.domain.DatasetRecord;
import org.devgateway.viz.commons.services.generic.DatasetService;
import org.devgateway.viz.commons.services.generic.FileContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;

public abstract class BaseCSVImporter<T extends DatasetRecord> extends BaseImport<T, String[]> {


    @Autowired
    private DatasetService datasetService;
    private FileContentService fileContentService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void start(BufferedReader in, Dataset dataset) throws IOException, CsvValidationException {
        CSVReader reader = new CSVReader(in);
        reader.skip(1);
        final Integer[] count = new Integer[1];
        count[0] = 0;
        String[] values = null;
        while ((values = reader.readNext()) != null) {
            T record = read(values);
            record.setDataset(dataset);
            if (record != null) {
                logger.info("Record counts " + (count[0]++));
                save(record);
            }
        }
    }
}
