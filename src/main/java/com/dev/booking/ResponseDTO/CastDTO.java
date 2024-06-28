package com.dev.booking.ResponseDTO;

import com.dev.booking.Entity.Cast;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CastDTO {
    private Cast cast;
    private Integer roleCast;
}
