package org.devgateway.viz.commons.observers;

import com.opencsv.exceptions.CsvValidationException;
import org.devgateway.viz.commons.domain.Dataset;

import java.io.IOException;

public interface DatasetObserver {

    void onDatasetChange(Dataset dataset, DatasetAction action) throws CsvValidationException, IOException;

    enum DatasetAction {
        SAVE, UPDATE, DELETE
    }
}
