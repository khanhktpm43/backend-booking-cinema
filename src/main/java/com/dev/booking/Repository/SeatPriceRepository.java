package com.dev.booking.Repository;

import com.dev.booking.Entity.SeatPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatPriceRepository extends JpaRepository<SeatPrice, Long> {
}
