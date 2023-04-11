package org.devgateway.viz.commons.domain;

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryType;
import org.devgateway.viz.commons.domain.annotations.Filter;
import org.devgateway.viz.commons.domain.annotations.Measure;
import org.devgateway.viz.commons.domain.annotations.Measures;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DatasetRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Measures(values = @Measure(label = "Records", expression = "count", group = "Aggregations", name = "count"))
    private Long id;

    @ManyToOne
    @QueryType(value = PropertyType.NONE)
    @Filter(param = "ds", label = "Dataset", description = "Dataset Filter")
    private Dataset dataset;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(final Dataset dataset) {
        this.dataset = dataset;
    }
}
