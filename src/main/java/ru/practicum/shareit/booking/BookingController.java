package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.annotation.ValidBookingStatus;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingResponse;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    private BookingResponse createBooking(@RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
                                          @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    private BookingResponse approveBooking(@PathVariable Long bookingId,
                                           @RequestParam String approved,
                                           @RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        if (approved.equals("true") || approved.equals("false")) {
            return bookingService.approveBooking(bookingId, approved, ownerId);
        } else throw new ValidationException("Неправильно передан параметр approved");
    }

    @GetMapping("/{bookingId}")
    private BookingResponse findById(@PathVariable Long bookingId,
                                     @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    private List<BookingResponse> allBookingsByBooker(@RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
                                                      @ValidBookingStatus @RequestParam(required = false) String state) {
        if (state == null || state.equalsIgnoreCase("all")) {
            return bookingService.allBookingsByBooker(bookerId);
        }
        if (state != null) {
            boolean anyMatchStatus = Arrays.stream(BookingStatus.values())
                    .anyMatch(bookingStatus -> bookingStatus.name().equalsIgnoreCase(state));
            if (!anyMatchStatus) {
                throw new UnsupportedStatusException("UNSUPPORTED_STATUS");
            }
        }
        return bookingService.allBookingsByBookerAndStatus(state, bookerId);
    }

    @GetMapping("/owner")
    private List<BookingResponse> allBookingsByOwner(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                                     @ValidBookingStatus @RequestParam(required = false) String state) {
        if (state == null || state.equalsIgnoreCase("all")) {
            return bookingService.allBookingsByOwner(ownerId);
        }
        boolean anyMatchStatus = Arrays.stream(BookingStatus.values())
                .anyMatch(bookingStatus -> bookingStatus.name().equalsIgnoreCase(state));
        if (!anyMatchStatus) {
            throw new UnsupportedStatusException("Unknown state: " + state);
        }
        return bookingService.allBookingsByOwnerAndStatus(state, ownerId);
    }
}
