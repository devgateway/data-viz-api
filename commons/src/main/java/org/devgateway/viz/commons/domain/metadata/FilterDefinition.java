package org.devgateway.viz.commons.domain.metadata;

import org.devgateway.viz.commons.domain.Category;

import javax.persistence.Entity;

@Entity
public class FilterDefinition extends Category {


    private String param;
    private String fieldType;
    private String field;
    private Class aClass;

    public FilterDefinition() {
    }



    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }
}
