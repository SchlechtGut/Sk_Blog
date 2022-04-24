package com.example.sk_blog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@JsonIncludeProperties({ "id", "name" })
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "is_moderator", columnDefinition = "tinyint")
    private Integer isModerator;

    @Column(name = "reg_time")
    private LocalDateTime regTime;

    @Column(columnDefinition = "varchar(255)")
    @NotNull
    private String name;

    @Column(columnDefinition = "varchar(255)")
    @NotNull
    private String email;

    @Column(columnDefinition = "varchar(255)")
    @NotNull
    private String password;

    @Column(columnDefinition = "varchar(255)")
    private String code;

    @Column(columnDefinition = "text")
    private String photo;

    @JsonIgnore
    @OneToMany(mappedBy="user")
    private Set<Post> posts;

    @JsonIgnore
    @OneToMany(mappedBy="user")
    private Set<PostVote> postVotes;

    @JsonIgnore
    @OneToMany(mappedBy="user")
    private Set<PostComment> postComments;

    public User(Integer isModerator, LocalDateTime regTime, String name, String email, String password) {
        this.isModerator = isModerator;
        this.regTime = regTime;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User() {
    }

    public boolean isModerator() {
        return isModerator == 1;
    }

    public Role getRole() {
        return isModerator == 1 ? Role.MODERATOR : Role.USER;
    }
}
