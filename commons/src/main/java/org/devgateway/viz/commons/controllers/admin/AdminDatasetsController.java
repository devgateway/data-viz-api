package org.devgateway.viz.commons.controllers.admin;

import org.devgateway.viz.commons.controllers.Controller;
import org.devgateway.viz.commons.domain.Dataset;
import org.devgateway.viz.commons.domain.FileContent;
import org.devgateway.viz.commons.domain.Job;
import org.devgateway.viz.commons.services.generic.DatasetService;
import org.devgateway.viz.commons.services.generic.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "admin/datasets", produces = APPLICATION_JSON_VALUE)
public class AdminDatasetsController extends Controller {

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private JobService jobService;

    @GetMapping
    public List<Dataset> getDatasets() {
        return datasetService.getAllDatasets();
    }

    @GetMapping("/{code}")
    public Dataset getDatasetByCode(@PathVariable("code") String code) {
        return datasetService.getDatasetByCodeOrThrowException(code);
    }

    @PutMapping("/{code}")
    public Job updateDatasetByCode(@PathVariable("code") String code, @RequestParam("name") String name,
                                   @RequestParam("file") MultipartFile file) throws IOException {
        return jobService.initCreateOrUpdateDatasetJob(code, name, file.getName(), file.getContentType(), file.getBytes());
    }

    @DeleteMapping("/{code}")
    public Job deleteDatasetByCode(@PathVariable("code") String code) {
        return jobService.initDeleteDatasetJob(code);
    }


    @GetMapping("/{code}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("code") String code) {
        FileContent fileContent = datasetService.getDatasetByCodeOrThrowException(code).getFileContent();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileContent.getName() + "\"")
                .body(fileContent.getBytes());
    }

    @PostMapping
    public Job createDataset(@RequestParam("code") String code, @RequestParam("name") String name,
                             @RequestParam("file") MultipartFile file) throws IOException {
        return jobService.initCreateOrUpdateDatasetJob(code, name, file.getName(), file.getContentType(), file.getBytes());
    }

}