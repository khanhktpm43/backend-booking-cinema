package com.dev.booking.ResponseDTO;

import com.dev.booking.Entity.Ticket;
import com.dev.booking.RequestDTO.OrderFoodDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDTO {
    private String customerName;
    private String phone;
    private String mail;
    private LocalDateTime createdAt;
    private String movieName;
    private LocalDateTime showtime;
    private String room;
    private List<Ticket> seats;
    private List<OrderFoodDTO> foods;
    private float totalPrice;
}
