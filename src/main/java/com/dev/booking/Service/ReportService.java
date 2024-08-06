package com.dev.booking.Service;

import com.dev.booking.Repository.BookingRepository;
import com.dev.booking.ResponseDTO.DailyRevenue;
import com.dev.booking.ResponseDTO.MonthlyRevenue;
import com.dev.booking.ResponseDTO.MovieRevenue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportService {
    @Autowired
    private BookingRepository bookingRepository;

    public List<DailyRevenue> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        // Lấy dữ liệu từ repository
        List<DailyRevenue> results = bookingRepository.findDailyRevenue(startDate, endDate);
        return results;
    }
    public List<MonthlyRevenue> getMonthlyRevenue(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth startYearMonth = YearMonth.parse(startDate, formatter);
        YearMonth endYearMonth = YearMonth.parse(endDate, formatter);
        LocalDate fromDate = startYearMonth.atDay(1); // Ngày đầu tiên của tháng
        LocalDate toDate = endYearMonth.atEndOfMonth(); // Ngày cuối cùng của tháng
        List<MonthlyRevenue> results = bookingRepository.findMonthlyRevenue(fromDate, toDate);
        return results;
    }


    public List<MovieRevenue> getMovieRevenue(LocalDate fromDate, LocalDate toDate) {
        List<MovieRevenue> results = bookingRepository.findMovieRevenue(fromDate, toDate);
        return results;
    }
}
