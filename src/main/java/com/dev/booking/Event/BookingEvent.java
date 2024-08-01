package com.dev.booking.Event;

import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class BookingEvent  extends ApplicationEvent {
    private final Long bookingId;

    public BookingEvent(Object source, Long bookingId) {
        super(source);
        this.bookingId = bookingId;
    }



}
