package com.geziblog.geziblog.entity;
import com.geziblog.geziblog.Enums.Role;
import jakarta.persistence.*;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;



@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "kullanicilar")
public class User implements UserDetails {
    @Enumerated(EnumType.STRING)
    Role role;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "mail")
    private String mail;
    @Column(name = "name")
    private String name;
    @Column(name = "surname")
    private String sname;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Post> posts = new ArrayList<>();
    @OneToMany(mappedBy = "follower")
    private Set<Following> following = new HashSet<>();
    @OneToMany(mappedBy = "following")
    private Set<Following> followers = new HashSet<>();




public User(){}
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}


