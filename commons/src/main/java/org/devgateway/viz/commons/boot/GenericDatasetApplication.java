package org.devgateway.viz.commons.boot;

import org.devgateway.viz.commons.domain.DatasetRecord;
import org.devgateway.viz.commons.io.BaseCSVImporter;
import org.devgateway.viz.commons.observers.CacheManagerDatasetObserver;
import org.devgateway.viz.commons.observers.DatasetListener;
import org.devgateway.viz.commons.observers.DatasetRecordObserver;
import org.devgateway.viz.commons.services.generic.DatasetRecordService;
import org.devgateway.viz.commons.services.generic.DatasetService;
import org.devgateway.viz.commons.services.generic.GenericConfigService;
import org.devgateway.viz.commons.services.generic.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class GenericDatasetApplication implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${tcdi.initial.config}")
    Boolean initialConfig = true;

    @Value("${tcdi.startup.import}")
    Boolean startupImport = false;

    @Value("${tcdi.import.directory}")
    String importDirectory;

    @Value("${tcdi.date.format}")
    String format;


    @Autowired
    DatasetService datasetService;

    public GenericDatasetApplication(final BaseCSVImporter importer, final DatasetRecordService datasetRecordService, final GenericConfigService configService) {
        this.importer = importer;
        this.datasetRecordService = datasetRecordService;
        this.configService = configService;
    }

    private final BaseCSVImporter<? extends DatasetRecord> importer;

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

                Path path = Paths.get(importDirectory);
                try {

                    Files.list(path)
                            .filter(file -> !file.getFileName().toString().equalsIgnoreCase(CommonConstants.TEMPLATE_FILE_NAME))
                            .forEach(file -> {
                        try {
                            UUID uuid = UUID.randomUUID();
                            String name = file.getFileName().toString();
                            name = name.substring(0, name.lastIndexOf(".")).toUpperCase();

                            datasetService.createDataset(uuid.toString(), name, file.getFileName().toString(), Files.probeContentType(file.toAbsolutePath()), Files.readAllBytes(file.toAbsolutePath()));


                        } catch (Exception e) {
                            logger.error("error when creating dataset", e);
                        }

                        //
                    });
                } catch (IOException e) {
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
