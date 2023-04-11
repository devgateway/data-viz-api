package org.devgateway.viz.commons.domain;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.ZonedDateTime;

@Entity
@JsonPropertyOrder({"id", "value", "createdDate", "updatedDate", "fileContent", "code"})
public class Dataset extends Category {

    private ZonedDateTime createdDate;

    private ZonedDateTime updatedDate;

    @ManyToOne
    private FileContent fileContent;

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public FileContent getFileContent() {
        return fileContent;
    }

    public void setFileContent(FileContent fileContent) {
        this.fileContent = fileContent;
    }

    public void setUpdatedDate(final ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }


}
