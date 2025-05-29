package org.devgateway.viz.gateway.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.devgateway.viz.gateway.services.SuperSetProxyService;
import org.devgateway.viz.gateway.services.WarmUpService;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*") // Allow all origins
public class SupersetController {

    private final SuperSetProxyService supersetProxyService;

    private final WarmUpService warmUpService;

    private final CacheManager cacheManager;

    public SupersetController(SuperSetProxyService supersetProxyService, WarmUpService warmUpService, CacheManager cacheManager) {
        this.supersetProxyService = supersetProxyService;
        this.warmUpService = warmUpService;
        this.cacheManager = cacheManager;
    }

    @GetMapping("/cacheEvict")
    public ResponseEntity<Object> cacheEvict(HttpServletRequest req, @RequestParam Map<String, String> allParams) {
        cacheManager.getCacheNames().forEach(s -> Objects.requireNonNull(cacheManager.getCache(s)).clear());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/charts")
    public Object getCharts() {
        return supersetProxyService.getCharts();
    }

    @GetMapping("/warmUp")
    public Object warmUp() {
        warmUpService.warmUp();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/datasets")
    public Object getDatasets() {
        return supersetProxyService.getDatasets();
    }

    @GetMapping("/dimensions")
    public Object getDimensions(@RequestParam(required = false) String dvzProxyDatasetId) {
        if (dvzProxyDatasetId == null || dvzProxyDatasetId.isEmpty()) {
            return List.of();
        }

        return supersetProxyService.getDimensions(dvzProxyDatasetId);
    }

    @GetMapping("/measures")
    public Object getMeasures(@RequestParam(required = false) String dvzProxyDatasetId) {
        if (dvzProxyDatasetId == null || dvzProxyDatasetId.isEmpty()) {
            return List.of();
        }

        return supersetProxyService.getMeasures(dvzProxyDatasetId);
    }

    @GetMapping("/filters")
    public Object getFilters(@RequestParam(required = false) String dvzProxyDatasetId) {
        if (dvzProxyDatasetId == null || dvzProxyDatasetId.isEmpty()) {
            return List.of();
        }

        return supersetProxyService.getFilters(dvzProxyDatasetId);
    }

    @GetMapping(value = {"/categories", "/categories/"})
    public Object getCategories(@RequestParam(required = false) String dvzProxyDatasetId) {
        if (dvzProxyDatasetId == null || dvzProxyDatasetId.isEmpty()) {
            return List.of();
        }

        return supersetProxyService.getCategories(dvzProxyDatasetId);
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
            return supersetProxyService.getStats(dvzProxyDatasetId, allParams, dimensions);
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