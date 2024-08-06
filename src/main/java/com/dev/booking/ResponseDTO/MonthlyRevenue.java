package com.dev.booking.ResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor

public class MonthlyRevenue {
    private String month;
    private double successRevenue;
    private double failedRevenue;

    public MonthlyRevenue(String month, double successRevenue, double failedRevenue) {
        this.month = month;
        this.successRevenue = successRevenue;
        this.failedRevenue = failedRevenue;
    }
}
