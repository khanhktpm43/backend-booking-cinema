package com.dev.booking.Event;

import com.dev.booking.Entity.Booking;
import com.dev.booking.Entity.PaymentStatus;
import com.dev.booking.Repository.BookingRepository;
import com.dev.booking.Service.CustomerOrderService;
import com.dev.booking.Service.TicketService;
//import com.dev.booking.TestEvent.DoorBellEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class BookingHandler {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private CustomerOrderService customerOrderService;
    @Autowired
    private BookingRepository repository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<Long, ScheduledFuture<?>> scheduledEvents = new ConcurrentHashMap<>();

    @EventListener
    public void bookingEventListener(BookingEvent bookingEvent) {
        Long bookingId = bookingEvent.getBookingId();
        ScheduledFuture<?> scheduledFuture = scheduler.schedule(() -> {
            if(repository.existsById(bookingId)){
                Booking booking = repository.findById(bookingId).orElseThrow();
                ticketService.changeActiveTickets(booking, false);
                customerOrderService.changeActiveOrders(booking, false);
                booking.setPaymentStatus(PaymentStatus.FAILED);
                repository.save(booking);
            }
            scheduledEvents.remove(bookingId);
        }, 15, TimeUnit.MINUTES);

        scheduledEvents.put(bookingId, scheduledFuture);
    }

    public void cancelEvent(Long bookingId) {
        ScheduledFuture<?> scheduledFuture = scheduledEvents.remove(bookingId);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            System.out.println(String.format("đã hủy"));
        } else {
            System.out.println(String.format("Không tìm thấy sự kiện cho khách "));
        }
    }
}
