package org.devgateway.viz.gateway.repository;

import org.devgateway.viz.gateway.security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
