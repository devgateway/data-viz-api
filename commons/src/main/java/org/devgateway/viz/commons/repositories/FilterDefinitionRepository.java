package org.devgateway.viz.commons.repositories;

import org.devgateway.viz.commons.domain.metadata.FilterDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FilterDefinitionRepository extends JpaRepository<FilterDefinition, Long>,
        JpaSpecificationExecutor<FilterDefinition>, QuerydslPredicateExecutor<FilterDefinition> {

    FilterDefinition findByParam(String param);

}