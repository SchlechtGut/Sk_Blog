package com.example.sk_blog.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "global_settings")
@Data
public class GlobalSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private String code;

    @NotNull
    private String name;

    @NotNull
    private String value;
}
