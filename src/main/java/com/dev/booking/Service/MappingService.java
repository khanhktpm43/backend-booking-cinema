package com.dev.booking.Service;

import com.dev.booking.Entity.BaseEntity;
import com.dev.booking.Entity.User;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MappingService {
    @Autowired
    private UserRepository userRepository;

    public <T extends BaseEntity> List<DetailResponse<T>> mapToResponse(List<T> list) {
        return list.stream().map(item -> {
            UserBasicDTO createdBy = null;
            if (item.getCreatedBy() != null) {
                User user = userRepository.findById(item.getCreatedBy()).orElse(null);
                if (user != null) {
                    createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
                }
            }

            UserBasicDTO updatedBy = null;
            if (item.getUpdatedBy() != null) {
                User user = userRepository.findById(item.getUpdatedBy()).orElse(null);
                if (user != null) {
                    updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
                }
            }

            return new DetailResponse<>(item, createdBy, updatedBy);
        }).collect(Collectors.toList());
    }
    public <T extends BaseEntity> DetailResponse<T> mapToResponse(T item) {
        UserBasicDTO createdBy = null;
        if (item.getCreatedBy() != null) {
            User user = userRepository.findById(item.getCreatedBy()).orElse(null);
            if (user != null) {
                createdBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
        }

        UserBasicDTO updatedBy = null;
        if (item.getUpdatedBy() != null) {
            User user = userRepository.findById(item.getUpdatedBy()).orElse(null);
            if (user != null) {
                updatedBy = new UserBasicDTO(user.getId(), user.getName(), user.getEmail());
            }
        }

        return new DetailResponse<>(item, createdBy, updatedBy);
    }
}
