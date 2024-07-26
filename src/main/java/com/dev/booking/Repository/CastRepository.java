package com.dev.booking.Repository;

import com.dev.booking.Entity.Cast;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CastRepository extends JpaRepository<Cast, Long> {
    boolean existsByName(String name);

    Page<Cast> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
