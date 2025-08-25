package com.hmd.learnredis.repositories;

import com.hmd.learnredis.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("jpaUserRepository")
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
