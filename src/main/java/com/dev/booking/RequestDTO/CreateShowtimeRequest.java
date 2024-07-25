package com.dev.booking.RequestDTO;

import com.dev.booking.Entity.Movie;
import com.dev.booking.Entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateShowtimeRequest {
    private Movie movie;
    private Room room;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TimeRange> times;
}
