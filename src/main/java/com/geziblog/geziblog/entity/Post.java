package com.geziblog.geziblog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.tomcat.util.codec.binary.Base64;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "postlar")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "user_id")
    public User user;
    @Column(name = "metin",length=2048)
    private String metin;
    @Column(name = "baslik")
    private String baslik;


    public Post(){}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}


