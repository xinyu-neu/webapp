package com.xinyu.webapp.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface AppProductRepository extends JpaRepository<AppProduct, Integer> {
}
