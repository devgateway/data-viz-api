package org.devgateway.viz.commons.services.generic;

import org.devgateway.viz.commons.domain.Dataset;
import org.devgateway.viz.commons.domain.FileContent;
import org.devgateway.viz.commons.domain.Job;
import org.devgateway.viz.commons.domain.JobStatus;
import org.devgateway.viz.commons.observers.DatasetListener;
import org.devgateway.viz.commons.repositories.DatasetRepository;
import org.devgateway.viz.commons.repositories.JobRepository;
import org.devgateway.viz.commons.observers.DatasetObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class DatasetService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DatasetRepository datasetRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private FileContentService fileContentService;

    @Autowired
    private DatasetListener datasetListener;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @Autowired
    public DatasetService(final DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    public List<Dataset> getAllDatasets() {
        return this.datasetRepository.findAll();
    }

    public Dataset getDatasetByCode(String code) {
        return datasetRepository.findByCode(code);
    }

    public Dataset getDatasetByCodeOrThrowException(String code) {
        Dataset dataset = getDatasetByCode(code);

        if (dataset == null) {
            throw new RuntimeException("No Dataset found with code: '" + code + "'");
        }

        return dataset;
    }

    public Dataset createDataset(final String code, final String name, String fileName, String contentType, byte[] content) throws IOException {
        validateDatasetParameters(name);

        ZonedDateTime time = ZonedDateTime.now();
        Dataset dataset = new Dataset();
        dataset.setValue(name);
        dataset.setCreatedDate(time);
        dataset.setUpdatedDate(time);
        dataset.setCode(code);
        FileContent fileContent = fileContentService.saveFile(fileName, contentType, content);
        dataset.setFileContent(fileContent);
        Dataset ds = datasetRepository.save(dataset);

        //Will trigger the event
        try {
            datasetListener.notifyDatasetObservers(dataset, DatasetObserver.DatasetAction.SAVE);
        } catch (Exception e) {
            logger.error("error when creating dataset", e);
            deleteDataset(code);
            throw new RuntimeException(e);
        }

        return ds;
    }


    public Dataset updateDataset(final String code, final String name, String fileName, String contentType, byte[] content) throws IOException {
        validateDatasetParameters(name);

        Dataset dataset = getDatasetByCode(code);
        dataset.setValue(name);
        dataset.setUpdatedDate(ZonedDateTime.now());
        FileContent fileContent = fileContentService.saveFile(fileName, contentType, content);
        dataset.setFileContent(fileContent);

        Dataset ds = datasetRepository.save(dataset);

        try {
            datasetListener.notifyDatasetObservers(dataset, DatasetObserver.DatasetAction.UPDATE);
        } catch (Exception e) {
            deleteDataset(code);
            throw new RuntimeException(e);
        }

        return ds;
    }

    public void deleteDataset(final String code) {
        Dataset dataset = getDatasetByCodeOrThrowException(code);

        try {
            datasetListener.notifyDatasetObservers(dataset, DatasetObserver.DatasetAction.DELETE);
            datasetRepository.delete(dataset);
        } catch (Exception e) {
            logger.error("Error in deleting the dataset", e);
            throw new RuntimeException(e);
        }
    }

    private void validateDatasetParameters(final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    @Async("asyncTaskExecutor")
    @Transactional(value = Transactional.TxType.NEVER)
    public void startCreateOrUpdateDatasetJob(Long jobId, String code, String name, String fileName,
                                              String contentType, byte[] fileContent) {
        logger.info("Received request with job-id {} and file {}", jobId, fileName);
        try {
            if (getDatasetByCode(code) != null) {
                updateDataset(code, name, fileName, contentType, fileContent);
            } else {
                createDataset(code, name, fileName, contentType, fileContent);
            }
            updateJob(jobId, JobStatus.COMPLETED, null);
        } catch (Exception e) {
            logger.error("Error during import operation.", e);
            String errMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
            updateJob(jobId, JobStatus.ERROR, errMsg.length() > 2000 ? errMsg.substring(0, 2000) + "..." : errMsg);
        }
    }

    @Async("asyncTaskExecutor")
    @Transactional(value = Transactional.TxType.NEVER)
    public void startDeleteDatasetJob(Long jobId, String code) {
        logger.info("Received request with job-id {}", jobId);
        try {
            if (getDatasetByCode(code) != null) {
                deleteDataset(code);
            }
            updateJob(jobId, JobStatus.COMPLETED, null);
        } catch (Exception e) {
            logger.error("Error during import operation.", e);
            String errMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
            updateJob(jobId, JobStatus.ERROR, errMsg.length() > 2000 ? errMsg.substring(0, 2000) + "..." : errMsg);
        }
    }

    private void updateJob(Long jobId, JobStatus status, String message) {
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.executeWithoutResult(s -> {
            Job job = jobRepository.getOne(jobId);
            job.setStatus(status);
            job.setMessage(message);
            job.setEndDate(ZonedDateTime.now());
        });
    }

}
