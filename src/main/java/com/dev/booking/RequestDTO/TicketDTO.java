package com.dev.booking.RequestDTO;
import com.dev.booking.Entity.Seat;
import com.dev.booking.Entity.Showtime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketDTO {
    private Seat seat;
    private Showtime showtime;
    private float price;
}
