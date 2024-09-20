
package org.devgateway.viz.commons.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
public class FileContent {
    private static final int LOB_LENGTH = 10000000;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID id;

    private ZonedDateTime createdDate;

    private String name;

    private String contentType;

    private long size;

    @Lob
    @Column(length = LOB_LENGTH)
    @JdbcTypeCode(Types.VARBINARY)
    @JsonIgnore
    private byte[] bytes;

    public FileContent() {
    }

    public FileContent(final String name, final String contentType, final byte[] bytes) {
        this.name = name;
        this.contentType = contentType;
        this.bytes = bytes;
        this.size = bytes.length;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(final byte[] bytes) {
        this.bytes = bytes;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(final long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
