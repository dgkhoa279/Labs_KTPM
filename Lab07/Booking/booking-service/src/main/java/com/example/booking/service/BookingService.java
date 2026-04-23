package com.example.booking.service;

import com.example.booking.dto.BookingRequest;
import com.example.booking.dto.BookingResponse;
import com.example.booking.entity.Booking;
import com.example.booking.event.BookingCreatedEvent;
import com.example.booking.event.BookingEventPublisher;
import com.example.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingEventPublisher eventPublisher;

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for movie: {}", request.getMovieName());

        // 1. Map DTO to Entity
        Booking booking = Booking.builder()
                .movieName(request.getMovieName())
                .numberOfSeats(request.getNumberOfSeats())
                .status("PENDING")
                .build();

        // 2. Save to Database
        booking = bookingRepository.save(booking);
        log.info("Booking saved with ID: {}", booking.getId());

        // 3. Publish Event
        BookingCreatedEvent event = BookingCreatedEvent.builder()
                .bookingId(booking.getId())
                .movieName(booking.getMovieName())
                .numberOfSeats(booking.getNumberOfSeats())
                .build();
        
        eventPublisher.publishBookingCreated(event);

        // 4. Return Response
        return mapToResponse(booking);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .movieName(booking.getMovieName())
                .numberOfSeats(booking.getNumberOfSeats())
                .status(booking.getStatus())
                .build();
    }
}
