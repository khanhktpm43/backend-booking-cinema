package com.dev.booking.Controller;
import com.dev.booking.Entity.SpecialDay;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.RequestDTO.CreateUserRequest;
import com.dev.booking.RequestDTO.PasswordChangeDTO;
import com.dev.booking.RequestDTO.RegisterRequest;
import com.dev.booking.RequestDTO.UserInfoDTO;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.TokenDTO;
import com.dev.booking.ResponseDTO.UserDetailResponse;
import com.dev.booking.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<UserDetailResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<UserDetailResponse> responses = userService.getAll(name,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseObject<TokenDTO>> register(@RequestBody RegisterRequest request) {
        TokenDTO tokenDTO = userService.register(request);
        if (tokenDTO != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", tokenDTO));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("username, phone or email already exists in the system", null));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ResponseObject<UserDetailResponse>> create(HttpServletRequest request, @RequestBody CreateUserRequest createUserRequest) {
        UserDetailResponse userDetailResponse = userService.create(request, createUserRequest);
        if (userDetailResponse != null)
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", userDetailResponse));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("username, phone or email already exists in the system", null));
    }
    @PreAuthorize("hasRole('GUEST')")
    @PatchMapping("/change-password")
    public ResponseEntity<ResponseObject<User>> changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO, HttpServletRequest request){
        User userReq = jwtRequestFilter.getUserRequest(request);
        if(userReq == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        if(!new BCryptPasswordEncoder().matches(passwordChangeDTO.getCurrentPassword(), userReq.getPassWord()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("Current password is incorrect",null));
        if (!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getConfirmNewPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("New passwords do not match",null));
        }
        userReq.setPassWord(new BCryptPasswordEncoder().encode(passwordChangeDTO.getNewPassword()));
        User user = userRepository.save(userReq);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", user));
    }
    @PreAuthorize("hasRole('GUEST')")
    @PutMapping("")
    public ResponseEntity<ResponseObject<User>> updateInfo(@RequestBody UserInfoDTO info, HttpServletRequest request){
        User user = userService.updateInfo(request, info);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", user));
    }


}
