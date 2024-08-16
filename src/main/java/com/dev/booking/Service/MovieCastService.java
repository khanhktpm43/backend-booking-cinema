package com.dev.booking.Service;

import com.dev.booking.Entity.*;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.CastRepository;
import com.dev.booking.Repository.MovieCastRepository;
import com.dev.booking.Repository.MovieRepository;
import com.dev.booking.RequestDTO.CastReq;
import com.dev.booking.RequestDTO.MovieCastDTO;
import com.dev.booking.ResponseDTO.DetailResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MovieCastService {
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private CastRepository castRepository;
    @Autowired
    private MovieCastRepository movieCastRepository;
    @Autowired
    private MappingService mappingService;

    public DetailResponse<Movie> attachCasts(HttpServletRequest request, MovieCastDTO movieCastDTO) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        if (userReq == null || movieCastDTO.getMovie().getId() == null || !movieRepository.existsById(movieCastDTO.getMovie().getId()) || movieCastDTO.getCasts().isEmpty()) {
            return null;
        }
        Movie movie = movieRepository.findById(movieCastDTO.getMovie().getId()).orElse(null);
        if (movie == null) {
            return null;
        }
        for (CastReq castDTO : movieCastDTO.getCasts()) {
            if (castDTO.getCast().getId() == null || !castRepository.existsById(castDTO.getCast().getId()) || (castDTO.getRoleCast() != 1 && castDTO.getRoleCast() != 2)) {
                continue;
            }
            Cast managedCast = castRepository.findById(castDTO.getCast().getId()).orElse(null);
            if (managedCast == null) {
                continue;
            }
            MovieCast movieCast = new MovieCast();
            movieCast.setMovie(movie);
            movieCast.setCast(managedCast);
            movieCast.setRoleCast(castDTO.getRoleCast());
            movieCast.setCreatedAt(LocalDateTime.now());
            movieCast.setUpdatedAt(null);
            movieCast.setCreatedBy(userReq);
            movieCastRepository.save(movieCast);
        }

        Movie result = movieRepository.findById(movieCastDTO.getMovie().getId()).orElse(null);
        if (result == null) {
            return null;
        }
        DetailResponse<Movie> response = new DetailResponse<>(result, result.getCreatedBy(), result.getUpdatedBy(), result.getCreatedAt(), result.getUpdatedAt());
        return response;
    }


    public DetailResponse<MovieCast> update(HttpServletRequest request, Long id, CastReq castDTO) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        MovieCast movieCast1 = movieCastRepository.findById(id).orElseThrow();
        movieCast1.setCast(castDTO.getCast());
        movieCast1.setRoleCast(castDTO.getRoleCast());
        movieCast1.setUpdatedAt(LocalDateTime.now());
        movieCast1.setUpdatedBy(userReq);
        MovieCast movieCast2 = movieCastRepository.save(movieCast1);
        return mappingService.mapToResponse(movieCast2);
    }

    public Page<DetailResponse<MovieCast>> getAll(int page, int size, String[] sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<MovieCast> movieCasts = movieCastRepository.findAll(pageable);
        return mappingService.mapToResponse(movieCasts);
    }

    public DetailResponse<MovieCast> getById(Long id) {
        MovieCast movieCast = movieCastRepository.findById(id).orElse(null);
        return mappingService.mapToResponse(movieCast);
    }
}
