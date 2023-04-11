package org.devgateway.viz.commons.controllers.admin;

import org.devgateway.viz.commons.controllers.Controller;
import org.devgateway.viz.commons.domain.metadata.FilterDefinition;
import org.devgateway.viz.commons.pojo.request.FilterDefinitionRequest;
import org.devgateway.viz.commons.services.FilterDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "admin/filters", produces = APPLICATION_JSON_VALUE)
public class AdminFiltersController extends Controller {

    @Autowired
    private FilterDefinitionService filterDefinitionService;

    @GetMapping
    public List<FilterDefinition> getFilters() {
        return filterDefinitionService.getAllFilterDefinitions();
    }

    @GetMapping("/{id}")
    public FilterDefinition getFilterById(@PathVariable("id") Long id) {
        return filterDefinitionService.getFilterDefinitionById(id);
    }

    @PutMapping("/{id}")
    public FilterDefinition updateFilter(@PathVariable("id") Long id,
                                         @RequestBody FilterDefinitionRequest filterDefinitionRequest) {
        return filterDefinitionService.updateFilterDefinition(id, filterDefinitionRequest);
    }
}