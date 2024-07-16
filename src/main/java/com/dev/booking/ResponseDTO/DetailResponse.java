package com.dev.booking.ResponseDTO;

import com.dev.booking.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailResponse<T> {
    private T Object;
    private User createdBy;
    private User updatedBy;
}
