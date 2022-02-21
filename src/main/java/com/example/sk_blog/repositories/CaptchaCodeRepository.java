package com.example.sk_blog.repositories;

import com.example.sk_blog.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Integer> {
    @Transactional
    void deleteAllByTimeBefore(LocalDateTime time);

    CaptchaCode findBySecretCode(@NotNull String secretCode);
}
