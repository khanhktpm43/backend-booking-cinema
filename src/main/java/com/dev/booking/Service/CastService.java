package com.dev.booking.Service;

import com.dev.booking.Entity.Cast;
import com.dev.booking.Entity.Genre;
import com.dev.booking.Entity.User;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CastService {
    @Autowired
    private UserRepository userRepository;


    public List<DetailResponse<Cast>> mapGenreToResponse(List<Cast> casts){
        return casts.stream().map(cast -> {
            UserBasicDTO createdBy = null;
            if (cast.getCreatedBy() != null) {
                User user = userRepository.findById(cast.getCreatedBy()).orElse(null);
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            UserBasicDTO updatedBy = null;
            if (cast.getUpdatedBy() != null) {
                User user = userRepository.findById(cast.getUpdatedBy()).orElse(null);
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            return new DetailResponse<>(cast, createdBy, updatedBy);
        }).collect(Collectors.toList());
    }
}
