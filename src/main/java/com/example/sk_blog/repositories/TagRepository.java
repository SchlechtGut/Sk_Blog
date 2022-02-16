package com.example.sk_blog.repositories;

import com.example.sk_blog.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    List<Tag> getAllByNameStartingWith(@NotNull String name);


}
