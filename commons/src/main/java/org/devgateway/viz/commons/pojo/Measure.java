package org.devgateway.viz.commons.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.devgateway.viz.commons.domain.LocaleText;
import org.devgateway.viz.commons.domain.Styles;
import org.devgateway.viz.commons.pojo.serializers.LocaleTextSerializer;
import org.devgateway.viz.commons.services.generic.delegates.Delegate;

import java.util.List;

public class Measure extends Translatable {

    private String value;

    private Translatable group;

    private Styles styles;

    private Integer position;


    private Boolean enabled;

    @JsonIgnore
    private String field;

    @JsonIgnore
    private Class<? extends Delegate> delegate;

    @JsonIgnore
    private String expression;

    @JsonIgnore
    private String filter;
    @JsonSerialize(using = LocaleTextSerializer.class)
    private List<LocaleText> labels;


    public Measure(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public Measure(String value, String label, String expression, Class<? extends Delegate> delegate, Translatable group, String field, String filter, Integer position, String color, List<LocaleText> translations) {
        this.value = value;
        this.label = label;
        this.expression = expression;
        this.group = group;
        this.field = field;
        this.filter = filter;
        this.delegate = delegate;
        this.styles = new Styles(color);
        this.position = position;
        this.labels = translations;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public Styles getStyles() {
        return styles;
    }

    public void setStyles(Styles styles) {
        this.styles = styles;
    }

    public Class<? extends Delegate> getDelegate() {
        return delegate;
    }

    public void setDelegate(Class<? extends Delegate> delegate) {
        this.delegate = delegate;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @JsonIgnore
    public boolean isDelegated() {
        return (this.expression == null || this.expression.isBlank()) && this.delegate != null;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(final String filter) {
        this.filter = filter;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(final Integer position) {
        this.position = position;
    }

    public Translatable getGroup() {
        return group;
    }

    public void setGroup(Translatable group) {
        this.group = group;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public List<LocaleText> getLabels() {
        return labels;
    }

    @Override
    public void setLabels(List<LocaleText> labels) {
        this.labels = labels;
    }
}
