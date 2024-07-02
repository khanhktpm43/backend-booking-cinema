package com.dev.booking.Service;

import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.SpecialDay;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.SeatRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpecialDayService {

    @Autowired
    private UserRepository userRepository;


    public List<DetailResponse<SpecialDay>> mapSpecialDayToResponse(List<SpecialDay> specialDays){
        List<DetailResponse<SpecialDay>> result = specialDays.stream().map(specialDay -> {
            UserBasicDTO createdBy = null;
            if (specialDay.getCreatedBy() != null) {
                User user = userRepository.findById(specialDay.getCreatedBy()).orElse(null);
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            UserBasicDTO updatedBy = null;
            if (specialDay.getUpdatedBy() != null) {
                User user = userRepository.findById(specialDay.getUpdatedBy()).orElse(null);
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            return new DetailResponse<>(specialDay, createdBy, updatedBy);
        }).collect(Collectors.toList());
        return result;
    }
}
