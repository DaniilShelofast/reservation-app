package bot.reservation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import bot.reservation.dto.booking.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingDto addBooking(CreateBooking createBooking);

    List<BookingDto> getMyBooking();

    Page<BookingDto> getAllBooking(Pageable pageable);

    BookingDto updateBookingById(Long bookingId, UpdateBookingDto updateBookingDto);

    void deleteBookingById(Long bookingId);

    Set<Integer> getBookedHours(BookingAvailabilityDto request);

    void createBookings(CreateBookingListDto request);

    void book(long roomId, LocalDate date, Set<Integer> hours, Long userId, String userName);

    Set<Integer> getBookedHours(Long id, LocalDate date);
}
