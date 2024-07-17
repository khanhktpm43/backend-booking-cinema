package com.dev.booking.Service;

import com.dev.booking.Entity.*;
import com.dev.booking.Repository.SeatPriceRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class SeatPriceService {
    @Autowired
    private SpecialDayService specialDayService;

    @Autowired
    private SeatPriceRepository seatPriceRepository;
//    public List<DetailResponse<SeatPrice>> mapToResponse(List<SeatPrice> seatPrices){
//        return seatPrices.stream().map(seatPrice -> {
//            return new DetailResponse<>(seatPrice, seatPrice.getCreatedBy(), seatPrice.getUpdatedBy());
//        }).collect(Collectors.toList());
//    }
//    public DetailResponse<SeatPrice> getById(Long id){
//        SeatPrice seatPrice = seatPriceRepository.findById(id).orElse(null);
//        return new DetailResponse<>(seatPrice, seatPrice.getCreatedBy(), seatPrice.getUpdatedBy());
//    }
    public boolean isValid(SeatPrice seatPrice){
        return seatPrice.isValid() && seatPriceRepository.isValid(seatPrice);
    }
    public float getPrice(Showtime showtime, Seat seat){
        int dayType = specialDayService.checkDayType(showtime);
        float ticketPrice = seatPriceRepository.findPriceByDateAndCodeAndType(showtime.getStartTime(),dayType,seat.getSeatType());
        return ticketPrice;
    }
    public List<SeatPrice> getPricingTableByShowtime(Showtime showtime){
        int dayType = specialDayService.checkDayType(showtime);
        List<SeatPrice> prices = seatPriceRepository.findPriceByDate(showtime.getStartTime(),dayType);
        return prices;
    }
}
