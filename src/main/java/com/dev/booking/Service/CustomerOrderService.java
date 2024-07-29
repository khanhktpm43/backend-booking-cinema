package com.dev.booking.Service;

import com.dev.booking.Entity.Booking;
import com.dev.booking.Entity.CustomerOrder;
import com.dev.booking.Entity.Food;
import com.dev.booking.Entity.User;
import com.dev.booking.Repository.CustomerOrderRepository;
import com.dev.booking.Repository.FoodRepository;
import com.dev.booking.RequestDTO.OrderFoodDTO;
import com.dev.booking.ResponseDTO.DetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerOrderService {
    @Autowired
    private CustomerOrderRepository customerOrderRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private MappingService mappingService;

    @Transactional
    public List<CustomerOrder> orderFood(User user, Booking booking, List<OrderFoodDTO> orderFoodDTOS){
        List<CustomerOrder> orders = new ArrayList<>();
        for (OrderFoodDTO item : orderFoodDTOS){
            Food food = foodRepository.findById(item.getFood().getId()).orElseThrow();
            CustomerOrder order = new CustomerOrder();
            order.setFood(item.getFood());
            order.setAmount(item.getAmount());
            order.setPrice(item.getAmount() * food.getPrice());
            order.setBooking(booking);
            order.setCreatedAt(LocalDateTime.now());
            order.setCreatedBy(user);
            order.setUpdatedAt(null);
            CustomerOrder createdOrder = customerOrderRepository.save(order);
            orders.add(createdOrder);
        }
        return orders;
    }

    public Page<DetailResponse<CustomerOrder>> getAll(int page, int size, String[] sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<CustomerOrder> customerOrders = customerOrderRepository.findAll( pageable);
        return mappingService.mapToResponse(customerOrders);
    }
    public List<OrderFoodDTO> getDTOByBookingId( Long id){
        return customerOrderRepository.findAllByBookingId(id);
    }
    public void deletedByBooking(Booking booking){
        customerOrderRepository.deleteByBooking(booking);
    }
}