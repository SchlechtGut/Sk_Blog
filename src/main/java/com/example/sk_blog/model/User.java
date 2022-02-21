package com.example.sk_blog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@JsonIncludeProperties({ "id", "name" })
@Entity
@Table(name = "users")
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIsModerator() {
        return isModerator;
    }

    public void setIsModerator(Integer isModerator) {
        this.isModerator = isModerator;
    }

    public LocalDateTime getRegTime() {
        return regTime;
    }

    public void setRegTime(LocalDateTime regTime) {
        this.regTime = regTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public Set<PostVote> getPostVotes() {
        return postVotes;
    }

    public void setPostVotes(Set<PostVote> postVotes) {
        this.postVotes = postVotes;
    }

    public Set<PostComment> getPostComments() {
        return postComments;
    }

    public void setPostComments(Set<PostComment> postComments) {
        this.postComments = postComments;
    }
}
