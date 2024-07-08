package com.dev.booking.Service;

import com.dev.booking.Entity.Cast;
import com.dev.booking.Entity.Genre;
import com.dev.booking.Entity.Movie;
import com.dev.booking.Repository.MovieRepository;
import com.dev.booking.ResponseDTO.CastDTO;
import com.dev.booking.ResponseDTO.MovieResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    public MovieResponse getById(Long id) {
        List<Object[]> results = movieRepository.findDetailById(id);
        if (!results.isEmpty()) {
            Object[] result = results.get(0); // Chỉ lấy một kết quả đầu tiên
            Long movieId = (Long) result[0];
            Integer duration = (Integer) result[1];
            byte[] image = (byte[]) result[2];
            String movieName= (String) result[3];
            byte[] blobData = (byte[]) result[4];
            String overview = new String(blobData, StandardCharsets.UTF_8);
            LocalDateTime release =  convertTimestampToLocalDateTime((Timestamp) result[5]);
            byte[] blobTrailer = (byte[]) result[6];
            String trailer = new String(blobTrailer, StandardCharsets.UTF_8);
            LocalDateTime createdAt =  convertTimestampToLocalDateTime((Timestamp) result[7]);
            Long createdBy = (Long) result[8];
            LocalDateTime updatedAt =  convertTimestampToLocalDateTime((Timestamp) result[9]);
            Long updatedBy = (Long) result[10];
            Movie movie = new Movie(movieId,movieName,release,image,overview,trailer,duration,createdAt, createdBy, updatedAt, updatedBy);
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
}
