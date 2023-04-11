package org.devgateway.viz.commons.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.devgateway.viz.commons.domain.LocaleText;
import org.devgateway.viz.commons.pojo.serializers.LocaleTextSerializer;

import java.util.List;

public class Translatable {
    protected String label;
    @JsonSerialize(using = LocaleTextSerializer.class)
    protected List<LocaleText> labels;

    public Translatable(String label) {
        this.label = label;
    }

    public Translatable() {
    }

    public Translatable(String label, List<LocaleText> labels) {
        this.label = label;
        this.labels = labels;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<LocaleText> getLabels() {
        return labels;
    }

    public void setLabels(List<LocaleText> labels) {
        this.labels = labels;
    }
}
