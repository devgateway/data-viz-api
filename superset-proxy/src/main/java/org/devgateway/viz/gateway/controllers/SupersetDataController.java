package org.devgateway.viz.gateway.controllers;

import org.devgateway.viz.gateway.services.SupersetDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/data")
@CrossOrigin(origins = "*")
public class SupersetDataController {

    private final SupersetDataService supersetDataService;
    private final Logger logger = Logger.getLogger(SupersetDataController.class.getName());

    @Autowired
    public SupersetDataController(SupersetDataService supersetDataService) {
        this.supersetDataService = supersetDataService;
    }

    /**
     * Creates a view in the database with the given name and data
     * @param viewName the name of the view to create
     * @param viewData the data to add to the view
     * @return a response entity with the result of the operation
     */
    @PostMapping("/view")
    public ResponseEntity<Map<String, Object>> createView(
            @RequestParam String viewName,
            @RequestBody Object viewData) {
        logger.info("Creating view: " + viewName);

        Map<String, Object> response = new HashMap<>();
        boolean success = supersetDataService.createView(viewName, viewData);

        response.put("success", success);
        response.put("viewName", viewName);

        return new ResponseEntity<>(response, success ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates multiple views in the database from a map of view names to view data
     * @param views a map of view names to view data
     * @return a response entity with the result of the operation
     */
    @PostMapping("/views")
    public ResponseEntity<Map<String, Object>> createViews(@RequestBody Map<String, Object> views) {
        logger.info("Creating multiple views: " + views.keySet());

        Map<String, Object> response = new HashMap<>();
        boolean success = supersetDataService.createViews(views);

        response.put("success", success);
        response.put("viewCount", views.size());

        return new ResponseEntity<>(response, success ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }
}
