package com.example.sk_blog.api.response;

import com.example.sk_blog.model.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TagResponse {

    private List<Tag> tags;
}
