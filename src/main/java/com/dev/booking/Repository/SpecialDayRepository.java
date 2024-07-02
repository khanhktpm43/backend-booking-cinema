package com.dev.booking.Repository;

import com.dev.booking.Entity.SpecialDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialDayRepository extends JpaRepository<SpecialDay, Long> {
    @Query("SELECT e FROM SpecialDay e WHERE MONTH(e.start) = :month AND YEAR(e.start) = :year")
    List<SpecialDay> findByMothAndYear(@Param("month") int month, @Param("year") int year);
}
