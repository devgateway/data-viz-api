package org.devgateway.viz.commons.domain.metadata;

import org.devgateway.viz.commons.domain.Category;
import jakarta.persistence.Entity;

@Entity
public class DimensionDefinition extends Category {


    private String field;
    private String fieldType;

    private Class aClass;

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String dimensionType) {
        this.fieldType = dimensionType;
    }
}
