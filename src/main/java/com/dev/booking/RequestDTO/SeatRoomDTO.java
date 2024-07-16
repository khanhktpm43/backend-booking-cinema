package com.dev.booking.RequestDTO;

import com.dev.booking.Entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatRoomDTO {
    private Room room;
    private List<SeatDTO> seats;
}
