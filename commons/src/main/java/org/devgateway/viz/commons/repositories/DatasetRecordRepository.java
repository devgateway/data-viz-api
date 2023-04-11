package org.devgateway.viz.commons.repositories;

import org.devgateway.viz.commons.domain.DatasetRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface DatasetRecordRepository<T extends DatasetRecord> extends JpaRepository<T, Long>,
        JpaSpecificationExecutor<T>, QuerydslPredicateExecutor<T> {

    List<T> findByDatasetId(Long datasetId);

}
