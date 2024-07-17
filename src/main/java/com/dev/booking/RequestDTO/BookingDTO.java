package com.dev.booking.RequestDTO;

import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.Showtime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Showtime showtime;
    private List<Seat> seats;
    private List<OrderFoodDTO> foodOrderList;
    private float totalPrice;
}
