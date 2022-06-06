package com.example.sk_blog.repositories;

import com.example.sk_blog.model.GlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;

public interface GlobalSettingsRepository extends JpaRepository<GlobalSetting, Integer> {
    GlobalSetting findByCode(@NotNull String code);
}
