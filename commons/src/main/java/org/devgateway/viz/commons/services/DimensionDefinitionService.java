package org.devgateway.viz.commons.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.devgateway.viz.commons.domain.LocaleText;
import org.devgateway.viz.commons.domain.metadata.DimensionDefinition;
import org.devgateway.viz.commons.domain.metadata.QDimensionDefinition;
import org.devgateway.viz.commons.pojo.Dimension;
import org.devgateway.viz.commons.pojo.request.DimensionDefinitionRequest;
import org.devgateway.viz.commons.repositories.DimensionDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DimensionDefinitionService {

    private final DimensionDefinitionRepository dimensionDefinitionRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    CategoryService categoryService;

    @Autowired
    public DimensionDefinitionService(final DimensionDefinitionRepository dimensionDefinitionRepository) {
        this.dimensionDefinitionRepository = dimensionDefinitionRepository;
    }

    public List<DimensionDefinition> getAllDimensionDefinitionsWithoutBoolean() {
        return this.dimensionDefinitionRepository.findAllByFieldTypeNotLike("Boolean");
    }

    public List<DimensionDefinition> getAllDimensionDefinitions() {
        return this.dimensionDefinitionRepository.findAll();
    }


    public Dimension getDimensionsByName(String name) {
        JPAQuery<DimensionDefinition> query = new JPAQuery<>(entityManager);
        query.from(QDimensionDefinition.dimensionDefinition);
        BooleanBuilder builder = new BooleanBuilder();
        query.where(builder.and(QDimensionDefinition.dimensionDefinition.code.eq(name)));
        DimensionDefinition d = query.fetchFirst();
        if (d != null) {
            return new Dimension(d.getField(), d.getCode(), d.getValue(), d.getLabels(), d.getFieldType());
        } else {
            throw new IllegalArgumentException("Dimension definition name not found");
        }
    }

    public DimensionDefinition createDimensionDefinitionIfNotExists(String field, String name, String label, String type, List<LocaleText> translations) {
        DimensionDefinition dimensionDefinition = createDimensionDefinitionIfNotExists(field, name, label, type);
        if (translations != null) {
            dimensionDefinition.setLabels(translations);
            this.dimensionDefinitionRepository.save(dimensionDefinition);
        }
        return dimensionDefinition;
    }

    public DimensionDefinition createDimensionDefinitionIfNotExists(String field, String name, String label, String type) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Dimension definition name cannot be null or empty");
        }

        DimensionDefinition dimensionDefinition = this.dimensionDefinitionRepository.findByCode(Utils.codify(name));
        if (dimensionDefinition == null) {
            DimensionDefinition dimensionDefinition1 = new DimensionDefinition();
            dimensionDefinition1.setCode(name);
            dimensionDefinition1.setValue(label);
            dimensionDefinition1.setFieldType(type);
            dimensionDefinition1.setField(field);

            return this.dimensionDefinitionRepository.save(dimensionDefinition1);
        }
        return dimensionDefinition;
    }

    public DimensionDefinition getDimensionDefinitionById(final Long id) {
        return this.dimensionDefinitionRepository.findById(id).get();
    }

    public DimensionDefinition getDimensionDefinitionByCode(final String code) {
        return this.dimensionDefinitionRepository.findByCode(code);
    }

    public DimensionDefinition getDimensionDefinitionByFieldType(final String type) {
        return this.dimensionDefinitionRepository.findByFieldType(type);
    }

    public DimensionDefinition updateDimensionDefinition(final Long id, final DimensionDefinitionRequest dimensionDefinitionRequest) {
        DimensionDefinition dimensionDefinition = this.dimensionDefinitionRepository.findById(id).get();
        if (dimensionDefinition == null) {
            throw new RuntimeException("Dimension definition with id " + id + " does not exist");
        }

        if (StringUtils.isNotBlank(dimensionDefinitionRequest.getValue())) {
            dimensionDefinition.setValue(dimensionDefinitionRequest.getValue());
        }

        if (StringUtils.isNotBlank(dimensionDefinitionRequest.getCode())) {
            dimensionDefinition.setCode(dimensionDefinitionRequest.getCode());
        }

        if (dimensionDefinitionRequest.getPosition() != null) {
            dimensionDefinition.setPosition(dimensionDefinitionRequest.getPosition());
        }

        if (dimensionDefinitionRequest.getLabels() != null) {
            List<LocaleText> labels = new ArrayList<>();
            dimensionDefinitionRequest.getLabels().forEach((language, text) -> {
                LocaleText localeText = new LocaleText();
                localeText.setLanguage(categoryService.getLanguage(language));
                localeText.setText(text);
                labels.add(localeText);
            });

            dimensionDefinition.setLabels(labels);
        }

        return this.dimensionDefinitionRepository.save(dimensionDefinition);
    }


    public DimensionDefinition save(final DimensionDefinition dimensionDefinition) {
        return this.dimensionDefinitionRepository.save(dimensionDefinition);
    }

    public List<Dimension> getDimensions() {
        List<DimensionDefinition> dimensionDefinitions = getAllDimensionDefinitions();
        return dimensionDefinitions.stream().map(d -> new Dimension(d.getField(), d.getCode(), d.getValue(), d.getLabels(), d.getFieldType())).collect(Collectors.toList());
    }

    /**
     * Update the labels of an existing Dimension.
     * ONLY labels for a new language can be updated through this method to avoid overriding translations made with
     * the admin module (or db patches).
     */
    public void updateLabels(String field, List<LocaleText> labels) {
        DimensionDefinition dimension = dimensionDefinitionRepository.findByCode(field);
        if (dimension == null) {
            throw new RuntimeException("Dimension definition with field " + field + " does not exist");
        }
        if (dimension.getLabels() == null) {
            dimension.setLabels(labels);
        } else {
            if (labels != null) {
                labels.forEach(label -> {
                    if (dimension.getLabels().stream().noneMatch(l -> l.getLanguage().getCode().equals(label.getLanguage().getCode()))) {
                        dimension.getLabels().add(label);
                    }
                });
            }
        }
        this.dimensionDefinitionRepository.save(dimension);
    }
}
