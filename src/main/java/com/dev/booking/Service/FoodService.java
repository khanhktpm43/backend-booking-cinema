package com.dev.booking.Service;

import com.dev.booking.Entity.Food;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.FoodRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class FoodService {
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private MappingService mappingService;

    public DetailResponse<Food> create(HttpServletRequest request, Food food) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        food.setId(null);
        food.setCreatedBy(userReq);
        food.setCreatedAt(LocalDateTime.now());
        food.setUpdatedAt(null);
        Food food1 = foodRepository.save(food);
        return new DetailResponse<>(food1, food1.getCreatedBy(), null, food1.getCreatedAt(), null);
    }

    public DetailResponse<Food> update(HttpServletRequest request, Long id, MultipartFile file, String name, float price) throws IOException {
        Food food = Food.builder().name(name).image(file.getBytes()).price(price).build();
        food.setId(id);
        return update(request, food);
    }

    public DetailResponse<Food> update(HttpServletRequest request, Food food) {
        Food food1 = foodRepository.findById(food.getId()).orElseThrow();
        User userReq = jwtRequestFilter.getUserRequest(request);
        food1.setName(food.getName());
        food1.setPrice(food.getPrice());
        food1.setImage(food.getImage());
        food1.setUpdatedBy(userReq);
        food1.setUpdatedAt(LocalDateTime.now());
        foodRepository.save(food1);
        return new DetailResponse<>(food1, food1.getCreatedBy(), food1.getCreatedBy(), food1.getCreatedAt(), food1.getUpdatedAt());
    }

    public Page<DetailResponse<Food>> getAll(int page, int size, String[] sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Food> foods = foodRepository.findAllByDeleted(false, pageable);
        return mappingService.mapToResponse(foods);
    }

    public Page<DetailResponse<Food>> getAllByDeleted(int page, int size, String[] sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Food> foods = foodRepository.findAllByDeleted(true, pageable);
        return mappingService.mapToResponse(foods);
    }

    public DetailResponse<Food> getById(Long id) {
        Food food = foodRepository.findById(id).orElse(null);
        return mappingService.mapToResponse(food);
    }

    public DetailResponse<Food> create(HttpServletRequest request, MultipartFile file, String name, float price) throws IOException {
        Food food = Food.builder().name(name).image(file.getBytes()).price(price).build();
        if (!foodRepository.existsByName(food.getName())) {
            return create(request, food);
        }
        return null;
    }

    public void delete(HttpServletRequest request, Long id) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        Food food = foodRepository.findById(id).orElseThrow();
        food.setDeleted(true);
        food.setUpdatedBy(userReq);
        food.setUpdatedAt(LocalDateTime.now());
        foodRepository.save(food);
    }

    public DetailResponse<Food> restore(HttpServletRequest request, Food food) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        food.setDeleted(false);
        food.setUpdatedAt(LocalDateTime.now());
        food.setUpdatedBy(userReq);
        Food food1 = foodRepository.save(food);
        return mappingService.mapToResponse(food1);
    }


}
