package org.devgateway.viz.commons.services.generic;

import org.devgateway.viz.commons.domain.Job;
import org.devgateway.viz.commons.repositories.JobRepository;
import org.devgateway.viz.commons.domain.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class JobService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    DatasetService datasetService;

    @Autowired
    FileContentService fileContentService;

    public Job getJob(Long jobId) {
        return fetchJobElseThrowException(jobId);
    }

    public Job getJobByCode(String code) {
        List<Job> jobs = jobRepository.findAllByCodeOrderByCreatedDateDesc(code);
        if (jobs.isEmpty()) {
            logger.error("Job for code {} not found.", code);
            throw new RuntimeException("Job with supplied code not found!");
        }

        return jobs.get(0);
    }

    /**
     * @param jobId
     * @return the Job associated with the jobId. This method will
     * throw an exception if the job does not exist.
     */
    public Job fetchJobElseThrowException(Long jobId) {
        Job job = fetchJob(jobId);

        if (null == job) {
            logger.error("Job-id {} not found.", jobId);
            throw new RuntimeException("Job with supplied job-id not found!");
        }

        return job;
    }

    /**
     * @param jobId
     * @return the Job associated with the jobId.
     */
    public Job fetchJob(Long jobId) {
        Job job = jobRepository.findById(jobId).get();

        return job;
    }
    public Job initCreateOrUpdateDatasetJob(String code, String name, String fileName, String contentType, byte[] fileContent) {
        Job job = createJob(code);
        datasetService.startCreateOrUpdateDatasetJob(job.getId(), code, name, fileName, contentType, fileContent);

        return job;
    }
    public Job initDeleteDatasetJob(String code) {
        Job job = createJob(code);
        datasetService.startDeleteDatasetJob(job.getId(), code);

        return job;
    }

    public Job createJob(String code) {
        List<Job> jobs = jobRepository.findAllByCodeAndStatusIn(code, List.of(JobStatus.SUBMITTED, JobStatus.IN_PROGRESS));
        if (jobs.size() > 0) {
            throw new RuntimeException("There is already a job for code: " + code);
        };

        Job job = new Job();
        job.setStatus(JobStatus.SUBMITTED);
        job.setCode(code);
        job.setCreatedDate(ZonedDateTime.now());
        jobRepository.save(job);

        return job;
    }

    @PostConstruct
    public void checkFailedImportJobs() {
        logger.info("Check if there are submitted jobs that failed to complete");
        jobRepository.findAllByStatusIn(List.of(JobStatus.SUBMITTED, JobStatus.IN_PROGRESS)).forEach(job -> {
            job.setStatus(JobStatus.ERROR);
            job.setEndDate(ZonedDateTime.now());
            job.setMessage("Application failed to finish the jobs");
            jobRepository.save(job);
        });
    }

}
