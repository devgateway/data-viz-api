package org.devgateway.viz.commons.repositories;

import org.devgateway.viz.commons.domain.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long>,
        JpaSpecificationExecutor<Dataset>, QuerydslPredicateExecutor<Dataset> {
    Dataset findByCode(String code);

}
