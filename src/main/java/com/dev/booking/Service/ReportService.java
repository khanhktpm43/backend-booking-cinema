package com.dev.booking.Service;

import com.dev.booking.Repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    @Autowired
    private BookingRepository bookingRepository;

}
