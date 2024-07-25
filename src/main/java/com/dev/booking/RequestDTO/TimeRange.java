package com.dev.booking.RequestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeRange {
    private LocalTime startTime;
    private LocalTime endTime;
}
