package org.devgateway.viz.commons.controllers.admin;

import org.devgateway.viz.commons.controllers.Controller;
import org.devgateway.viz.commons.domain.Job;
import org.devgateway.viz.commons.services.generic.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "admin/jobs", produces = APPLICATION_JSON_VALUE)
public class JobsController extends Controller {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JobService jobService;

    @GetMapping(path = "/{id}", produces = "application/json")
    public Job getJobStatus(@PathVariable(name = "id") Long id) {
        logger.debug("Received request to fetch status of job with id: {}", id);
        return jobService.getJob(id);
    }

    @GetMapping(path = "/code/{id}", produces = "application/json")
    public Job getJobByCode(@PathVariable("id") String id) {
        return jobService.getJobByCode(id);
    }

}