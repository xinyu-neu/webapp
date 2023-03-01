package com.xinyu.webapp.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AppImageRepository extends JpaRepository<AppImage, Integer> {
    List<AppImage> findByAppProduct(AppProduct product);
    Optional<AppImage> findById(String id);
    Optional<AppImage> deleteById(String id);
}
