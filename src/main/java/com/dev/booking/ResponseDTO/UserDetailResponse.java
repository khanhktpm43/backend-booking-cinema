package com.dev.booking.ResponseDTO;

import com.dev.booking.Entity.Role;
import com.dev.booking.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {
    private User user;
    private UserBasicDTO createdBy;
    private UserBasicDTO updatedBy;
    private List<Role> roles;
}
