package com.dev.booking.ResponseDTO;

import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.SeatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeSeat {
    private Seat seat;
    private boolean isBooked ;
}
