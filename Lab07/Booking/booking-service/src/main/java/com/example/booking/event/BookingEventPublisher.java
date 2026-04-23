package com.example.booking.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventPublisher {

    private final StreamBridge streamBridge;

    public void publishBookingCreated(BookingCreatedEvent event) {
        log.info("Publishing event BOOKING_CREATED via Cloud Stream: {}", event);
        // "bookingCreated-out-0" maps to the destination BOOKING_CREATED in application.yml
        streamBridge.send("bookingCreated-out-0", event);
    }
}
