package org.devgateway.viz.commons.services.generic;

import com.opencsv.exceptions.CsvValidationException;
import org.devgateway.viz.commons.domain.Dataset;
import org.devgateway.viz.commons.domain.DatasetRecord;
import org.devgateway.viz.commons.domain.FileContent;
import org.devgateway.viz.commons.io.BaseCSVImporter;
import org.devgateway.viz.commons.io.BaseImport;
import org.devgateway.viz.commons.repositories.DatasetRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class DatasetRecordService<T extends DatasetRecord, R> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    BaseImport<T,R> importer;

    private final DatasetRecordRepository<T> datasetRecordRepository;

    public DatasetRecordService(final DatasetRecordRepository<T> datasetRecordRepository,
                                final BaseImport<T,R> datasetRecordImporter) {
        this.datasetRecordRepository = datasetRecordRepository;
        this.importer = datasetRecordImporter;
    }

    /**/

    /**
     * Create the dataset records related to a dataset.
     *
     * @param dataset
     */

    public void createDatasetRecords(final Dataset dataset) throws CsvValidationException, IOException {
        FileContent f = dataset.getFileContent();
        BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(f.getBytes())));
        importer.start(in, dataset);
    }

    /**
     * Recreate the dataset records from the dataset content file.
     * Remove the old dataset records and create new ones.
     *
     * @param dataset
     */
    public void recreateDatasetRecords(final Dataset dataset) throws CsvValidationException, IOException {
        deleteDatasetRecords(dataset);
        createDatasetRecords(dataset);
    }

    /**
     * Delete the dataset records related to a dataset.
     *
     * @param dataset
     */
    public void deleteDatasetRecords(final Dataset dataset) {
        datasetRecordRepository.findByDatasetId(dataset.getId()).stream()
                .forEach(datasetRecord -> datasetRecordRepository.delete(datasetRecord));
    }

    public void save(final T datasetRecord) {
        datasetRecordRepository.save(datasetRecord);
    }

    List<T> findByDatasetId(final Long datasetId) {
        return datasetRecordRepository.findByDatasetId(datasetId);
    }

    public void delete(final T datasetRecord) {
        datasetRecordRepository.delete(datasetRecord);
    }
}
