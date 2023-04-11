package org.devgateway.viz.commons.controllers.admin;

import org.devgateway.viz.commons.controllers.Controller;
import org.devgateway.viz.commons.domain.metadata.DimensionDefinition;
import org.devgateway.viz.commons.pojo.request.DimensionDefinitionRequest;
import org.devgateway.viz.commons.services.DimensionDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "admin/dimensions", produces = APPLICATION_JSON_VALUE)
public class AdminDimensionsController extends Controller {

    @Autowired
    private DimensionDefinitionService dimensionDefinitionService;

    @GetMapping
    public List<DimensionDefinition> getDimensions() {
        return dimensionDefinitionService.getAllDimensionDefinitionsWithoutBoolean();
    }

    @GetMapping("/{id}")
    public DimensionDefinition getDimensionById(@PathVariable("id") Long id) {
        return dimensionDefinitionService.getDimensionDefinitionById(id);
    }

    @PutMapping("/{id}")
    public DimensionDefinition updateDimension(@PathVariable("id") Long id,
                                               @RequestBody DimensionDefinitionRequest dimensionDefinitionRequest) {
        return dimensionDefinitionService.updateDimensionDefinition(id, dimensionDefinitionRequest);
    }
}