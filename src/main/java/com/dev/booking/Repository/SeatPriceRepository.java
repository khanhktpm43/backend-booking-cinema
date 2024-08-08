package com.dev.booking.Repository;

import com.dev.booking.Entity.SeatPrice;
import com.dev.booking.Entity.SeatType;
import com.dev.booking.Entity.SpecialDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeatPriceRepository extends JpaRepository<SeatPrice, Long> {
    @Query("SELECT CASE WHEN COUNT(sp) = 0 THEN true ELSE false END FROM SeatPrice sp WHERE sp.seatType = :#{#seatPrice.seatType} AND sp.normalDay = :#{#seatPrice.normalDay} AND sp.earlyShow = :#{#seatPrice.earlyShow} AND sp.weekend = :#{#seatPrice.weekend} AND sp.specialDay = :#{#seatPrice.specialDay} AND ((sp.startDate <= :#{#seatPrice.startDate} AND sp.endDate >= :#{#seatPrice.startDate}) OR (sp.startDate <= :#{#seatPrice.endDate} AND sp.endDate >= :#{#seatPrice.endDate}))")
    boolean isValid(@Param("seatPrice") SeatPrice seatPrice);

    @Query("SELECT CASE WHEN COUNT(sp) = 0 THEN false ELSE true END FROM SeatPrice sp WHERE sp.seatType = :#{#seatPrice.seatType} AND sp.normalDay = :#{#seatPrice.normalDay} AND sp.weekend = :#{#seatPrice.weekend} AND sp.earlyShow = :#{#seatPrice.earlyShow} AND sp.specialDay = :#{#seatPrice.specialDay} AND ((sp.startDate <= :#{#seatPrice.startDate} AND sp.endDate >= :#{#seatPrice.startDate}) OR (sp.startDate <= :#{#seatPrice.endDate} AND sp.endDate >= :#{#seatPrice.endDate})) AND sp.id <> :currentId ")
    boolean checkDuplicateSeatPrice(@Param("seatPrice") SeatPrice seatPrice, @Param("currentId") Long currentId);

    @Query(value = "SELECT price FROM `seat-price` WHERE " +
            ":inputDate BETWEEN start_date AND end_date AND " +
            "seat_typeid = :type AND " +
            "((:code = 7 AND normal_day = 1) OR " +
            "(:code = 5 AND weekend = 1) OR " +
            "(:code = 1 AND special_day = 1) OR " +
            "(:code = 3 AND early_show = 1))", nativeQuery = true)
    float findPriceByDateAndCodeAndType(@Param("inputDate") LocalDateTime inputDate,
                                        @Param("code") int code,
                                        @Param("type") Long type);


    @Query(value = "SELECT * FROM `seat-price` WHERE " +
            ":inputDate BETWEEN start_date AND end_date AND " +
            "((:code = 7 AND normal_day = 1) OR " +
            "(:code = 5 AND weekend = 1) OR " +
            "(:code = 1 AND special_day = 1) OR" +
            "(:code = 3 AND early_show = 1) )", nativeQuery = true)
    List<SeatPrice> findPriceByDate(@Param("inputDate") LocalDateTime inputDate,
                                        @Param("code") int code);

}
