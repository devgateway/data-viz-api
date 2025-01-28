package org.devgateway.viz.security.repository;

import org.devgateway.viz.security.security.domain.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {

    JwtToken findByToken(String token);
}