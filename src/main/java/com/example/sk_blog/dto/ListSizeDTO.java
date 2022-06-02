package com.example.sk_blog.dto;

import com.example.sk_blog.model.Post;
import lombok.Data;

import java.util.List;

@Data
public class ListSizeDTO {
    private Integer size;
    private List<Post> list;

    public ListSizeDTO(Integer size, List<Post> list) {
        this.size = size;
        this.list = list;
    }

    @Override
    public String toString() {
        return "ListSizeDTO{" +
                "size=" + size +
                ", list=" + list +
                '}';
    }
}
