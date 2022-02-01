package com.example.sk_blog.repositories;

import com.example.sk_blog.model.GlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalSettingsRepository extends JpaRepository<GlobalSetting, Integer> {
}
