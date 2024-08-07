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
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/foods")
@CrossOrigin(origins = "*")
public class FoodController {
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private FoodService foodService;

    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Food>>>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Page<DetailResponse<Food>> responses = foodService.getAll(page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/deleted")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Food>>>> getAllByDeleted(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt,desc") String[] sort){
        Page<DetailResponse<Food>> responses = foodService.getAllByDeleted(page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Food>>> getById(@PathVariable Long id) {
        if (!foodRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
        DetailResponse<Food> response = foodService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<Food>>> create(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("name") String name, @RequestParam("price") float price) throws IOException {
       DetailResponse<Food> food = foodService.create(request,file, name, price);
       if(food != null){
           return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("",food));
       }
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("duplicate", null));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Food>>> update(@PathVariable Long id, HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("name") String name, @RequestParam("price") float price) throws IOException {
       if(!foodRepository.existsById(id)){
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
       }
        DetailResponse<Food> response = foodService.update(request, id, file, name, price);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",response));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Food>>> delete(@PathVariable Long id, HttpServletRequest request){
        if(foodRepository.existsByIdAndDeleted(id, false)){
            foodService.delete(request, id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public  ResponseEntity<ResponseObject<DetailResponse<Food>>> restore(@PathVariable Long id, HttpServletRequest request){
        Food food = foodRepository.findByIdAndDeleted(id, true).orElse(null);
        if (food == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
        }
        DetailResponse<Food> response = foodService.restore(request, food);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",response));
    }
}
