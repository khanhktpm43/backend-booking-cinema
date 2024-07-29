package com.dev.booking.Repository;

import com.dev.booking.Entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository  extends JpaRepository<Genre, Long> {
    boolean existsByName( String name);
    Page<Genre> findAll(Pageable pageable);

    Page<Genre> findByNameContainingIgnoreCase(String name, Pageable page);
}
