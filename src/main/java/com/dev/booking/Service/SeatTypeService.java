package com.dev.booking.Service;

import com.dev.booking.Entity.SeatType;
import com.dev.booking.Entity.User;
import com.dev.booking.JWT.JwtRequestFilter;
import com.dev.booking.Repository.SeatTypeRepository;
import com.dev.booking.ResponseDTO.DetailResponse;
import com.dev.booking.ResponseDTO.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SeatTypeService {
    @Autowired
    private SeatTypeRepository seatTypeRepository;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    public Page<DetailResponse<SeatType>> getByDeleted(boolean b, int page, int size, String[] sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<SeatType> seatTypes = seatTypeRepository.findAllByDeleted(b, pageable);
        return mappingService.mapToResponse(seatTypes);
    }

    public DetailResponse<SeatType> getById(Long id) {
        SeatType type = seatTypeRepository.findById(id).orElse(null);
        return mappingService.mapToResponse(type);
    }

    public DetailResponse<SeatType> create(HttpServletRequest request, SeatType seatType) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        seatType.setId(null);
        seatType.setCreatedBy(userReq);
        seatType.setCreatedAt(LocalDateTime.now());
        seatType.setUpdatedAt(null);
        SeatType newType = seatTypeRepository.save(seatType);
        return mappingService.mapToResponse(newType);
    }

    public DetailResponse<SeatType> update(HttpServletRequest request, Long id, SeatType seatType) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        SeatType type = seatTypeRepository.findById(id).orElseThrow();
        type.setCode(seatType.getCode());
        type.setName(seatType.getName());
        type.setUpdatedAt(LocalDateTime.now());
        type.setUpdatedBy(userReq);
        SeatType newType = seatTypeRepository.save(type);
        return mappingService.mapToResponse(newType);
    }

    public void delete(HttpServletRequest request, Long id) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        SeatType seatType = seatTypeRepository.findById(id).orElseThrow();
        seatType.setDeleted(true);
        seatType.setUpdatedBy(userReq);
        seatType.setUpdatedAt(LocalDateTime.now());
        seatTypeRepository.save(seatType);
    }

    public DetailResponse<SeatType> restore(HttpServletRequest request, Long id) {
        User userReq = jwtRequestFilter.getUserRequest(request);
        SeatType seatType = seatTypeRepository.findById(id).orElseThrow();
        seatType.setDeleted(true);
        seatType.setUpdatedBy(userReq);
        seatType.setUpdatedAt(LocalDateTime.now());
        SeatType seatType1 = seatTypeRepository.save(seatType);
        return mappingService.mapToResponse(seatType1);
    }
}
