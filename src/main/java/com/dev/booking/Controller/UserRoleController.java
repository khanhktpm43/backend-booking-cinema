package com.dev.booking.Controller;

import com.dev.booking.Repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user-role")
public class UserRoleController {
    @Autowired
    private UserRoleRepository userRoleRepository;

}
