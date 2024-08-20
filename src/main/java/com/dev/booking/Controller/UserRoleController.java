package com.dev.booking.Controller;

import com.dev.booking.Entity.MovieCast;
import com.dev.booking.Entity.Role;
import com.dev.booking.Entity.User;
import com.dev.booking.Entity.UserRole;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.RoleRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.Repository.UserRoleRepository;
import com.dev.booking.RequestDTO.UserRoleRequest;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.ResponseDTO.UserDetailResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/user-roles")
@CrossOrigin(origins = "*")
public class UserRoleController {
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<ResponseObject<UserDetailResponse>> assignRole(@RequestBody UserRoleRequest request , HttpServletRequest httpRequest){
        User userReq = jwtRequestFilter.getUserRequest(httpRequest);
        if(userReq == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        List<Role> roles = new ArrayList<>();
        Example<User> userExample = Example.of(request.getUser());
        for (Role role : request.getRoles()) {
            Example<Role> roleExample = Example.of(role);
            if (!roleRepository.exists(roleExample) || role.getId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("Role does not exist",null));
            }
            roles.add(role);
        }
        if(userRepository.exists(userExample) && request.getUser().getId() != null && !roles.isEmpty()) {
            for (Role role : roles) {
                    UserRole userRole = new UserRole();
                    userRole.setUser(request.getUser());
                    userRole.setRole(role);
                    Example<UserRole> userRoleExample = Example.of(userRole);
                userRole.setCreatedBy(userReq);
                userRole.setCreatedAt(LocalDateTime.now());
                    if(userRoleRepository.existsByUserAndRole(request.getUser(), role)){
                        continue;
                    }
                userRoleRepository.save(userRole);
            }
            User user = userRepository.findById(request.getUser().getId()).orElseThrow();
            user.setUpdatedBy(userReq);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            UserBasicDTO createdBy = new UserBasicDTO(user.getCreatedBy().getId(),user.getCreatedBy().getName(), user.getCreatedBy().getEmail());
            UserBasicDTO updatedBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
            UserDetailResponse userDetailResponse = new UserDetailResponse(user,createdBy,updatedBy,roles);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("",userDetailResponse));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("User not exist",null));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping ("/{id}")
    ResponseEntity<ResponseObject<UserDetailResponse>> removeRole(@PathVariable Long id){
        if(userRoleRepository.existsById(id)){
            userRoleRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("",null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist",null));
    }
}
