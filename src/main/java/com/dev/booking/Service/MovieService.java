package com.dev.booking.Service;

import com.dev.booking.Entity.Cast;
import com.dev.booking.Entity.Genre;
import com.dev.booking.Entity.Movie;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.MovieRepository;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Value("${upload.path}")
    private String uploadPath;

    public MovieResponse getById(Long id) {
        List<Object[]> results = movieRepository.findDetailById(id);
        if (!results.isEmpty()) {
            Object[] result = results.get(0); // Chỉ lấy một kết quả đầu tiên
            Long movieId = (Long) result[0];
            Integer duration = (Integer) result[1];
            String image = (String) result[2];
            String movieName = (String) result[3];
            byte[] blobData = (byte[]) result[4];
            String overview = new String(blobData, StandardCharsets.UTF_8);
            LocalDateTime release = convertTimestampToLocalDateTime((Timestamp) result[5]);
            byte[] blobTrailer = (byte[]) result[6];
            String trailer = new String(blobTrailer, StandardCharsets.UTF_8);
            LocalDateTime createdAt = convertTimestampToLocalDateTime((Timestamp) result[7]);
            Long createdId = (Long) result[8];
            User createdBy = userRepository.findById(createdId).orElse(null);
            LocalDateTime updatedAt = convertTimestampToLocalDateTime((Timestamp) result[9]);
            Long updatedId = (Long) result[10];
            User updatedBy = null;
            if (updatedId != null)
                userRepository.findById(updatedId).orElse(null);
            Movie movie = new Movie(movieId, movieName, release, image, overview, trailer, duration, createdAt, createdBy, updatedAt, updatedBy);
            List<Genre> genres = new ArrayList<>();
            List<CastDTO> casts = new ArrayList<>();
            for (Object[] obj : results) {
                Long genreId = (Long) obj[11];
                String genreName = (String) obj[12];
                Long castId = (Long) obj[13];
                String castName = (String) obj[14];
                Integer roleCast = (Integer) obj[15];
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(genreName);
                genres.add(genre);
                CastDTO castDTO = new CastDTO();
                castDTO.setId(castId);
                castDTO.setName(castName);
                castDTO.setRoleCast(roleCast);
                casts.add(castDTO);
            }
            MovieResponse movieResponse = new MovieResponse();
            movieResponse.setMovie(movie);

            movieResponse.setGenres(new LinkedHashSet<>(genres).stream().toList());

            movieResponse.setCasts(new LinkedHashSet<>(casts).stream().toList());

            return movieResponse;
        }
        return null;
    }

    private LocalDateTime convertTimestampToLocalDateTime(Timestamp timestamp) {
        if (timestamp != null) {
            return timestamp.toLocalDateTime();
        }
        return null;
    }

    public List<DetailResponse<Movie>> getMoviesWithActiveShowtimes() {
        LocalDateTime currentTime = LocalDateTime.now();
        return mappingService.mapToResponse(movieRepository.findMoviesWithActiveShowtimes(currentTime));
    }

    public List<DetailResponse<Movie>> getMoviesUpcoming() {
        LocalDateTime currentTime = LocalDateTime.now();
        return mappingService.mapToResponse(movieRepository.findMoviesUpcoming(currentTime));
    }

    public Page<DetailResponse<Movie>> getAllByDeleted(boolean b, int page, int size, String[] sort, String name) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Movie> movies;
        if (name == null || name.isEmpty()) {
            movies = movieRepository.findByDeleted(b, pageable);
        } else {
            movies = movieRepository.findByNameContainingIgnoreCaseAndDeleted(name, b, pageable);
        }

        return mappingService.mapToResponse(movies);
    }

    public DetailResponse<Movie> create(HttpServletRequest request, String name, LocalDateTime releaseDate, String overview, int duration, MultipartFile image, String trailer) {
        User userReq = jwtRequestFilter.getUserRequest(request);

            Movie movie = new Movie();
            movie.setName(name);
            movie.setReleaseDate(releaseDate);
            movie.setOverview(overview);
            movie.setDuration(duration);
            movie.setImage(uploadImage(image));
           // movie.setImage(image.getBytes());
            movie.setTrailer(trailer);
            movie.setCreatedAt(LocalDateTime.now());
            movie.setCreatedBy(userReq);
            movie.setUpdatedAt(null);
            Movie newMovie = movieRepository.save(movie);
            DetailResponse<Movie> response = new DetailResponse<>(newMovie, newMovie.getCreatedBy(), null, newMovie.getCreatedAt(), newMovie.getUpdatedAt());
            return response;

    }
    private String uploadImage(MultipartFile file){
        if (file.isEmpty()) {
            return null;
        }
        try {
            File dir = new File(uploadPath);
            if (!dir.exists()) dir.mkdirs();
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String originalFilename = Paths.get(file.getOriginalFilename()).getFileName().toString();
            String newFilename = timestamp + "_" + originalFilename;
            File serverFile = new File(dir.getAbsolutePath() + File.separator + newFilename);
            file.transferTo(serverFile);
            return "/uploads/" + newFilename;
        } catch (IOException e) {
            return null;
        }
    }

    public DetailResponse<Movie> update(Long id, HttpServletRequest request, String name, LocalDateTime releaseDate, String overview, int duration, MultipartFile image, String trailer) throws IOException {
        User userReq = jwtRequestFilter.getUserRequest(request);
        Movie movie = movieRepository.findById(id).orElseThrow();
        movie.setId(id);
        movie.setName(name);
        movie.setReleaseDate(releaseDate);
        movie.setOverview(overview);
        movie.setDuration(duration);
        if(!image.isEmpty()){
            movie.setImage(uploadImage(image));
        }
        movie.setTrailer(trailer);
        movie.setUpdatedAt(LocalDateTime.now());
        movie.setUpdatedBy(userReq);
        Movie newMovie = movieRepository.save(movie);
        return new DetailResponse<>(newMovie, newMovie.getCreatedBy(), userReq, newMovie.getCreatedAt(), newMovie.getUpdatedAt());

    }

    public void delete(HttpServletRequest request, Long id) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        Movie movie = movieRepository.findByIdAndDeleted(id, false).orElseThrow();
        movie.setDeleted(true);
        movie.setUpdatedBy(userReq);
        movie.setUpdatedAt(LocalDateTime.now());
        movieRepository.save(movie);
    }

    public Movie restore(HttpServletRequest request, Long id) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        Movie movie = movieRepository.findByIdAndDeleted(id, true).orElseThrow();
        movie.setDeleted(false);
        movie.setUpdatedAt(LocalDateTime.now());
        movie.setUpdatedBy(userReq);
        return movieRepository.save(movie);
    }
}
