package com.cinetickets.api.repository;

import com.cinetickets.api.entity.ProductCategory;
import com.cinetickets.api.entity.Promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {
    List<Promotion> findByIsActiveTrue();
    List<Promotion> findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(
            ZonedDateTime now, ZonedDateTime now2);
}