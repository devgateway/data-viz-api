package org.devgateway.viz.commons.controllers;

import org.devgateway.viz.commons.services.CategoryService;
import org.devgateway.viz.commons.services.DimensionDefinitionService;
import org.devgateway.viz.commons.services.FilterDefinitionService;
import org.devgateway.viz.commons.services.MeasureDefinitionService;
import org.devgateway.viz.commons.services.generic.GenericStatsAPIServiceBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;


public class BaseStatsController<S extends GenericStatsAPIServiceBase> extends Controller {

    private final Logger logger = LoggerFactory.getLogger(GenericStatsAPIServiceBase.class);

    private S service;

    private Class entityClass;

    @Autowired
    private MeasureDefinitionService measureDefinitionService;

    @Autowired
    private DimensionDefinitionService dimensionDefinitionService;

    @Autowired
    private FilterDefinitionService filterDefinitionService;
    
    @Autowired
    private CategoryService categoryService;

    public BaseStatsController(S service, Class entityClass) {
        this.service = service;
        this.entityClass = entityClass;
    }


    @Autowired
    CacheManager cacheManager;

    @GetMapping("/cacheEvict")
    public ResponseEntity cacheEvict(HttpServletRequest req, @RequestParam Map<String, String> allParams) {

        cacheManager.getCacheNames().forEach(s -> cacheManager.getCache(s).clear());
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity stats(HttpServletRequest req, @RequestParam Map<String, String> allParams) {

        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName));

        logger.info("------- " + methodName + " ----");

        return new ResponseEntity<>(service.stats(allParams, new ArrayList<>()), HttpStatus.OK);
    }

    @GetMapping("/stats/**")
    public ResponseEntity sum(HttpServletRequest req, @RequestParam Map<String, String> allParams) {
        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName));

        logger.info("------- " + methodName + " ----");

        String dimensions = req.getRequestURI().substring(req.getRequestURI().indexOf("stats") + 6);
        List dms = new ArrayList();
        if (!dimensions.isEmpty()) {
            dms = Arrays.asList(dimensions.split("/"));
        }
        return new ResponseEntity<>(service.stats(allParams, dms), HttpStatus.OK);
    }

    @GetMapping(value = "/dimensions")
    public ResponseEntity getDimensions() {
        return new ResponseEntity(dimensionDefinitionService.getDimensions(), HttpStatus.OK);
    }

    @GetMapping(value = "/filters")
    public ResponseEntity getFilters() {
        return new ResponseEntity(filterDefinitionService.getFilters(entityClass), HttpStatus.OK);
    }

    @GetMapping(value = "/measures")
    public ResponseEntity getMeasures() {
        return new ResponseEntity(measureDefinitionService.getMeasures(), HttpStatus.OK);
    }
    
    @GetMapping(value = "/clearCategories")
    public ResponseEntity clearCategories() {
        categoryService.clearCategories();
        return new ResponseEntity(null, HttpStatus.OK);
    }
}
