package com.dev.booking.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailResponse<T> {
    private T Object;
    private UserBasicDTO createdBy;
    private UserBasicDTO updatedBy;
}
