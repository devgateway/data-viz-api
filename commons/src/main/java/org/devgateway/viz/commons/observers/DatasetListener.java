package org.devgateway.viz.commons.observers;

import com.opencsv.exceptions.CsvValidationException;
import org.devgateway.viz.commons.domain.Dataset;
import org.devgateway.viz.commons.observers.DatasetObserver.DatasetAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatasetListener {

    private List<DatasetObserver> datasetObservers = new ArrayList<>();

    public void addDatasetObserver(DatasetObserver observer) {
        datasetObservers.add(observer);
    }

    public void removeDatasetObserver(DatasetObserver observer) {
        datasetObservers.remove(observer);
    }

    public void notifyDatasetObservers(Dataset dataset, DatasetAction action) throws CsvValidationException, IOException {
        for (DatasetObserver observer : datasetObservers) {
            observer.onDatasetChange(dataset, action);
        }
    }

}
