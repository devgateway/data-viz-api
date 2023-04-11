package org.devgateway.viz.commons.domain.metadata;

import org.devgateway.viz.commons.domain.Category;

import javax.persistence.*;

@Entity
///@Polymorphism(type = PolymorphismType.EXPLICIT)
public class MeasureDefinition extends Category {


    private String field;

    private String filter;

    private String expression;

    private String delegate;

    private Class aClass;

    private Boolean enabled;


    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDelegate() {
        return delegate;
    }

    public void setDelegate(String delegate) {
        this.delegate = delegate;
    }

    public Class getaClass() {
        return aClass;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }



    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}




