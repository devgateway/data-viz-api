package org.devgateway.viz.commons.repositories;

import org.devgateway.viz.commons.domain.Job;
import org.devgateway.viz.commons.domain.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findAllByCodeAndStatusIn(String code, List<JobStatus> statuses);

    List<Job> findAllByStatus(JobStatus status);

    List<Job> findAllByStatusIn(List<JobStatus> statuses);

    List<Job> findAllByCodeOrderByCreatedDateDesc(String code);

}
