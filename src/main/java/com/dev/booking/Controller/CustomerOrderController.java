package com.dev.booking.Controller;

import com.dev.booking.Entity.*;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.CustomerOrderRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.Service.CustomerOrderService;
import com.dev.booking.Service.MappingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

//@RestController
//@RequestMapping("api/v1/customer-orders")
//@CrossOrigin(origins = "*")
public class CustomerOrderController {
//    @Autowired
//    private CustomerOrderService service;
//
//    @GetMapping("")
//    public ResponseEntity<ResponseObject<Page<DetailResponse<CustomerOrder>>>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt,desc") String[] sort){
//        Page<DetailResponse<CustomerOrder>> responses = service.getAll(page, size, sort);
//        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
//    }
}
