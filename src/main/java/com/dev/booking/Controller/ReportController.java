package com.dev.booking.Controller;

import com.dev.booking.ResponseDTO.DailyRevenue;
import com.dev.booking.ResponseDTO.MonthlyRevenue;
import com.dev.booking.ResponseDTO.MovieRevenue;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("api/v1/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/daily")
    public ResponseEntity<ResponseObject<List<DailyRevenue>>> getDailyRevenue( @RequestParam LocalDate fromDate, @RequestParam LocalDate toDate){
        List<DailyRevenue> dailyRevenues = reportService.getDailyRevenue(fromDate, toDate);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", dailyRevenues));
    }

    @GetMapping("/monthly")
    public ResponseEntity<ResponseObject<List<MonthlyRevenue>>> getMonthlyRevenue(@RequestParam String fromDate, @RequestParam String toDate){
        List<MonthlyRevenue> dailyRevenues = reportService.getMonthlyRevenue(fromDate, toDate);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", dailyRevenues));
    }
    @GetMapping("/movie")
    public ResponseEntity<ResponseObject<List<MovieRevenue>>> getMovieRevenue( @RequestParam LocalDate fromDate, @RequestParam LocalDate toDate){
        List<MovieRevenue> dailyRevenues = reportService.getMovieRevenue(fromDate, toDate);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", dailyRevenues));
    }


}
