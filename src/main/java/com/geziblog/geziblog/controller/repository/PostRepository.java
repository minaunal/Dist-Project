package com.geziblog.geziblog.controller.repository;

import com.geziblog.geziblog.entity.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUser_id(int id);

}
