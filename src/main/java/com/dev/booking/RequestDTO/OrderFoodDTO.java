package com.dev.booking.RequestDTO;

import com.dev.booking.Entity.Food;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderFoodDTO {
    private Food food;
    private Integer amount;
    private float price;
}
