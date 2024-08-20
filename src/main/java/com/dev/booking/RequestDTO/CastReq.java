package com.dev.booking.RequestDTO;

import com.dev.booking.Entity.Cast;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CastReq {
    private Cast cast;
    private int roleCast;
}
