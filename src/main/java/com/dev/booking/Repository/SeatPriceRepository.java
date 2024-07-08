package com.dev.booking.Repository;

import com.dev.booking.Entity.SeatPrice;
import com.dev.booking.Entity.SpecialDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatPriceRepository extends JpaRepository<SeatPrice, Long> {
    @Query("SELECT CASE WHEN COUNT(sp) = 0 THEN true ELSE false END FROM SeatPrice sp WHERE sp.seatType = :#{#seatPrice.seatType} AND sp.normalDay = :#{#seatPrice.normalDay} AND sp.earlyShow = :#{#seatPrice.earlyShow} AND sp.weekend = :#{#seatPrice.weekend} AND sp.specialDay = :#{#seatPrice.specialDay} AND ((sp.startDate <= :#{#seatPrice.startDate} AND sp.endDate >= :#{#seatPrice.startDate}) OR (sp.startDate <= :#{#seatPrice.endDate} AND sp.endDate >= :#{#seatPrice.endDate}))")
    boolean isValid(@Param("seatPrice") SeatPrice seatPrice);

    @Query("SELECT CASE WHEN COUNT(sp) = 0 THEN false ELSE true END FROM SeatPrice sp WHERE sp.seatType = :#{#seatPrice.seatType} AND sp.normalDay = :#{#seatPrice.normalDay} AND sp.weekend = :#{#seatPrice.weekend} AND sp.earlyShow = :#{#seatPrice.earlyShow} AND sp.specialDay = :#{#seatPrice.specialDay} AND ((sp.startDate <= :#{#seatPrice.startDate} AND sp.endDate >= :#{#seatPrice.startDate}) OR (sp.startDate <= :#{#seatPrice.endDate} AND sp.endDate >= :#{#seatPrice.endDate})) AND sp.id <> :currentId")
    boolean checkDuplicateSeatPrice(@Param("seatPrice") SeatPrice seatPrice, @Param("currentId") Long currentId);
}
