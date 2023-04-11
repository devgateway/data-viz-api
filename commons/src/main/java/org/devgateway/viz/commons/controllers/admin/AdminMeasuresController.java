package org.devgateway.viz.commons.controllers.admin;

import org.devgateway.viz.commons.controllers.Controller;
import org.devgateway.viz.commons.domain.metadata.MeasureDefinition;
import org.devgateway.viz.commons.pojo.request.MeasureDefinitionRequest;
import org.devgateway.viz.commons.services.MeasureDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "admin/measures", produces = APPLICATION_JSON_VALUE)
public class AdminMeasuresController extends Controller {

    @Autowired
    private MeasureDefinitionService measureDefinitionService;

    @GetMapping
    public List<MeasureDefinition> getMeasures() {
        return measureDefinitionService.getAllMeasureDefinitions();
    }

    @GetMapping("/{id}")
    public MeasureDefinition getMeasureById(@PathVariable("id") Long id) {
        return measureDefinitionService.getMeasureDefinitionById(id);
    }

    @PutMapping("/{id}")
    public MeasureDefinition updateMeasure(@PathVariable("id") Long id,
                                           @RequestBody MeasureDefinitionRequest measureDefinitionRequest) {
        return measureDefinitionService.updateMeasureDefinition(id, measureDefinitionRequest);
    }
}