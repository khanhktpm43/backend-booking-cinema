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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/casts")
@CrossOrigin(origins = "*")
public class CastController {
    @Autowired
    private CastRepository castRepository;
    @Autowired
    private CastService castService;
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("")
    public ResponseEntity<ResponseObject<Page<DetailResponse<Cast>>>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt,desc") String[] sort,  @RequestParam(defaultValue = "") String name) {
        Page<DetailResponse<Cast>> responses = castService.getAll(page, size, sort, name);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", responses));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Cast>>> getById(@PathVariable Long id) {
        if (castRepository.existsById(id)) {
            DetailResponse<Cast> response = castService.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("")
    public ResponseEntity<ResponseObject<DetailResponse<Cast>>> create(@RequestBody Cast cast, HttpServletRequest request) {
        DetailResponse<Cast> response = castService.create(request, cast);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>("", response));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Cast>>> update(@PathVariable Long id, @RequestBody Cast cast, HttpServletRequest request) {
        if (castRepository.existsById(id)) {
            DetailResponse<Cast> response = castService.update(request, id , cast);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", response));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Transactional
    @DeleteMapping("{id}")
    public ResponseEntity<ResponseObject<DetailResponse<Cast>>> delete(@PathVariable Long id) {
        if (castRepository.existsById(id)) {
            castService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<>("", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>("id does not exist", null));
    }
}
