package com.dev.booking.Controller;

import com.dev.booking.Entity.MyUserDetails;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.RequestDTO.CreateUserRequest;
import com.dev.booking.RequestDTO.RegisterRequest;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.TokenDTO;
import com.dev.booking.ResponseDTO.UserDetailResponse;
import com.dev.booking.Service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@Tag(name = "User API", description = "Sample API for demonstration")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<UserDetailResponse>> getById(@PathVariable Long id) {

        if (userRepository.existsById(id)) {
            UserDetailResponse response = userService.getMyUserDetailsById(id);

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<UserDetailResponse>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<UserDetailResponse>("id does not exist", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseObject<UserDetailResponse>> getByToken(HttpServletRequest request) {

        UserDetailResponse response = userService.getMyUserDetailsFromAccessToken(request);
        if (response.getUser() != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<UserDetailResponse>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<UserDetailResponse>("Token invalid", null));
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject<List<UserDetailResponse>>> getAll() {
        List<UserDetailResponse> responses = userService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }

    @PostMapping("/register")

    public ResponseEntity<ResponseObject<TokenDTO>> register(@RequestBody RegisterRequest request) {
        TokenDTO tokenDTO = userService.register(request);
        if (tokenDTO != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<TokenDTO>("", tokenDTO));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<TokenDTO>("username, phone or email already exists in the system", null));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseObject<UserDetailResponse>> create(HttpServletRequest request, @RequestBody CreateUserRequest createUserRequest) {
        UserDetailResponse userDetailResponse = userService.create(request, createUserRequest);
        if (userDetailResponse != null)
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", userDetailResponse));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("username, phone or email already exists in the system", null));
    }

}
