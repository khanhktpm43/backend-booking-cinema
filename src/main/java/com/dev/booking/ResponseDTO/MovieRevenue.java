package com.dev.booking.ResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class MovieRevenue {
    private String movie;
    private double successRevenue;
    private double failedRevenue;

    public MovieRevenue(String movie, double successRevenue, double failedRevenue) {
        this.movie = movie;
        this.successRevenue = successRevenue;
        this.failedRevenue = failedRevenue;
    }
}
