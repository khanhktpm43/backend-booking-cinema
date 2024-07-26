package com.dev.booking.Service;

import com.dev.booking.Entity.Cast;
import com.dev.booking.Entity.Genre;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.CastRepository;
import com.dev.booking.Repository.MovieCastRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CastService {
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private CastRepository castRepository;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private MovieCastRepository movieCastRepository;

    public DetailResponse<Cast> create(HttpServletRequest request, Cast cast) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        cast.setId(null);
        cast.setCreatedAt(LocalDateTime.now());
        cast.setCreatedBy(userReq);
        cast.setUpdatedAt(null);
        Cast newCast = castRepository.save(cast);
        return mappingService.mapToResponse(newCast);
    }

    public Page<DetailResponse<Cast>> getAll(int page, int size, String[] sort, String name) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Cast> casts;
        if (name == null || name.isEmpty()) {
            casts = castRepository.findAll(pageable);
            return mappingService.mapToResponse(casts);
        }
        casts = castRepository.findByNameContainingIgnoreCase(name, pageable);
        return mappingService.mapToResponse(casts);
    }

    public DetailResponse<Cast> getById(Long id) {
        Cast cast = castRepository.findById(id).orElse(null);
        return mappingService.mapToResponse(cast);
    }

    public DetailResponse<Cast> update(HttpServletRequest request, Long id, Cast cast) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        Cast cast1 = castRepository.findById(id).orElseThrow();
        cast1.setUpdatedBy(userReq);
        cast1.setUpdatedAt(LocalDateTime.now());
        cast1.setName(cast.getName());
        Cast newCast = castRepository.save(cast1);
        return mappingService.mapToResponse(newCast);
    }

    public void delete(Long id) {
        Cast cast = castRepository.findById(id).orElse(null);
        movieCastRepository.deleteByCast(cast);
        castRepository.deleteById(id);
    }
}
