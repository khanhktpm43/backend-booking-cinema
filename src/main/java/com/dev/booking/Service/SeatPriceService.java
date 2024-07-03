package com.dev.booking.Service;

import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.SeatPrice;
import com.dev.booking.Entity.User;
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
    private UserRepository userRepository;
    @Autowired
    private SeatPriceRepository seatPriceRepository;


    public List<DetailResponse<SeatPrice>> mapToResponse(List<SeatPrice> seatPrices){
        List<DetailResponse<SeatPrice>> result = seatPrices.stream().map(seatPrice -> {
            UserBasicDTO createdBy = null;
            if (seatPrice.getCreatedBy() != null) {
                User user = userRepository.findById(seatPrice.getCreatedBy()).orElse(null);
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            UserBasicDTO updatedBy = null;
            if (seatPrice.getUpdatedBy() != null) {
                User user = userRepository.findById(seatPrice.getUpdatedBy()).orElse(null);
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            return new DetailResponse<>(seatPrice, createdBy, updatedBy);
        }).collect(Collectors.toList());
        return result;
    }
    public DetailResponse<SeatPrice> getById(Long id){
        SeatPrice seatPrice = seatPriceRepository.findById(id).orElse(null);
        UserBasicDTO createdBy = null;
        if(seatPrice != null && seatPrice.getCreatedBy() != null){
            User user = userRepository.findById(seatPrice.getCreatedBy()).orElse(null);
            if (user != null) {
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
        }
        UserBasicDTO updatedBy = null;
        if(seatPrice != null && seatPrice.getUpdatedBy() != null){
            User user = userRepository.findById(seatPrice.getUpdatedBy()).orElse(null);
            if (user != null) {
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
        }
        return new DetailResponse<>(seatPrice, createdBy, updatedBy);
    }
    public boolean isValid(SeatPrice seatPrice){
        return seatPrice.isValid() && seatPriceRepository.isValid(seatPrice);

    }
}
