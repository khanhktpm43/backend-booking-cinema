package com.dev.booking.Service;

import com.dev.booking.Entity.*;
import com.dev.booking.JWT.JwtRequestFilter;

import com.dev.booking.Repository.SeatPriceRepository;
import com.dev.booking.Repository.SeatTypeRepository;
import com.dev.booking.Repository.ShowtimeRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class SeatPriceService {
    @Autowired
    private SpecialDayService specialDayService;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private SeatPriceRepository seatPriceRepository;
    @Autowired
    private SeatTypeRepository seatTypeRepository;

    public boolean isValid(SeatPrice seatPrice) {
        return seatPrice.isValid() && seatPriceRepository.isValid(seatPrice);
    }

    public float getPrice(Showtime showtime, Seat seat) {
        int dayType = specialDayService.checkDayType(showtime);
        SeatType type = seatTypeRepository.findById(seat.getSeatType().getId()).orElseThrow();
        // sau này sửa lại
        if(type.getName().equals("double")){
            return seatPriceRepository.findPriceByDateAndCodeAndType(showtime.getStartTime(), dayType, seat.getSeatType().getId())/2;
        }
        return seatPriceRepository.findPriceByDateAndCodeAndType(showtime.getStartTime(), dayType, seat.getSeatType().getId());

    }

    public List<SeatPrice> getPricingTableByShowtime(Showtime showtime) {
        int dayType = specialDayService.checkDayType(showtime);
        return  seatPriceRepository.findPriceByDate(showtime.getStartTime(), dayType);

    }

    public Page<DetailResponse<SeatPrice>> getAll(int page, int size, String[] sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<SeatPrice> seatPrices = seatPriceRepository.findAll(pageable);
        return mappingService.mapToResponse(seatPrices);
    }

    public DetailResponse<SeatPrice> getById(Long id) {
        SeatPrice seatPrice = seatPriceRepository.findById(id).orElse(null);
        return mappingService.mapToResponse(seatPrice);
    }

    public DetailResponse<SeatPrice> create(HttpServletRequest request, SeatPrice seatPrice) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        seatPrice.setId(null);
        seatPrice.setCreatedBy(userReq);
        seatPrice.setCreatedAt(LocalDateTime.now());
        seatPrice.setUpdatedAt(null);
        SeatPrice seatPrice1 = seatPriceRepository.save(seatPrice);
        return mappingService.mapToResponse(seatPrice1);
    }

    public DetailResponse<SeatPrice> update(HttpServletRequest request, Long id, SeatPrice seatPrice) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        SeatPrice seatPrice1 = seatPriceRepository.findById(id).orElseThrow();
        seatPrice1.setPrice(seatPrice.getPrice());
        seatPrice1.setSeatType(seatPrice.getSeatType());
        seatPrice1.setStartDate(seatPrice.getStartDate());
        seatPrice1.setEndDate(seatPrice.getEndDate());
        seatPrice1.setEarlyShow(seatPrice.isEarlyShow());
        seatPrice1.setNormalDay(seatPrice.isNormalDay());
        seatPrice1.setWeekend(seatPrice.isWeekend());
        seatPrice1.setSpecialDay(seatPrice.isSpecialDay());
        seatPrice1.setUpdatedAt(LocalDateTime.now());
        seatPrice1.setUpdatedBy(userReq);
        SeatPrice seatPrice2 = seatPriceRepository.save(seatPrice1);
        return mappingService.mapToResponse(seatPrice2);
    }

    public List<DetailResponse<SeatPrice>> getPricesByShowtime(Long id) {
        Showtime showtime = showtimeRepository.findById(id).orElseThrow();
        List<SeatPrice> prices = getPricingTableByShowtime(showtime);
        return mappingService.mapToResponse(prices);
    }
}
