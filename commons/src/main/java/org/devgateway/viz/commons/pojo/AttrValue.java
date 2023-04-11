package org.devgateway.viz.commons.pojo;

import org.devgateway.viz.commons.domain.LocaleText;

import java.util.List;

@Deprecated
public class AttrValue {

    private String attribute;
    private Object value;

    public String getAttribute() {
        return attribute;
    }

    public AttrValue(String attribute, Object value) {
        this.attribute = attribute;
        this.value = value;

    }

    public AttrValue(String attribute, Object value, List<LocaleText> labels) {
        this.attribute = attribute;
        this.value = value;

    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
}
