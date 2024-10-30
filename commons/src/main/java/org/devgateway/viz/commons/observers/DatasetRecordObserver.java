package org.devgateway.viz.commons.observers;

import com.opencsv.exceptions.CsvValidationException;
import org.devgateway.viz.commons.domain.Dataset;
import org.devgateway.viz.commons.domain.DatasetRecord;
import org.devgateway.viz.commons.services.generic.DatasetRecordService;

import java.io.IOException;

public class DatasetRecordObserver<T extends DatasetRecord> implements DatasetObserver {

    private final DatasetRecordService datasetRecordService;

    public DatasetRecordObserver(final DatasetRecordService datasetRecordService) {
        this.datasetRecordService = datasetRecordService;
    }

    public void onDatasetSave(final Dataset dataset) throws CsvValidationException, IOException {
        datasetRecordService.createDatasetRecords(dataset);
    }

    public void onDatasetUpdate(final Dataset dataset) throws CsvValidationException, IOException {
        datasetRecordService.recreateDatasetRecords(dataset);
    }

    public void onDatasetDelete(final Dataset dataset) {
        datasetRecordService.deleteDatasetRecords(dataset);
    }

    @Override
    public void onDatasetChange(final Dataset dataset, final DatasetAction action) throws CsvValidationException, IOException {
        System.out.println("DatasetObserver - a dataset has been changed.");
        switch (action) {
            case SAVE:
                onDatasetSave(dataset);
                break;
            case UPDATE:
                onDatasetUpdate(dataset);
                break;
            case DELETE:
                onDatasetDelete(dataset);
                break;
            default:
                break;
        }
    }
}
