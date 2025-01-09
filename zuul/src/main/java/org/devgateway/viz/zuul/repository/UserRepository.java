package org.devgateway.viz.zuul.repository;

import org.devgateway.viz.zuul.security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
