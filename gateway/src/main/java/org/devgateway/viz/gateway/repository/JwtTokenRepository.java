package org.devgateway.viz.gateway.repository;

import org.devgateway.viz.gateway.security.domain.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {

    JwtToken findByToken(String token);
}