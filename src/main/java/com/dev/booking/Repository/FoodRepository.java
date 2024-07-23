package com.dev.booking.Repository;

import com.dev.booking.Entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    Page<Food> findAllByDeleted(boolean b, Pageable pageable);

    boolean existsByIdAndDeleted(Long id, boolean b);

    Optional<Food> findByIdAndDeleted(Long id, boolean b);

    boolean existsByName(String name);
}
