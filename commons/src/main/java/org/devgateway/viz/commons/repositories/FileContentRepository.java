package org.devgateway.viz.commons.repositories;

import org.devgateway.viz.commons.domain.FileContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FileContentRepository extends JpaRepository<FileContent, Long>,
        JpaSpecificationExecutor<FileContent>, QuerydslPredicateExecutor<FileContent> {

}
