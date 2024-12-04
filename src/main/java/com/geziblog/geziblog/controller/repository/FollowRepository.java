package com.geziblog.geziblog.controller.repository;

import com.geziblog.geziblog.entity.Following;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Following, Long> {
    List<Following> findByFollower_id(int id);
}
