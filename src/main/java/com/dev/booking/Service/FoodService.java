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
    public List<DetailResponse<Food>> getAll() {
        List<Food> foods = foodRepository.findAll();
        return foods.stream().map(food -> {
            UserBasicDTO createdBy = null;
            if (food.getCreatedBy() != null) {
                User user = userRepository.findById(food.getCreatedBy()).orElse(null);
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            UserBasicDTO updatedBy = null;
            if (food.getUpdatedBy() != null) {
                User user = userRepository.findById(food.getUpdatedBy()).orElse(null);
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            return new DetailResponse<>(food, createdBy, updatedBy);
        }).collect(Collectors.toList());
    }

    public DetailResponse<Food> getById(Long id){
        Food food = foodRepository.findById(id).orElse(null);
        if(food == null){
            return null;
        }
        UserBasicDTO createdBy = null;
        UserBasicDTO updatedBy = null;
        if(food.getUpdatedBy() != null && userRepository.existsById(food.getUpdatedBy())){
            User user = userRepository.findById(food.getUpdatedBy()).orElse(null);
            updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
        }
        if(food.getCreatedBy() != null && userRepository.existsById(food.getCreatedBy())){
            User user = userRepository.findById(food.getCreatedBy()).orElse(null);
           createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
        }
        DetailResponse<Food> response = new DetailResponse<>(food,createdBy,updatedBy);
        return response;
    }

    public DetailResponse<Food> create(HttpServletRequest request, Food food) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null){
            return null;
        }
        food.setId(null);
        food.setCreatedBy(userReq.getId());
        food.setCreatedAt(LocalDateTime.now());
        food.setUpdatedAt(null);
        Food food1 = foodRepository.save(food);
        UserBasicDTO createdBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail()) ;
        DetailResponse<Food> response = new DetailResponse<>(food1,createdBy,null);
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
            food1.setUpdatedBy(userReq.getId());
            food1.setUpdatedAt(LocalDateTime.now());
            foodRepository.save(food1);
            User user = userRepository.findById(food1.getCreatedBy()).orElse(null);
            UserBasicDTO createdBy = new UserBasicDTO(user.getId(),user.getName(), user.getEmail());
            User user1 = userRepository.findById(food1.getUpdatedBy()).orElse(null);
            UserBasicDTO updatedBy = new UserBasicDTO(user1.getId(),user1.getName(), user1.getEmail());
            DetailResponse<Food> response = new DetailResponse<>(food1,createdBy,updatedBy);
            return response;
        }
        return null;
    }
}
