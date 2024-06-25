package com.dev.booking.Controller;

import com.dev.booking.Entity.MyUserDetails;
import com.dev.booking.Entity.User;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.RequestDTO.RegisterRequest;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> register(@RequestBody RegisterRequest registerRequest){
        if(userService.register(registerRequest)){
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject("",null));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("invalid", null));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getById(@PathVariable Long id){

        if(userRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("",userService.getMyUserDetailsById(id)));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("",null));
    }
    @GetMapping("/me")
    public ResponseEntity<ResponseObject> getByToken(HttpServletRequest request){
        MyUserDetails userDetails = userService.getMyUserDetailsFromAccessToken(request);
        if(userDetails!= null){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("",userDetails));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("Token invalid",null));
    }
}
