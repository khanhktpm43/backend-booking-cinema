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
    private UserRepository userRepository;
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
            UserBasicDTO createdBy = null;
            if (movieCast.getCreatedBy() != null) {
                User user = userRepository.findById(movieCast.getCreatedBy()).orElse(null);
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            UserBasicDTO updatedBy = null;
            if (movieCast.getUpdatedBy() != null) {
                User user = userRepository.findById(movieCast.getUpdatedBy()).orElse(null);
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
            return new DetailResponse<>(movieCast, createdBy, updatedBy);
        }).collect(Collectors.toList());
    }

    public DetailResponse<Movie> attachCasts(HttpServletRequest request, MovieCastDTO movieCastDTO) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        if (userReq == null || movieCastDTO.getMovie().getId() == null || !movieRepository.existsById(movieCastDTO.getMovie().getId()) || movieCastDTO.getCasts().isEmpty())
            return null;
        Movie movie = movieRepository.findById(movieCastDTO.getMovie().getId()).orElse(null);
        for (CastDTO cast : movieCastDTO.getCasts()) {
            if (cast.getCast().getId() == null || !castRepository.existsById(cast.getCast().getId()) || (cast.getRoleCast() != 1 && cast.getRoleCast() != 2))
                continue;
            MovieCast movieCast = new MovieCast();
            movieCast.setMovie(movie);
            movieCast.setCast(cast.getCast());
            movieCast.setRoleCast(cast.getRoleCast());
            movieCast.setCreatedAt(LocalDateTime.now());
            movieCast.setUpdatedAt(null);
            movieCast.setCreatedBy(userReq.getId());
            movieCastRepository.save(movieCast);
        }
        Movie result = movieRepository.findById(movieCastDTO.getMovie().getId()).orElse(null);
        UserBasicDTO createdBy = null;
        UserBasicDTO updatedBy = null;
        if (result != null && result.getCreatedBy() != null) {
            User user = userRepository.findById(result.getCreatedBy()).orElse(null);
            if (user != null)
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
        }
        if (result != null && result.getUpdatedBy() != null) {
            User user = userRepository.findById(result.getUpdatedBy()).orElse(null);
            if (user != null)
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
        }
        DetailResponse<Movie> response = new DetailResponse<>(result, createdBy, updatedBy);
        return response;
    }

    public DetailResponse<MovieCast> update(User userReq, Long id, MovieCast movieCast) {
        MovieCast movieCast1 = movieCastRepository.findById(id).orElse(null);
        movieCast1.setMovie(movieCast.getMovie());
        movieCast1.setCast(movieCast.getCast());
        movieCast1.setRoleCast(movieCast.getRoleCast());
        movieCast1.setUpdatedAt(LocalDateTime.now());
        movieCast1.setUpdatedBy(userReq.getId());
        MovieCast movieCast2 = movieCastRepository.save(movieCast1);
        UserBasicDTO createdBy = null;
        if(movieCast2.getCreatedBy() != null && userRepository.existsById(movieCast2.getCreatedBy())){
            User user = userRepository.findById(movieCast2.getCreatedBy()).orElse( null);
            createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
        }
        UserBasicDTO updatedBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
        DetailResponse<MovieCast> response = new DetailResponse<>(movieCast2, createdBy, updatedBy);
        return response;
    }
//
//    public DetailResponse<MovieGenre> update(User userReq, Long id, MovieCast movieCast) {
//        MovieGenre movieGenre1 = movieGenreRepository.findById(id).orElse(null);
//        movieGenre1.setMovie(movieGenre.getMovie());
//        movieGenre1.setGenre(movieGenre.getGenre());
//        movieGenre1.setUpdatedAt(LocalDateTime.now());
//        movieGenre1.setUpdatedBy(userReq.getId());
//        MovieGenre movieGenre2 = movieGenreRepository.save(movieGenre1);
//        UserBasicDTO createdBy = null;
//        if(movieGenre2.getCreatedBy() != null && userRepository.existsById(movieGenre2.getCreatedBy())){
//            User user = userRepository.findById(movieGenre2.getCreatedBy()).orElse( null);
//            createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
//        }
//        UserBasicDTO updatedBy = new UserBasicDTO(userReq.getId(), userReq.getName(), userReq.getEmail());
//        DetailResponse<MovieGenre> response = new DetailResponse<>(movieGenre2, createdBy, updatedBy);
//        return response;
//    }
}
