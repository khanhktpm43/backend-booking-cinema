package com.dev.booking.ResponseDTO;

import lombok.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Data
@NoArgsConstructor
@Getter
@Setter
public class DailyRevenue {
    private Date bookingDate;
    private double successRevenue;
    private double failedRevenue;

    // Constructor
    public DailyRevenue(Date bookingDate, double successRevenue, double failedRevenue) {
        this.bookingDate =  bookingDate;
        this.successRevenue = successRevenue;
        this.failedRevenue = failedRevenue;
    }

}
