package com.dev.booking.Service;

import com.dev.booking.Entity.Food;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.FoodRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FoodService {
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    public DetailResponse<Food> create(HttpServletRequest request, Food food) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null){
            return null;
        }
        food.setId(null);
        food.setCreatedBy(userReq);
        food.setCreatedAt(LocalDateTime.now());
        food.setUpdatedAt(null);
        Food food1 = foodRepository.save(food);
        DetailResponse<Food> response = new DetailResponse<>(food1,food1.getCreatedBy(),null);
        return response;
    }

    public DetailResponse<Food> update(Long id, HttpServletRequest request, Food food) {
        Food food1 = foodRepository.findById(id).orElse(null);
        if(food1 != null ){
            User userReq = jwtRequestFilter.getUserRequest(request);
            if(userReq == null){
                return null;
            }
            food1.setName(food.getName());
            food1.setPrice(food.getPrice());
            food1.setImage(food.getImage());
            food1.setUpdatedBy(userReq);
            food1.setUpdatedAt(LocalDateTime.now());
            foodRepository.save(food1);
            DetailResponse<Food> response = new DetailResponse<>(food1,food1.getCreatedBy(),food1.getCreatedBy());
            return response;
        }
        return null;
    }
}
