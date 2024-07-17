package com.dev.booking.Service;

import com.dev.booking.Entity.*;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.CastRepository;
import com.dev.booking.Repository.MovieCastRepository;
import com.dev.booking.Repository.MovieRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.RequestDTO.CastDTO;
import com.dev.booking.RequestDTO.MovieCastDTO;
import com.dev.booking.RequestDTO.MovieGenreDTO;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<DetailResponse<MovieCast>> mapMovieCastToResponse(List<MovieCast> movieCasts) {
        return movieCasts.stream().map(movieCast -> {
            return new DetailResponse<>(movieCast, movieCast.getCreatedBy(), movieCast.getUpdatedBy());
        }).collect(Collectors.toList());
    }

    public DetailResponse<Movie> attachCasts(HttpServletRequest request, MovieCastDTO movieCastDTO) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        if (userReq == null || movieCastDTO.getMovie().getId() == null || !movieRepository.existsById(movieCastDTO.getMovie().getId()) || movieCastDTO.getCasts().isEmpty()) {
            return null;
        }
        Movie movie = movieRepository.findById(movieCastDTO.getMovie().getId()).orElse(null);
        if (movie == null) {
            return null;
        }
        for (CastDTO castDTO : movieCastDTO.getCasts()) {
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
        DetailResponse<Movie> response = new DetailResponse<>(result, result.getCreatedBy(), result.getUpdatedBy());
        return response;
    }


    public DetailResponse<MovieCast> update(User userReq, Long id, CastDTO castDTO) {
        MovieCast movieCast1 = movieCastRepository.findById(id).orElse(null);


        movieCast1.setCast(castDTO.getCast());
        movieCast1.setRoleCast(castDTO.getRoleCast());
        movieCast1.setUpdatedAt(LocalDateTime.now());
        movieCast1.setUpdatedBy(userReq);
        MovieCast movieCast2 = movieCastRepository.save(movieCast1);
        DetailResponse<MovieCast> response = new DetailResponse<>(movieCast2, movieCast2.getCreatedBy(), userReq);
        return response;
    }
}
