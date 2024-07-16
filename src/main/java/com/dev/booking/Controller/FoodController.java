package com.dev.booking.Controller;

import com.dev.booking.Entity.Food;
import com.dev.booking.Entity.Room;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.FoodRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.FoodService;
import com.dev.booking.Service.MappingService;
import com.dev.booking.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/foods")
public class FoodController {
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private FoodService foodService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Food>>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Food> foods = foodRepository.findAllByDeleted(false, pageable);
        Page<DetailResponse<Food>> responses = mappingService.mapToResponse(foods);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }

    @GetMapping("/deleted")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Food>>>> getAllByDeleted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Food> foods = foodRepository.findAllByDeleted(true, pageable);
        Page<DetailResponse<Food>> responses = mappingService.mapToResponse(foods);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Food>>> getById(@PathVariable Long id) {
        if (!foodRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
        Food food = foodRepository.findById(id).orElse(null);
        DetailResponse<Food> response = mappingService.mapToResponse(food);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
    }


    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<Food>>> create(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("name") String name, @RequestParam("price") float price) {
        Food food = new Food();
        food.setName(name);
        try {
            if (file != null) food.setImage(file.getBytes());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("upload image fail", null));
        }
        food.setPrice(price);
        Example<Food> example = Example.of(food);
        if (!foodRepository.exists(example)) {
            DetailResponse<Food> response = foodService.create(request, food);
            if (response != null)
                return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("duplicate", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Food>>> update(@PathVariable Long id, HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("name") String name, @RequestParam("price") float price) {
        if (foodRepository.existsById(id)) {
            Food food = new Food();
            food.setName(name);
            try {
                if (file != null) food.setImage(file.getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("upload image fail", null));
            }
            food.setPrice(price);
            Example<Food> example = Example.of(food);
            if (!foodRepository.exists(example)) {
                DetailResponse<Food> response = foodService.update(id, request, food);
                if (response != null)
                    return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("duplicate", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Food>>> delete(@PathVariable Long id, HttpServletRequest request){
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        if(foodRepository.existsByIdAndDeleted(id, false)){
            Food food = foodRepository.findById(id).orElse(null);
            food.setDeleted(true);
            food.setUpdatedBy(userReq);
            food.setUpdatedAt(LocalDateTime.now());
            foodRepository.save(food);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
    }
    @PatchMapping("/{id}")
    public  ResponseEntity<ResponseObject<Food>> restore(@PathVariable Long id, HttpServletRequest request){
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        Food food = foodRepository.findByIdAndDeleted(id, true).orElse(null);
        if (food == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
        }
        food.setDeleted(false);
        food.setUpdatedAt(LocalDateTime.now());
        food.setUpdatedBy(userReq);
        foodRepository.save(food);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",food));
    }
}
