package com.dev.booking.Service;

import com.dev.booking.Entity.Genre;
import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.User;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreService {
    @Autowired
    private UserRepository userRepository;


    public List<DetailResponse<Genre>> mapGenreToResponse(List<Genre> genres){
        List<DetailResponse<Genre>> result = genres.stream().map(genre -> {
            UserBasicDTO createdBy = null;
            if (genre.getCreatedBy() != null) {
                User user = userRepository.findById(genre.getCreatedBy()).orElse(null);
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            UserBasicDTO updatedBy = null;
            if (genre.getUpdatedBy() != null) {
                User user = userRepository.findById(genre.getUpdatedBy()).orElse(null);
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            return new DetailResponse<>(genre, createdBy, updatedBy);
        }).collect(Collectors.toList());
        return result;
    }
}
