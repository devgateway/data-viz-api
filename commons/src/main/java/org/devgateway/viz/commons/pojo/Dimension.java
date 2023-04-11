package org.devgateway.viz.commons.pojo;

import org.devgateway.viz.commons.domain.LocaleText;

import java.util.List;

public class Dimension extends Translatable {

    private String value;

    private String type;
    private String field;


    public Dimension(String field, String value, String label, List<LocaleText> translations, String type) {
        this.field = field;
        this.value = value;
        this.label = label;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
