package com.example.sk_blog.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_moderator", columnDefinition = "tinyint")
    @NotNull
    private boolean isModerator;

    @Column(name = "reg_time")
    @NotNull
    private Date regTime;

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


    @OneToMany(mappedBy="user")
    private Set<Post> posts;

    @OneToMany(mappedBy="user")
    private Set<PostVote> postVotes;

    @OneToMany(mappedBy="user")
    private Set<PostComment> postComments;


}
