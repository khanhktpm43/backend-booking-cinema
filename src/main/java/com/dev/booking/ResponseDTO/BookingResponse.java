package com.dev.booking.ResponseDTO;

import com.dev.booking.Entity.Booking;
import com.dev.booking.RequestDTO.OrderFoodDTO;
import com.dev.booking.RequestDTO.TicketDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private Booking booking;
    private List<TicketDTO> tickets;
    private List<OrderFoodDTO> orders;
}
