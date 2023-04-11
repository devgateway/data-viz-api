package org.devgateway.viz.commons.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.devgateway.viz.commons.pojo.serializers.ResponseSerializer;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"type", "value", "count", "sum", "value", "children"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize(using = ResponseSerializer.class)
public class Response {

    private String type;
    private Object value;

    private String category;

    public List<AttrValue> getAttrValues() {
        return attrValues;
    }

    public void setAttrValues(List<AttrValue> attrValues) {
        this.attrValues = attrValues;
    }


    List<AttrValue> attrValues;


    public void addAttrValues(final AttrValue attrValue) {
        if (attrValues == null) {
            attrValues = new ArrayList<>();
        }
        attrValues.add(attrValue);
    }


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Response> children;

    public void addChild(final Response item) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(item);
    }

    public List<Response> getChildren() {
        return children;
    }

    public void setChildren(List<Response> children) {
        this.children = children;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}



