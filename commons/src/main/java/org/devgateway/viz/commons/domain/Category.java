package org.devgateway.viz.commons.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.devgateway.viz.commons.pojo.serializers.LocaleTextSerializer;
import org.devgateway.viz.commons.pojo.serializers.ParentCategorySerializer;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Table(indexes = {
        @Index(name = "fk_index_type", columnList = "type"),
        @Index(name = "fk_index_code", columnList = "code"),
        @Index(name = "fk_index_value", columnList = "value"),
        @Index(name = "fk_index_parent", columnList = "parent_id")
})
public abstract class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String code;
    private String value;
    private Integer position;

    public Category getParent() {
        return parent;
    }

    @Embedded
    private Styles styles;

    public void setParent(Category parent) {
        this.parent = parent;
    }

    @ManyToOne(targetEntity = Category.class, fetch = FetchType.LAZY)
    @JsonSerialize(using = ParentCategorySerializer.class)

    private Category parent;

    @Column(insertable = false, updatable = false)
    private String type;

    @OneToMany(targetEntity = LocaleText.class, fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JsonSerialize(using = LocaleTextSerializer.class)
    private List<LocaleText> labels;

    @JsonSerialize(using = LocaleTextSerializer.class)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OneToMany(targetEntity = LocaleText.class)
    private List<LocaleText> descriptions;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<LocaleText> getLabels() {
        return labels;
    }

    public void setLabels(List<LocaleText> labels) {
        this.labels = labels;
    }

    public void addLabel(LocaleText label) {
        if (this.labels == null) {
            this.labels = new ArrayList<>();
        }
        this.labels.add(label);
    }

    public List<LocaleText> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<LocaleText> descriptions) {
        this.descriptions = descriptions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }


    public Styles getCategoryStyle() {
        return styles;
    }

    public void setCategoryStyle(Styles styles) {
        this.styles = styles;
    }
}
