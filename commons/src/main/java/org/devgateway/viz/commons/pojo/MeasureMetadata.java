package org.devgateway.viz.commons.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.devgateway.viz.commons.services.generic.delegates.Delegate;

public class MeasureMetadata extends Translatable {

    private String value;
    private Boolean enabled;

    @JsonIgnore
    private String field;

    @JsonIgnore
    private Class<? extends Delegate> delegate;

    @JsonIgnore
    private String expression;

    @JsonIgnore
    private String filter;

    @JsonIgnore
    public boolean isDelegated() {
        return (this.expression == null || this.expression.isBlank()) && this.delegate != null;
    }

    public MeasureMetadata() {

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
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

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
