package com.dev.booking.Service;

import com.dev.booking.Entity.MyUserDetails;
import com.dev.booking.Entity.Role;
import com.dev.booking.Entity.User;
import com.dev.booking.Entity.UserRole;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.JWT.JwtUtil;
import com.dev.booking.Repository.RoleRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.Repository.UserRoleRepository;
import com.dev.booking.RequestDTO.RegisterRequest;
import com.dev.booking.ResponseDTO.TokenDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;


    public boolean register(RegisterRequest registerRequest) {
    try {
        registerRequest.getUser().setPassWord(passwordEncoder.encode(registerRequest.getUser().getPassWord()));

        User user = userRepository.save(registerRequest.getUser());

        if( registerRequest.getRoleList().size() == 0){
            Role roleDefault = roleRepository.getByCode("ROLE_GUEST");
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(roleDefault);
            userRoleRepository.save(userRole);
            return true;
        }
        for (Role role : registerRequest.getRoleList()) {
            Example<Role> example = Example.of(role);
            if (roleRepository.exists(example)) {
                UserRole userRole = new UserRole();
                userRole.setUser(user);
                userRole.setRole(role);
                userRoleRepository.save(userRole);
            } else {
                return false;
            }

        }
       return  true;
    }catch (Exception e) {

        e.printStackTrace();
        return false;
    }


    }
    public MyUserDetails getMyUserDetailsById(Long userId) {
        // Tìm User từ userRepository
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        // Tìm danh sách UserRole của User
        List<UserRole> userRoles = userRoleRepository.findByUser(user);

        // Chuyển đổi UserRole sang Role và thu thập vào danh sách roles
        List<Role> roles = userRoles.stream()
                .map(userRole -> roleRepository.findById(userRole.getRole().getId())
                        .orElseThrow(() -> new IllegalStateException("Role not found")))
                .collect(Collectors.toList());

        // Trả về đối tượng MyUserDetails
        return new MyUserDetails(user, roles);
    }

    public MyUserDetails getMyUserDetailsFromAccessToken(HttpServletRequest request) {
        try{
            final String authorizationHeader = request.getHeader("Authorization");
            String accessToken = null;
            String username = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                accessToken = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(accessToken);
            }
            if(jwtUtil.validateToken(accessToken,username)){
                if (username == null) {
                    throw new UsernameNotFoundException("Invalid access token");
                }

                // Tìm User từ userRepository
                User user = userRepository.findByUserName(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found "));

                // Tìm danh sách UserRole của User
                List<UserRole> userRoles = userRoleRepository.findByUser(user);

                // Chuyển đổi UserRole sang Role và thu thập vào danh sách roles
                List<Role> roles = userRoles.stream()
                        .map(userRole -> roleRepository.findById(userRole.getRole().getId())
                                .orElseThrow(() -> new IllegalStateException("Role not found")))
                        .collect(Collectors.toList());

                // Trả về đối tượng MyUserDetails
                return new MyUserDetails(user, roles);
            }
            return  null;
        } catch (Exception e){
            return null;
        }

    }
}
