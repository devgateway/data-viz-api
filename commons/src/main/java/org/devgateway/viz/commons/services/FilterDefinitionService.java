package org.devgateway.viz.commons.services;

import org.apache.commons.lang3.StringUtils;
import org.devgateway.viz.commons.domain.LocaleText;
import org.devgateway.viz.commons.domain.metadata.FilterDefinition;
import org.devgateway.viz.commons.pojo.Filter;
import org.devgateway.viz.commons.pojo.request.FilterDefinitionRequest;
import org.devgateway.viz.commons.repositories.FilterDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.annotations.Cacheable;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilterDefinitionService {

    private final FilterDefinitionRepository filterDefinitionRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EntityManager em;

    @Autowired
    CategoryService categoryService;

    @Autowired
    public FilterDefinitionService(final FilterDefinitionRepository filterDefinitionRepository) {
        this.filterDefinitionRepository = filterDefinitionRepository;
    }

    public List<FilterDefinition> getAllFilterDefinitions() {
        return this.filterDefinitionRepository.findAll();
    }

    public FilterDefinition createFilterDefinitionIfNotExist(String param, String label, String fieldType, String field) {
        if (StringUtils.isBlank(param)) {
            throw new IllegalArgumentException("Filter definition param cannot be null or empty");
        }

        FilterDefinition filterDefinition = this.filterDefinitionRepository.findByParam(param);
        if (filterDefinition != null) {
            logger.info("Filter definition with param " + param + " already exists");
            return filterDefinition;
        } else {

            filterDefinition = new FilterDefinition();
            filterDefinition.setParam(param);
            filterDefinition.setCode(param);
            filterDefinition.setValue(label);
            filterDefinition.setFieldType(fieldType);

            filterDefinition.setField(field);
            return this.filterDefinitionRepository.save(filterDefinition);
        }
    }

    public FilterDefinition getFilterDefinitionById(final Long id) {
        return this.filterDefinitionRepository.findById(id).get();
    }

    public FilterDefinition getFilterByParam(String param) {
        return this.filterDefinitionRepository.findByParam(param);
    }



    @Cacheable("utils")
    public String getField(String param) {
        Query q = em.createQuery("select field from FilterDefinition where param=:param");
        q.setParameter("param", param);
        List<String> fields=q.getResultList();
        if (fields.isEmpty()){
            return null;
        }
        return fields.iterator().next();

    }

    @Cacheable("utils")
    public String getFieldType(String param) {
        Query q = em.createQuery("select fieldType from FilterDefinition where param=:param");
        q.setParameter("param", param);
        List<String> fields=q.getResultList();
        if (fields.isEmpty()){
            return null;
        }
        return fields.iterator().next();
    }

    @Cacheable("utils")
    public String getFieldFromFieldType(String fieldType) {
        Query q = em.createQuery("select field from FilterDefinition where fieldType=:fieldType");
        q.setParameter("fieldType", fieldType);
        List<String> fields=q.getResultList();
        if (fields.isEmpty()){
            return null;
        }
        return fields.iterator().next();
    }


    public FilterDefinition updateFilterDefinition(final Long id, final FilterDefinitionRequest filterDefinitionRequest) {
        FilterDefinition filterDefinition = this.filterDefinitionRepository.findById(id).get();

        if (StringUtils.isNotBlank(filterDefinitionRequest.getValue())) {
            filterDefinition.setValue(filterDefinitionRequest.getValue());
        }

        if (filterDefinitionRequest.getLabels() != null) {
            List<LocaleText> labels = new ArrayList<>();
            filterDefinitionRequest.getLabels().forEach((language, text) -> {
                LocaleText localeText = new LocaleText();
                localeText.setLanguage(categoryService.getLanguage(language));
                localeText.setText(text);
                labels.add(localeText);
            });

            filterDefinition.setLabels(labels);
        }

        return this.filterDefinitionRepository.save(filterDefinition);
    }

    public List<Filter> getFilters(final Class entityClass) {
        List<FilterDefinition> filters = getAllFilterDefinitions();
        return filters.stream().map(filterDefinition -> new Filter(filterDefinition.getParam(), filterDefinition.getValue(), filterDefinition.getLabels(), filterDefinition.getFieldType(), filterDefinition.getField())).collect(Collectors.toList());
    }
}
