package org.devgateway.viz.commons.services;

import org.apache.commons.lang3.StringUtils;
import org.devgateway.viz.commons.domain.LocaleText;
import org.devgateway.viz.commons.domain.MeasureGroup;
import org.devgateway.viz.commons.domain.Styles;
import org.devgateway.viz.commons.domain.metadata.MeasureDefinition;
import org.devgateway.viz.commons.pojo.Measure;
import org.devgateway.viz.commons.pojo.MeasureMetadata;
import org.devgateway.viz.commons.pojo.Translatable;
import org.devgateway.viz.commons.pojo.request.MeasureDefinitionRequest;
import org.devgateway.viz.commons.repositories.MeasureDefinitionRepository;
import org.devgateway.viz.commons.services.generic.delegates.Delegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.annotations.Cacheable;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MeasureDefinitionService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EntityManager em;

    @Autowired
    CategoryService categoryService;

    private final MeasureDefinitionRepository measureDefinitionRepository;

    @Autowired
    public MeasureDefinitionService(final MeasureDefinitionRepository measureDefinitionRepository) {
        this.measureDefinitionRepository = measureDefinitionRepository;
    }

    public List<MeasureDefinition> getAllMeasureDefinitions() {
        return this.measureDefinitionRepository.findAll();
    }

    public MeasureDefinition createIfNotExists(String code, String label, String field, String expression, String delegate, String filter, String group, Integer position, Styles styles) {
        if (StringUtils.isBlank(code)) {
            throw new IllegalArgumentException("Measure definition name cannot be null or empty");
        }
        MeasureDefinition measureDefinition = this.measureDefinitionRepository.findByCode(code);
        if (measureDefinition == null) {
            measureDefinition = createMeasureDefinition(code, label, field, expression, delegate, filter, group, position, styles);
        }

        return measureDefinition;
    }


    public MeasureDefinition createMeasureDefinition(String name, String label, String field, String expression, String delegate, String filter, String group, Integer position, Styles styles) {
        MeasureDefinition measureDefinition = new MeasureDefinition();
        measureDefinition.setCode(name);
        measureDefinition.setValue(label);
        measureDefinition.setField(field);
        measureDefinition.setExpression(expression);
        measureDefinition.setDelegate(delegate);
        measureDefinition.setFilter(filter);
        measureDefinition.setParent(categoryService.createIfNotExist(group, MeasureGroup.class));
        measureDefinition.setCategoryStyle(styles);
        measureDefinition.setPosition(position);
        return this.measureDefinitionRepository.save(measureDefinition);
    }

    public MeasureDefinition getMeasureDefinitionById(final Long id) {
        return this.measureDefinitionRepository.findById(id).get();
    }

    public MeasureDefinition updateMeasureDefinition(final Long id, final MeasureDefinitionRequest measureDefinitionRequest) {
        MeasureDefinition measureDefinition = this.measureDefinitionRepository.findById(id).get();
        if (measureDefinition == null) {
            throw new RuntimeException("Measure definition with id " + id + " does not exist");
        }

        if (StringUtils.isNotBlank(measureDefinitionRequest.getValue())) {
            measureDefinition.setValue(measureDefinitionRequest.getValue());
        }

        if (measureDefinitionRequest.getPosition() != null) {
            measureDefinition.setPosition(measureDefinitionRequest.getPosition());
        }

        if (measureDefinitionRequest.getGroupName() != null) {
            measureDefinition.setParent(categoryService.createIfNotExist(measureDefinitionRequest.getGroupName(), MeasureGroup.class));
        }

        if (measureDefinitionRequest.getCategoryStyle() != null) {
            measureDefinition.setCategoryStyle(measureDefinitionRequest.getCategoryStyle());
        }

        if (measureDefinitionRequest.getLabels() != null) {
            List<LocaleText> labels = new ArrayList<>();
            measureDefinitionRequest.getLabels().forEach((language, text) -> {
                LocaleText localeText = new LocaleText();
                localeText.setLanguage(categoryService.getLanguage(language));
                localeText.setText(text);
                labels.add(localeText);
            });

            measureDefinition.setLabels(labels);
        }

        return this.measureDefinitionRepository.save(measureDefinition);
    }


    public List<MeasureMetadata> getMeasuresMetadata() {
      /*

    private String field;

    private String filter;

    private String expression;

    private String delegate;

    private Class aClass;

    private Boolean enabled;
      * */

        Query q = em.createQuery("select code,value ,expression,field,filter,delegate from MeasureDefinition where enabled is null or enabled = true ");
        List<MeasureMetadata> ms = (List<MeasureMetadata>) q.getResultList().parallelStream().map(new Function() {
            @Override
            public Object apply(Object o) {
                int i = 0;
                Object[] objs = (Object[]) o;
                MeasureMetadata m = new MeasureMetadata();
                m.setValue((String) objs[i++]);
                m.setLabel((String) objs[i++]);
                m.setExpression((String) objs[i++]);
                m.setField((String) objs[i++]);
                m.setFilter((String) objs[i++]);
                try {
                    String name = (String) objs[i++];
                    if (name != null && !name.isEmpty()) {
                        m.setDelegate((Class<? extends Delegate>) Class.forName(((String) name)));
                    }
                } catch (ClassNotFoundException e) {
                    logger.error("error when loading class", e);
                }
                return m;

            }
        }).collect(Collectors.toList());


        return ms;
    }

    @Cacheable("utils")
    public List<Measure> getMeasures() {
        StackWalker walker = StackWalker.getInstance();
        Optional<String> methodName = walker.walk(frames -> frames.findFirst().map(StackWalker.StackFrame::getMethodName));
        logger.info("-------  " + this.getClass().getSimpleName() + " ------- " + methodName + " ----");

        List<MeasureDefinition> measureDefinitions = getAllMeasureDefinitions();
        return measureDefinitions.stream().map(ms -> {
            Measure measure = new Measure(ms.getCode(), ms.getValue());
            measure.setStyles(ms.getCategoryStyle());
            Translatable t = new Translatable();
            t.setLabel(ms.getParent().getValue());
            t.setLabels(ms.getParent().getLabels());
            measure.setGroup(t);
            measure.setEnabled(ms.getEnabled());
            measure.setField(ms.getField());
            measure.setLabels(ms.getLabels());

            try {
                if (ms.getDelegate() != null) {
                    measure.setDelegate((Class<? extends Delegate>) Class.forName(ms.getDelegate()));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            measure.setPosition(ms.getPosition());
            measure.setExpression(ms.getExpression());
            measure.setField(ms.getField());
            measure.setFilter(ms.getFilter());

            return measure;
        }).collect(Collectors.toList());
    }
}
