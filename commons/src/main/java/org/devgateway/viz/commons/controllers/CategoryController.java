package org.devgateway.viz.commons.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.devgateway.viz.commons.pojo.CategoryResponse;
import org.devgateway.viz.commons.services.generic.GenericCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Map;

public class CategoryController<S extends GenericCategoryService>  extends Controller {

    private S service;

    private Class entityClass;

    public CategoryController(S service, Class entityClass) {
        this.service = service;
        this.entityClass = entityClass;
    }

    @GetMapping("/categories")
    public ResponseEntity<Collection<CategoryResponse>> getCategories(HttpServletRequest req, @RequestParam Map<String, String> allParams) {

        return service.categories(allParams);
    }
}
