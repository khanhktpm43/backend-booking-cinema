package com.dev.booking.TestEvent;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class MyDog {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<String, ScheduledFuture<?>> scheduledEvents = new ConcurrentHashMap<>();

    @EventListener
    public void doorBellEventListener(DoorBellEvent doorBellEvent) {
        String guestName = doorBellEvent.getGuestName();
        ScheduledFuture<?> scheduledFuture = scheduler.schedule(() -> {
            System.out.println("Chó ngủ dậy sau 15 phút!!!");
            System.out.println(String.format("Go go!! Có người tên là %s gõ cửa!!!", guestName));
            scheduledEvents.remove(guestName);
        }, 4, TimeUnit.MINUTES);

        scheduledEvents.put(guestName, scheduledFuture);
    }

    public void cancelEvent(String guestName) {
        ScheduledFuture<?> scheduledFuture = scheduledEvents.remove(guestName);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            System.out.println(String.format("Đã hủy sự kiện cho khách %s", guestName));
        } else {
            System.out.println(String.format("Không tìm thấy sự kiện cho khách %s", guestName));
        }
    }
}

