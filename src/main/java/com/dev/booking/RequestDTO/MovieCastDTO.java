package com.dev.booking.RequestDTO;

import com.dev.booking.Entity.Cast;
import com.dev.booking.Entity.Movie;
import com.dev.booking.Entity.MovieGenre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MovieCastDTO {
    private Movie movie;
    private List<CastDTO> casts;
}
