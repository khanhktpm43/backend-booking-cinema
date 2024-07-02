package com.dev.booking.Controller;

import com.dev.booking.Entity.Room;
import com.dev.booking.Entity.SeatType;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.SeatTypeRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/seat-type")
public class SeatTypeController {
    @Autowired
    private SeatTypeRepository seatTypeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @GetMapping("")
    public ResponseEntity<ResponseObject<List<DetailResponse<SeatType>>>> getAll(){
        List<SeatType> seatTypes = seatTypeRepository.findAll();

        List<DetailResponse<SeatType>> result = seatTypes.stream().map(type -> {
            UserBasicDTO createdBy = null;
            if (type.getCreatedBy() != null) {
                User user = userRepository.findById(type.getCreatedBy()).orElse(null);
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            UserBasicDTO updatedBy = null;
            if (type.getUpdatedBy() != null) {
                User user = userRepository.findById(type.getUpdatedBy()).orElse(null);
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            return new DetailResponse<>(type, createdBy, updatedBy);
        }).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", result));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SeatType>>> getById(@PathVariable Long id){
        if (seatTypeRepository.existsById(id)) {
            UserBasicDTO createdBy = null;
            UserBasicDTO updatedBy = null;

            SeatType type = seatTypeRepository.findById(id).orElse(null);
            if (type.getCreatedBy() != null) {
                User user = userRepository.findById(type.getCreatedBy()).orElse(null);
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            if (type.getUpdatedBy() != null) {
                User user = userRepository.findById(type.getUpdatedBy()).orElse(null);
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            DetailResponse<SeatType> response = new DetailResponse<>(type, createdBy, updatedBy);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));

    }

    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<SeatType>>> create(@RequestBody SeatType seatType, HttpServletRequest request){
        Example<SeatType> example = Example.of(seatType);
        if (seatTypeRepository.exists(example)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>("Information already exists", null));
        }
        Map<String, String> tokenAndUsername = jwtRequestFilter.getTokenAndUsernameFromRequest(request);
        String username = (String) tokenAndUsername.get("username");
        User userReq = userRepository.findByUserName(username).orElse(null);
        if(userReq == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        }
        seatType.setId(null);
        seatType.setCreatedBy(userReq.getId());
        seatType.setCreatedAt(LocalDateTime.now());
        seatType.setUpdatedAt(null);
        SeatType newType = seatTypeRepository.save(seatType);
        UserBasicDTO createdBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
        DetailResponse<SeatType> response = new DetailResponse<>(newType, createdBy, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SeatType>>> update(@PathVariable Long id,@RequestBody SeatType seatType, HttpServletRequest request){
        if (seatTypeRepository.existsById(id)) {
            Map<String, String> tokenAndUsername = jwtRequestFilter.getTokenAndUsernameFromRequest(request);
            String username = (String) tokenAndUsername.get("username");
            User userReq = userRepository.findByUserName(username).orElse(null);
            if(userReq == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
            }
            SeatType type = seatTypeRepository.findById(id).orElse(null);
            type.setCode(seatType.getCode());
            type.setName(seatType.getName());
            type.setUpdatedAt(LocalDateTime.now());
            type.setUpdatedBy(userReq.getId());
            SeatType newType = seatTypeRepository.save(type);
            User user = userRepository.findById(newType.getCreatedBy()).orElse(null);
            UserBasicDTO createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            UserBasicDTO updatedBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
            DetailResponse<SeatType> response = new DetailResponse<>(newType, createdBy, updatedBy);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<SeatType>>> delete(@PathVariable Long id){
        if(seatTypeRepository.existsById(id)){
            seatTypeRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }

}
