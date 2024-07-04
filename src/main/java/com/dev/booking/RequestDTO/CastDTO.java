package com.dev.booking.RequestDTO;

import com.dev.booking.Entity.Cast;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CastDTO {
    private Cast cast;
    private int roleCast;
}
