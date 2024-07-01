package com.dev.booking.Controller;

import com.dev.booking.Entity.Food;
import com.dev.booking.Repository.FoodRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.FoodService;
import com.dev.booking.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/food")
public class FoodController {
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FoodService foodService;
    @GetMapping("")
    public ResponseEntity<ResponseObject<List<DetailResponse<Food>>>> getAll(){
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",foodService.getAll()));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Food>>> getById(@PathVariable Long id){
        DetailResponse<Food> response = foodService.getById(id);
        if(response != null){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
    }


    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<Food>>> create(HttpServletRequest request, @RequestParam("file") MultipartFile file,@RequestParam("name") String name,@RequestParam("price") float price){
        Food food = new Food();
        food.setName(name);
        try {
            if(file != null) food.setImage(file.getBytes());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("upload image fail",null));
        }
        food.setPrice(price);
        Example<Food> example = Example.of(food);
        if(!foodRepository.exists(example)){
            DetailResponse<Food> response = foodService.create(request, food);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("",response));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("duplicate",null));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Food>>> update(@PathVariable Long id,HttpServletRequest request, @RequestParam("file") MultipartFile file,@RequestParam("name") String name,@RequestParam("price") float price){
        if(foodRepository.existsById(id)){
            Food food = new Food();
            food.setName(name);
            try {
                if(file != null) food.setImage(file.getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("upload image fail",null));
            }
            food.setPrice(price);
            Example<Food> example = Example.of(food);
            if(!foodRepository.exists(example)){
                DetailResponse<Food> response = foodService.update(id,request, food);
                return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("",response));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("duplicate",null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Food>>> delete(@PathVariable Long id){
        if(foodRepository.existsById(id)){
            foodRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
    }
}
