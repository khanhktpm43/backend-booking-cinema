package com.dev.booking.RequestDTO;

import com.dev.booking.Entity.Genre;
import com.dev.booking.Entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieGenreDTO {
    private Movie movie;
    private List<Genre> genres;
}
