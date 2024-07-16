package com.dev.booking.Service;

import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.BookingRepository;
import com.dev.booking.RequestDTO.BookingDTO;
import com.dev.booking.ResponseDTO.BillDTO;
import com.dev.booking.ResponseDTO.DetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
    @Autowired
    private BookingRepository repository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Transactional
    public DetailResponse<BillDTO> createBill(BookingDTO bookingDTO){
        return null;
    }
}
