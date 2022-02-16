package com.example.sk_blog.service;

import com.example.sk_blog.repositories.PostRepository;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class PostService {

    PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

//    public Page<Post> getPageOfPosts() {
//
//    }

    public static class CustomDateSerializer extends StdSerializer<LocalDateTime> {

        public CustomDateSerializer() {
            this(null);
        }

        public CustomDateSerializer(Class t) {
            super(t);
        }

        @Override
        public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeNumber(localDateTime.toEpochSecond(ZoneOffset.UTC));
        }
    }


}
