package com.dev.booking.Controller;

import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.CustomerOrderRepository;
import com.dev.booking.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/customer-order")
public class CustomerOrderController {
    @Autowired
    private CustomerOrderRepository customerOrderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;


}
