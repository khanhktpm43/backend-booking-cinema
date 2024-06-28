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
import com.dev.booking.RequestDTO.CreateUserRequest;
import com.dev.booking.RequestDTO.LoginDTO;
import com.dev.booking.RequestDTO.RegisterRequest;
import com.dev.booking.ResponseDTO.TokenDTO;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.ResponseDTO.UserDetailResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Transactional
    public TokenDTO register(RegisterRequest request) {
    try {

        User user = new User(request.getName(), request.getUserName(), request.getEmail(), request.getPhone(),passwordEncoder.encode(request.getPassWord()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User user1 = userRepository.save(user);
        user1.setCreatedBy(user1);

        userRepository.save(user1);
            Role roleDefault = roleRepository.getByCode("ROLE_GUEST");
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(roleDefault);
            userRoleRepository.save(userRole);
        LoginDTO loginDTO = new LoginDTO(user.getUserName(), request.getPassWord());
            TokenDTO tokenDTO = authService.login(loginDTO);
            return tokenDTO;

    }catch (DataIntegrityViolationException e) {
        // In ra stack trace để debug
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        String errorMessage = "Lỗi: thông tin đã tồn tại trong hệ thống.";
        return null;
    }


    }
    @Transactional
    public UserDetailResponse create( HttpServletRequest httpRequest,CreateUserRequest createUserRequest) {
        try {
            Map<String, String> tokenAndUsername = jwtRequestFilter.getTokenAndUsernameFromRequest(httpRequest);

            String username = (String) tokenAndUsername.get("username");
            User userReq = userRepository.findByUserName(username).orElse(null);

            List<Role> roles = new ArrayList<>();
            User user = new User(createUserRequest.getUser().getName(), createUserRequest.getUser().getUserName(), createUserRequest.getUser().getEmail(), createUserRequest.getUser().getPhone(),passwordEncoder.encode(createUserRequest.getUser().getPassWord()));
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setCreatedBy(userReq);
            User user1 = userRepository.save(user);


            UserBasicDTO createdBy = convertToCreatedBasicDto(userReq);
            UserBasicDTO updateBy = null;
            if( createUserRequest.getRoles().isEmpty()){
                Role roleDefault = roleRepository.getByCode("ROLE_GUEST");
                UserRole userRole = new UserRole();
                userRole.setUser(user);
                userRole.setRole(roleDefault);

                roles.add(roleDefault);
                userRoleRepository.save(userRole);
                return new UserDetailResponse(user1,createdBy,updateBy,roles);
            }
            for (Role role : createUserRequest.getRoles()) {

                if (roleRepository.existsById(role.getId())) {
                    Role role1 = roleRepository.findById(role.getId()).orElseThrow();
                    roles.add(role1);
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(role);
                    userRoleRepository.save(userRole);
                }

            }
            return  new UserDetailResponse(user1,createdBy,updateBy,roles);
        }catch (DataIntegrityViolationException e) {
            // In ra stack trace để debug
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            String errorMessage = "Lỗi: thông tin đã tồn tại trong hệ thống.";
            return null;
        }


    }
    public UserDetailResponse getMyUserDetailsById(Long userId) {
        // Tìm User từ userRepository
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        UserBasicDTO createdBy = convertToCreatedBasicDto(user);
        UserBasicDTO updateBy = convertToUpdatedBasicDto(user);

        // Tìm danh sách UserRole của User
        List<UserRole> userRoles = userRoleRepository.findByUser(user);

        // Chuyển đổi UserRole sang Role và thu thập vào danh sách roles
        List<Role> roles = userRoles.stream()
                .map(userRole -> roleRepository.findById(userRole.getRole().getId())
                        .orElseThrow(() -> new IllegalStateException("Role not found")))
                .collect(Collectors.toList());

        // Trả về đối tượng MyUserDetails
        return new UserDetailResponse(user,createdBy,updateBy, roles);
    }

    public UserDetailResponse getMyUserDetailsFromAccessToken(HttpServletRequest request) {
        try{
            Map<String, String> tokenAndUsername = jwtRequestFilter.getTokenAndUsernameFromRequest(request);
            String accessToken = (String) tokenAndUsername.get("accessToken");
            String username = (String) tokenAndUsername.get("username");
            if(jwtUtil.validateToken(accessToken,username)){
                if (username == null) {
                    throw new UsernameNotFoundException("Invalid access token");
                }

                // Tìm User từ userRepository
                User user = userRepository.findByUserName(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found "));
                UserBasicDTO createdBy = convertToCreatedBasicDto(user);
                UserBasicDTO updateBy = convertToUpdatedBasicDto(user);


                // Tìm danh sách UserRole của User
                List<UserRole> userRoles = userRoleRepository.findByUser(user);

                // Chuyển đổi UserRole sang Role và thu thập vào danh sách roles
                List<Role> roles = userRoles.stream()
                        .map(userRole -> roleRepository.findById(userRole.getRole().getId())
                                .orElseThrow(() -> new IllegalStateException("Role not found")))
                        .collect(Collectors.toList());

                // Trả về đối tượng MyUserDetails
                return new UserDetailResponse(user, createdBy, updateBy, roles);
            }
            return  null;
        } catch (Exception e){
            return null;
        }

    }

    public List<UserDetailResponse> getAll(){
            List<User> users = userRepository.findAll();
            return users.stream().map(user -> {
                UserBasicDTO cretedBy = convertToCreatedBasicDto(user);
                UserBasicDTO updatedBy = convertToUpdatedBasicDto(user);
                List<Role> roles = userRoleRepository.findByUser(user)
                        .stream()
                        .map(UserRole::getRole)
                        .collect(Collectors.toList());
                return new UserDetailResponse(user,cretedBy,updatedBy, roles);
            }).collect(Collectors.toList());

    }
    private UserBasicDTO convertToCreatedBasicDto(User user) {
        if (user == null) {
            return null;
        }
        User createdBy = user.getCreatedBy();
        if (createdBy == null) {
            return null; // or handle this case as needed
        }
        UserBasicDTO dto = new UserBasicDTO();
        dto.setId(user.getCreatedBy().getId());
        dto.setName(user.getCreatedBy().getName());
        dto.setEmail(user.getCreatedBy().getEmail());
        return dto;
    }
    private UserBasicDTO convertToUpdatedBasicDto(User user) {
        if (user == null) {
            return null;
        }

        User updatedBy = user.getUpdatedBy();
        if (updatedBy == null) {
            return null; // or handle this case as needed
        }
        UserBasicDTO dto = new UserBasicDTO();
        dto.setId(user.getUpdatedBy().getId());
        dto.setName(user.getUpdatedBy().getName());
        dto.setEmail(user.getUpdatedBy().getEmail());
        return dto;
    }
}
