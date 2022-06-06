package com.example.sk_blog.repositories;

import com.example.sk_blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(@NotNull String email);

    Optional<User> findByEmail(@NotNull String email);

    User findByCode(String code);

}
