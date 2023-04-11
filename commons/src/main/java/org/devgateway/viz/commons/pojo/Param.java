package org.devgateway.viz.commons.pojo;

import java.util.List;

public class Param {

    private String dimension;
    private List<Object> value;

    public String getName() {
        return dimension;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public List<Object> getValue() {
        return value;
    }

    public void setValue(List<Object> value) {
        this.value = value;
    }
}
