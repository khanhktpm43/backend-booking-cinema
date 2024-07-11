package com.dev.booking.Controller;

import com.dev.booking.Entity.Cast;
import com.dev.booking.Entity.MovieCast;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.CastRepository;
import com.dev.booking.Repository.MovieCastRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import com.dev.booking.Service.CastService;
import com.dev.booking.Service.MappingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/cast")
public class CastController {
    @Autowired
    private CastRepository castRepository;
    @Autowired
    private MovieCastRepository movieCastRepository;
    @Autowired
    private CastService castService;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public ResponseEntity<ResponseObject<List<DetailResponse<Cast>>>> getAll() {
        List<Cast> casts = castRepository.findAll();
        List<DetailResponse<Cast>> responses = mappingService.mapToResponse(casts);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Cast>>> getById(@PathVariable Long id) {
        if (castRepository.existsById(id)) {
            Cast cast = castRepository.findById(id).orElse(null);
            DetailResponse<Cast> response = mappingService.mapToResponse(cast);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<Cast>>> create(@RequestBody Cast cast, HttpServletRequest request) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        if (userReq == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        cast.setId(null);
        cast.setCreatedAt(LocalDateTime.now());
        cast.setCreatedBy(userReq.getId());
        cast.setUpdatedAt(null);
        Cast newCast = castRepository.save(cast);
        UserBasicDTO createdBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
        DetailResponse<Cast> response = new DetailResponse<>(newCast, createdBy, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));

    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Cast>>> update(@PathVariable Long id, @RequestBody Cast cast, HttpServletRequest request) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        if (userReq == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject<>("Not authenticated", null));
        if (castRepository.existsById(id)) {
            Cast cast1 = castRepository.findById(id).orElse(null);
            cast1.setUpdatedBy(userReq.getId());
            cast1.setUpdatedAt(LocalDateTime.now());
            cast1.setName(cast.getName());
            Cast newCast = castRepository.save(cast1);
//            UserBasicDTO createdBy = null;
//            if(newCast.getCreatedBy() != null){
//                User user = userRepository.findById(newCast.getCreatedBy()).orElse(null);
//                if(user != null)
//                    createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
//            }
 //           UserBasicDTO updatedBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
            DetailResponse<Cast> response =mappingService.mapToResponse(newCast);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
    @Transactional
    @DeleteMapping("{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Cast>>> delete(@PathVariable Long id) {
        if (castRepository.existsById(id)) {
            Cast cast = castRepository.findById(id).orElse(null);
            movieCastRepository.deleteByCast(cast);
            castRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
}