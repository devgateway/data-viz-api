package org.devgateway.viz.commons.controllers;

import org.devgateway.viz.commons.controllers.BaseCategoryController;
import org.devgateway.viz.commons.pojo.CategoryResponse;
import org.devgateway.viz.commons.services.generic.GenericCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;

public class GenericCategoryController<S extends GenericCategoryService> extends BaseCategoryController {

    private final S service;
    private final Class<?> entityClass;

    public GenericCategoryController(S service, Class<?> entityClass) {
        this.service = service;
        this.entityClass = entityClass;
    }

    @GetMapping("/generic-categories")
    public ResponseEntity<Collection<CategoryResponse>> getCategories(HttpServletRequest req, @RequestParam Map<String, String> allParams) {
        return service.categories(allParams);
    }
}
