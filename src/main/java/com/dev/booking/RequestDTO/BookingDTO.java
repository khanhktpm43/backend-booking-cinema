package com.dev.booking.RequestDTO;

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
    private List<TicketDTO> ticketList;
    private List<FoodOrderDTO> foodOrderList;
    private float totalPrice;
}
