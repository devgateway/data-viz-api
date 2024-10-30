package org.devgateway.viz.commons.boot;

import org.devgateway.viz.commons.domain.Dataset;
import org.devgateway.viz.commons.io.BaseImport;
import org.devgateway.viz.commons.observers.CacheManagerDatasetObserver;
import org.devgateway.viz.commons.observers.DatasetListener;
import org.devgateway.viz.commons.observers.DatasetRecordObserver;
import org.devgateway.viz.commons.services.generic.DatasetRecordService;
import org.devgateway.viz.commons.services.generic.DatasetService;
import org.devgateway.viz.commons.services.generic.GenericConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;

import java.io.IOException;
import java.util.List;

public class GenericDatasetApplication implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${viz.initial.config}")
    Boolean initialConfig = true;

    @Value("${viz.startup.import}")
    Boolean startupImport = false;

    @Value("${viz.import.directory}")
    String importDirectory;

    @Value("${viz.date.format}")
    String format;


    @Autowired
    DatasetService datasetService;

    public GenericDatasetApplication(final BaseImport importer,
                                     final DatasetRecordService datasetRecordService,
                                     final GenericConfigService configService) {
        this.importer = importer;
        this.datasetRecordService = datasetRecordService;
        this.configService = configService;
    }

    private final BaseImport  importer;

    private final DatasetRecordService datasetRecordService;

    private final GenericConfigService configService;

    @Autowired
    private DatasetListener datasetListener;

    @Autowired
    private CacheManager cacheManager;

    public void run(final String... args) {
        registerDatasetObserver();

        if (initialConfig) {
            initConfig();
        }

        if (startupImport) {
            if (datasetService.getAllDatasets().size() > 0) {
                logger.info("Initial startup import will not run having previous imported data");
            } else {
                try {
                    List<Dataset> datasets = importer.createDataSets();
                    datasets.forEach(ds -> {
                        try {
                            importer.start(ds);

                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    });
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

    }


    private void registerDatasetObserver() {
        datasetListener.addDatasetObserver(new DatasetRecordObserver(datasetRecordService));
        datasetListener.addDatasetObserver(new CacheManagerDatasetObserver(cacheManager));
    }


    private void initConfig() {
        configService.synchronizeConfigMetadata();
    }

}
