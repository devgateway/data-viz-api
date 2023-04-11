package org.devgateway.viz.commons.pojo;

import org.devgateway.viz.commons.domain.Category;

import java.util.List;

public class Type {
    String dimension;
    String category;
    List<Category> items;

    public Type(String dimension, String category, List<Category> items) {
        this.dimension = dimension;
        this.category = category;
        this.items = items;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Category> getItems() {
        return items;
    }

    public void setItems(List<Category> items) {
        this.items = items;
    }
}
