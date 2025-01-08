package com.geziblog.geziblog.controller.repository;

import com.geziblog.geziblog.entity.Place;
import com.geziblog.geziblog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    Place findById(Integer id);
    List<Place> findAllByUser_id(Integer id);
}

