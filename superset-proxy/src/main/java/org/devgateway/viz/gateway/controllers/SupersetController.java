package org.devgateway.viz.gateway.controllers;

import org.devgateway.viz.gateway.services.SupersetProxyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*") // Allow all origins
public class SupersetController {

    private final SupersetProxyService supersetService;

    CacheManager cacheManager;

    public SupersetController(SupersetProxyService supersetService, CacheManager cacheManager) {
        this.supersetService = supersetService;
        this.cacheManager = cacheManager;
    }

    @GetMapping("/cacheEvict")
    public ResponseEntity<Object> cacheEvict(HttpServletRequest req, @RequestParam Map<String, String> allParams) {
        cacheManager.getCacheNames().forEach(s -> Objects.requireNonNull(cacheManager.getCache(s)).clear());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/charts")
    public Object getCharts() {
        return supersetService.fetchCharts();
    }

    @GetMapping("/datasets")
    public Object getDatasets() {
        return supersetService.fetchDatasets();
    }

    @GetMapping("/dimensions")
    public Object getDimensions(@RequestParam(required = false) String dvzProxyDatasetId) {
        if (dvzProxyDatasetId == null || dvzProxyDatasetId.isEmpty()) {
            return List.of();
        }

        return supersetService.fetchDimensions(dvzProxyDatasetId);
    }

    @GetMapping("/measures")
    public Object getMeasures(@RequestParam(required = false) String dvzProxyDatasetId) {
        if (dvzProxyDatasetId == null || dvzProxyDatasetId.isEmpty()) {
            return List.of();
        }

        return supersetService.fetchMeasures(dvzProxyDatasetId);
    }

    @GetMapping("/filters")
    public Object getFilters(@RequestParam(required = false) String dvzProxyDatasetId) {
        if (dvzProxyDatasetId == null || dvzProxyDatasetId.isEmpty()) {
            return List.of();
        }

        return supersetService.fetchFilters(dvzProxyDatasetId);
    }

    @GetMapping(value = {"/categories", "/categories/"})
    public Object getCategories(@RequestParam(required = false) String dvzProxyDatasetId) {
        if (dvzProxyDatasetId == null || dvzProxyDatasetId.isEmpty()) {
            return List.of();
        }

        return supersetService.fetchCategories(dvzProxyDatasetId);
    }

    @GetMapping("/stats")
    public Object stats(HttpServletRequest req, @RequestParam Map<String, String> allParams) {
        return List.of();
    }

    @GetMapping("/stats/**")
    public Object getStats(HttpServletRequest req, @RequestParam(required = false) String dvzProxyDatasetId, @RequestParam Map<String, String> allParams) {
        try {
            if (dvzProxyDatasetId == null || dvzProxyDatasetId.isEmpty()) {
                return List.of();
            }
            String dimensions = req.getRequestURI().substring(req.getRequestURI().indexOf("stats") + 6);
            return supersetService.getStats(dvzProxyDatasetId, allParams, dimensions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to fetch stats");
        }
    }

    private static final String STATS = "stats";

    private String getDimensionsFromRequest(HttpServletRequest req) {
        try {
            int statsIndex = req.getRequestURI().indexOf(STATS) + STATS.length() + 1;
            return req.getRequestURI().substring(statsIndex);
        } catch (Exception e) {
            return "";
        }
    }
}