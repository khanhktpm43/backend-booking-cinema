package com.dev.booking.Service;

import com.dev.booking.Entity.Cast;
import com.dev.booking.Entity.Genre;
import com.dev.booking.Entity.Movie;
import com.dev.booking.Repository.MovieRepository;
import com.dev.booking.ResponseDTO.CastDTO;
import com.dev.booking.ResponseDTO.MovieResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            String overview = (String) result[4];
            LocalDateTime release = (LocalDateTime) result[5];
            byte[] trailer = (byte[]) result[6];
            Movie movie = new Movie(movieId,movieName,release,image,overview,trailer,duration);

            List<Genre> genres = new ArrayList<>();
            List<CastDTO> casts = new ArrayList<>();

            for (Object[] obj : results) {
//                Long movieId = (Long) obj[0];
//                Integer duration = (Integer) obj[1];
//                byte[] image = (byte[]) obj[2];
//                String movieName= (String) obj[3];
//                String overview = (String) obj[4];
//                Date release = (Date) obj[5];
//                byte[] trailer = (byte[]) obj[6];
                Long genreId = (Long) obj[7];
                String genreName = (String) obj[8];
                Long castId = (Long) obj[9];
                String castName = (String) obj[10];
                Integer roleCast = (Integer) obj[11];

                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(genreName);
                genres.add(genre);

                Cast cast = new Cast();
                cast.setId(castId);
                cast.setName(castName);

                CastDTO castDTO = new CastDTO();
                castDTO.setCast(cast);
                castDTO.setRoleCast(roleCast);
                casts.add(castDTO);
            }

            MovieResponse movieResponse = new MovieResponse();
            movieResponse.setMovie(movie);
            movieResponse.setGenres(genres);
            movieResponse.setCasts(casts);

            return movieResponse;
        }
        return null;
    }
}
