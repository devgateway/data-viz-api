package org.devgateway.viz.commons.pojo.request;

import org.devgateway.viz.commons.domain.Styles;

import java.util.Map;

public class CategoryRequest {

    private String value;

    private Integer position;

    private Styles categoryStyle;

    private Map<String, String> labels;

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(final Integer position) {
        this.position = position;
    }

    public Styles getCategoryStyle() {
        return categoryStyle;
    }

    public void setCategoryStyle(final Styles categoryStyle) {
        this.categoryStyle = categoryStyle;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(final Map<String, String> labels) {
        this.labels = labels;
    }
}
