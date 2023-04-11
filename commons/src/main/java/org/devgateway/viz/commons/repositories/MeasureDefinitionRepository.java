package org.devgateway.viz.commons.repositories;

import org.devgateway.viz.commons.domain.metadata.MeasureDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasureDefinitionRepository extends JpaRepository<MeasureDefinition, Long>,
        JpaSpecificationExecutor<MeasureDefinition>, QuerydslPredicateExecutor<MeasureDefinition> {


    MeasureDefinition findByCode(String code);
}
