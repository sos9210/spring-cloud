package com.example.catalogservice.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CatalogRepository extends JpaRepository<CatalogEntity,Long> {
    Optional<CatalogEntity> findByProductId(String productId);
}
