package com.example.sk_blog.dto;

import com.example.sk_blog.model.Post;

import java.util.List;

public class ListSizeDTO {
    private Integer size;
    private List<Post> list;

    public ListSizeDTO(Integer size, List<Post> list) {
        this.size = size;
        this.list = list;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<Post> getList() {
        return list;
    }

    public void setList(List<Post> list) {
        this.list = list;
    }
}
