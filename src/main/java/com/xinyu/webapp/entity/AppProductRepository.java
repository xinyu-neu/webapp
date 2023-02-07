package com.xinyu.webapp.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface AppProductRepository extends JpaRepository<AppProduct, Integer> {
    Optional<AppProduct> findBySku(String sku);
}
