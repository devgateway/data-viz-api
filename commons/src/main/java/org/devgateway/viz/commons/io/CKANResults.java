package org.devgateway.viz.commons.io;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("checkstyle:MemberName")
public class CKANResults {

    private Boolean include_total;
    private String resource_id;
    private String records_format;
    private Integer limit;
    private Integer total;
    private Integer offset;
   private HashMap _links;


    List<HashMap> records;

    public Boolean getInclude_total() {
        return include_total;
    }

    public void setInclude_total(Boolean include_total) {
        this.include_total = include_total;
    }

    public String getResource_id() {
        return resource_id;
    }

    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
    }

    public String getRecords_format() {
        return records_format;
    }

    public void setRecords_format(String records_format) {
        this.records_format = records_format;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public HashMap get_links() {
        return _links;
    }

    public void set_links(HashMap _links) {
        this._links = _links;
    }

    public List<HashMap> getRecords() {
        return records;
    }

    public void setRecords(List<HashMap> records) {
        this.records = records;
    }
}
