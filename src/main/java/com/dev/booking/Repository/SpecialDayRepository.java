package com.dev.booking.Repository;

import com.dev.booking.Entity.SpecialDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialDayRepository extends JpaRepository<SpecialDay, Long> {
}
