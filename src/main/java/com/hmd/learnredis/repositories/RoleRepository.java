package com.hmd.learnredis.repositories;

import com.hmd.learnredis.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String name);

    Set<Role> findByRoleNameIn(Set<String> roleNames);
}
