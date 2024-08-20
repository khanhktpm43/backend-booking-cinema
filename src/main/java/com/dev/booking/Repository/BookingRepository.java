package com.dev.booking.Repository;

import com.dev.booking.Entity.Booking;
import com.dev.booking.Entity.User;
import com.dev.booking.ResponseDTO.BookingResponse;
import com.dev.booking.ResponseDTO.DailyRevenue;
import com.dev.booking.ResponseDTO.MonthlyRevenue;
import com.dev.booking.ResponseDTO.MovieRevenue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {

    List<Booking> findByUserOrderByBookingDateDesc(User user);

    @Query("SELECT new com.dev.booking.ResponseDTO.DailyRevenue(" +
            "DATE(b.bookingDate), " +
            "SUM(CASE WHEN b.paymentStatus = com.dev.booking.Entity.PaymentStatus.SUCCESS THEN b.totalPrice ELSE 0 END), " +
            "SUM(CASE WHEN b.paymentStatus = com.dev.booking.Entity.PaymentStatus.FAILED THEN b.totalPrice ELSE 0 END)) " +
            "FROM Booking b " +
            "WHERE DATE(b.bookingDate) BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(b.bookingDate) " +
            "ORDER BY DATE(b.bookingDate) ASC")
    List<DailyRevenue> findDailyRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

//    @Query("SELECT new com.dev.booking.ResponseDTO.MonthlyRevenue(" +
//            "CONCAT(YEAR(b.bookingDate), '-', " +
//            "LPAD(CAST(MONTH(b.bookingDate) AS STRING), 2, '0')), " +
//            "SUM(CASE WHEN b.paymentStatus = com.dev.booking.Entity.PaymentStatus.SUCCESS THEN b.totalPrice ELSE 0 END), " +
//            "SUM(CASE WHEN b.paymentStatus = com.dev.booking.Entity.PaymentStatus.FAILED THEN b.totalPrice ELSE 0 END)) " +
//            "FROM Booking b " +
//            "WHERE DATE(b.bookingDate) BETWEEN :startDate AND :endDate " +
//            "GROUP BY YEAR(b.bookingDate), MONTH(b.bookingDate) " +
//            "ORDER BY YEAR(b.bookingDate), MONTH(b.bookingDate) ASC")
//    List<MonthlyRevenue> findMonthlyRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
@Query("SELECT new com.dev.booking.ResponseDTO.MonthlyRevenue(" +
        "CONCAT(YEAR(b.bookingDate), '-', " +
        "LPAD(CAST(MONTH(b.bookingDate) AS STRING), 2, '0')), " +
        "SUM(CASE WHEN b.paymentStatus = com.dev.booking.Entity.PaymentStatus.SUCCESS THEN b.totalPrice ELSE 0 END), " +
        "SUM(CASE WHEN b.paymentStatus = com.dev.booking.Entity.PaymentStatus.FAILED THEN b.totalPrice ELSE 0 END)) " +
        "FROM Booking b " +
        "WHERE DATE(b.bookingDate) BETWEEN :startDate AND :endDate " +
        "GROUP BY CONCAT(YEAR(b.bookingDate), '-', LPAD(CAST(MONTH(b.bookingDate) AS STRING), 2, '0')) " +
        "ORDER BY CONCAT(YEAR(b.bookingDate), '-', LPAD(CAST(MONTH(b.bookingDate) AS STRING), 2, '0')) ASC")
List<MonthlyRevenue> findMonthlyRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT new com.dev.booking.ResponseDTO.MovieRevenue(" +
            "s.movie.name, " +
            "SUM(CASE WHEN b.paymentStatus = com.dev.booking.Entity.PaymentStatus.SUCCESS  THEN b.totalPrice ELSE 0 END), " +
            "SUM(CASE WHEN b.paymentStatus = com.dev.booking.Entity.PaymentStatus.FAILED THEN b.totalPrice ELSE 0 END)) " +
            "FROM Ticket t " +
            "LEFT JOIN t.booking b " +
            "LEFT JOIN t.showtime s " +
            "WHERE (DATE(b.bookingDate) BETWEEN :fromDate AND :toDate) AND t.id IN ( SELECT MIN(t2.id) FROM Ticket t2 WHERE (DATE(t2.booking.bookingDate) BETWEEN :fromDate AND :toDate) GROUP BY t2.booking )" +
            "GROUP BY  s.movie ")
    List<MovieRevenue> findMovieRevenue(LocalDate fromDate, LocalDate toDate);
}
