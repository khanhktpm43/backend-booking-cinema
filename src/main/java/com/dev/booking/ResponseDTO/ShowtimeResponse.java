package com.dev.booking.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowtimeResponse extends  MovieResponse{
   // private MovieResponse movies;
    private List<ShowtimeDTO> showtimes;
}
