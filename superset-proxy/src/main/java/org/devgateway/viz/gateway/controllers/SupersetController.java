package org.devgateway.viz.gateway.controllers;

import org.devgateway.viz.gateway.services.SupersetProxyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;



@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*") // Allow all origins
public class SupersetController {

    private final SupersetProxyService supersetService;

      

    @Value("${superset.url}")
    private String supersetUrl;

    public SupersetController(SupersetProxyService supersetService) {
        this.supersetService = supersetService;
    }

    @GetMapping("/charts")
    public Object getCharts() {
        return supersetService.fetchCharts(supersetUrl);
    }

    @GetMapping("/datasets")
    public Object getDatasets() {
        return supersetService.fetchDatasets(supersetUrl);
    }

    @GetMapping("/dimensions")
    public Object getDimensions(@RequestParam(required = false) String datasetId) {
        if (datasetId == null || datasetId.isEmpty()) {
            return List.of();
        }

        
        return supersetService.fetchDimensions(supersetUrl, datasetId);       
    }

    @GetMapping("/measures")
    public Object getMeasures(@RequestParam(required = false) String datasetId) {
       if (datasetId == null || datasetId.isEmpty()) {
            return List.of();
      }

       return supersetService.fetchMeasures(supersetUrl, datasetId);        
    }

    @GetMapping("/filters")
    public Object getFilters(@RequestParam(required = false) String datasetId) {
        if (datasetId == null || datasetId.isEmpty()) {
            return List.of();
        }
       
        return supersetService.fetchFilters(supersetUrl, datasetId);        
    }

    @GetMapping("/stats/**")
    public Object getStats(HttpServletRequest req, @RequestParam(required = false) String datasetId,
            @RequestParam Map<String, String> allParams) {
        try {
            String dimensions = req.getRequestURI().substring(req.getRequestURI().indexOf("stats") + 6);
            return supersetService.getStats(supersetUrl, datasetId, allParams, dimensions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to fetch stats");
        }
    }
    
    @GetMapping("/categories")
    public Object getCategories(@RequestParam(required = false) String datasetId) {
        if (datasetId == null || datasetId.isEmpty()) {
            return List.of();
        }

        return supersetService.fetchCategories(supersetUrl, datasetId);         
    }   
}