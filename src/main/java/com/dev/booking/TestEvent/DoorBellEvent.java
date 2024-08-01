package com.dev.booking.TestEvent;

import org.springframework.context.ApplicationEvent;

public class DoorBellEvent extends ApplicationEvent {
    private final String guestName;

    public DoorBellEvent(Object source, String guestName) {
        super(source);
        this.guestName = guestName;
    }

    public String getGuestName() {
        return guestName;
    }
}

