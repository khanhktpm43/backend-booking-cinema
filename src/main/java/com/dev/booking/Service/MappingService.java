package com.dev.booking.Service;

import com.dev.booking.Entity.BaseEntity;
import com.dev.booking.Entity.User;
import com.dev.booking.Repository.UserRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.UserBasicDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MappingService {
    public <T extends BaseEntity> Page<DetailResponse<T>> mapToResponse(Page<T> list) {
        Pageable pageable = list.getPageable();
        long total = list.getTotalElements();
        Page<DetailResponse<T>> responsePage = list.map(item -> {
            return new DetailResponse<>(item, item.getCreatedBy(), item.getUpdatedBy(), item.getCreatedAt(), item.getUpdatedAt());
        });
        return new PageImpl<>(responsePage.getContent(), pageable, total);

    }

    public <T extends BaseEntity> List<DetailResponse<T>> mapToResponse(List<T> list) {
        return list.stream()
                .map(item -> new DetailResponse<>(item, item.getCreatedBy(), item.getUpdatedBy(), item.getCreatedAt(), item.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    public <T extends BaseEntity> DetailResponse<T> mapToResponse(T item) {
        return new DetailResponse<>(item, item.getCreatedBy(), item.getUpdatedBy(), item.getCreatedAt(), item.getUpdatedAt());
    }
}
