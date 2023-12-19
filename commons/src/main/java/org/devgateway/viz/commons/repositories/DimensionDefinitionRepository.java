package org.devgateway.viz.commons.repositories;

import org.devgateway.viz.commons.domain.metadata.DimensionDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DimensionDefinitionRepository extends JpaRepository<DimensionDefinition, Long>,
        JpaSpecificationExecutor<DimensionDefinition>, QuerydslPredicateExecutor<DimensionDefinition> {

    DimensionDefinition findByCode(String code);

    DimensionDefinition findByFieldType(String type);

    List<DimensionDefinition> findAllByFieldTypeNotLike(String type);
}
