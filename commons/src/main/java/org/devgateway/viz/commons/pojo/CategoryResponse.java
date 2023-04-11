package org.devgateway.viz.commons.pojo;

import org.devgateway.viz.commons.domain.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryResponse {

    private String type;
    private List<Category> items;

    public CategoryResponse(final String type) {
        this.type = type;
        items = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public List<Category> getItems() {
        return items;
    }

    public void setItems(final List<Category> items) {
        this.items = items;
    }
}
