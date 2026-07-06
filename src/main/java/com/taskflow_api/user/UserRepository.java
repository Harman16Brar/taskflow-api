package com.taskflow_api.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    //SELECT * FROM users WHERE email = ?
    public Optional<User> findByEmail(String email);

    //COUNT(*) > 0 FROM users WHERE email = ?
    public boolean existsByEmail(String email);
}
