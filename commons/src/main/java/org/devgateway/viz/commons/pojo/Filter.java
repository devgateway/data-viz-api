package org.devgateway.viz.commons.pojo;

import org.devgateway.viz.commons.domain.LocaleText;

import java.util.List;

public class Filter extends Translatable {

    private String param;


    private String type;

    private String field;


    public Filter(String param, String label, List<LocaleText> translations, String type, String field) {
        this.param = param;
        this.label = label;
        this.type = type;
        this.field = field;
        this.labels = translations;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }


    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
