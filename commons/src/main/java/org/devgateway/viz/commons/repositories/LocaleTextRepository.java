package org.devgateway.viz.commons.repositories;

import org.devgateway.viz.commons.domain.LocaleText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocaleTextRepository extends JpaRepository<LocaleText, Long> {
}
