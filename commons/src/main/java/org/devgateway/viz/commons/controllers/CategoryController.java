package org.devgateway.viz.commons.controllers;

import org.devgateway.viz.commons.controllers.BaseCategoryController;
import org.devgateway.viz.commons.domain.Category;
import org.devgateway.viz.commons.pojo.CategoryResponse;
import org.devgateway.viz.commons.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin("*")
public class CategoryController extends BaseCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<Collection<CategoryResponse>> getCategories() {

        List<Category> categories = categoryService.getAllCategories();

        HashMap<String, CategoryResponse> categoriesMap = new HashMap<>();

        categories.forEach((Category o) -> {
            if (categoriesMap.get(o.getType()) == null) {
                categoriesMap.put(o.getType(), new CategoryResponse(o.getType()));
            }

            categoriesMap.get(o.getType()).getItems().add(o);
        });
        return new ResponseEntity<>(categoriesMap.values(), HttpStatus.OK);
    }
}
