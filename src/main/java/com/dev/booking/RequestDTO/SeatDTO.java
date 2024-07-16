package com.dev.booking.RequestDTO;

import com.dev.booking.Entity.SeatType;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatDTO {
    private String name;
    private String row;
    private int column;
    private SeatType type;
}
