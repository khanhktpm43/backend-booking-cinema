package com.dev.booking.Service;

import com.dev.booking.Entity.Booking;
import com.dev.booking.Entity.CustomerOrder;
import com.dev.booking.Entity.Food;
import com.dev.booking.Entity.User;
import com.dev.booking.Repository.CustomerOrderRepository;
import com.dev.booking.Repository.FoodRepository;
import com.dev.booking.RequestDTO.OrderFoodDTO;
import org.springframework.beans.factory.annotation.Autowired;
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
}
