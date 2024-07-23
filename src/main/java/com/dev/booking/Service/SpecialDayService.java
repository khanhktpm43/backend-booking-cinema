package com.dev.booking.Service;

import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.Showtime;
import com.dev.booking.Entity.SpecialDay;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.SeatRepository;
import com.dev.booking.Repository.SpecialDayRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpecialDayService {
    @Autowired
    private SpecialDayRepository specialDayRepository;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    public int checkDayType(Showtime showtime) {
        if (specialDayRepository.isSpecialDay(showtime) == 1) {
            return 1;
        }
        if (showtime.getStartTime().isBefore(showtime.getMovie().getReleaseDate())) {
            return 3;
        }
        DayOfWeek dayOfWeek = showtime.getStartTime().getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return 5;
        } else {
            return 7;
        }
    }

    public Page<DetailResponse<SpecialDay>> getAll(int page, int size, String[] sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<SpecialDay> specialDays = specialDayRepository.findAll(pageable);
        return mappingService.mapToResponse(specialDays);
    }

    public Page<DetailResponse<SpecialDay>> getByMonthYear(int month, int year, int page, int size, String[] sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<SpecialDay> specialDays = specialDayRepository.findByMonthAndYear(month, year, pageable);
        return mappingService.mapToResponse(specialDays);

    }

    public DetailResponse<SpecialDay> getById(Long id) {
        SpecialDay specialDay = specialDayRepository.findById(id).orElse(null);
        return mappingService.mapToResponse(specialDay);
    }

    public DetailResponse<SpecialDay> create(HttpServletRequest request, SpecialDay specialDay) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        specialDay.setId(null);
        specialDay.setCreatedBy(userReq);
        specialDay.setCreatedAt(LocalDateTime.now());
        specialDay.setUpdatedAt(null);
        SpecialDay newSpecialDay = specialDayRepository.save(specialDay);
        return mappingService.mapToResponse(newSpecialDay);
    }

    public DetailResponse<SpecialDay> update(HttpServletRequest request, Long id, SpecialDay specialDay) {
        User userReq = jwtRequestFilter.getUserRequest(request);

        SpecialDay day = specialDayRepository.findById(id).orElseThrow();
        day.setName(specialDay.getName());
        day.setStart(specialDay.getStart());
        day.setEnd(specialDay.getEnd());
        day.setUpdatedAt(LocalDateTime.now());
        day.setUpdatedBy(userReq);
        SpecialDay newday = specialDayRepository.save(day);
        return mappingService.mapToResponse(newday);
    }
}
