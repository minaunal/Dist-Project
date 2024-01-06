package com.geziblog.geziblog.controller.repository;

import com.geziblog.geziblog.entity.Following;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Following, Long> {
    // Özel sorgular veya metotlar buraya eklenebilir
}
