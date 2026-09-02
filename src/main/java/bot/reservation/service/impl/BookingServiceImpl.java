package bot.reservation.service.impl;

import bot.reservation.dto.booking.*;
import bot.reservation.exception.EntityNotFoundException;
import bot.reservation.mapper.BookingMapper;
import bot.reservation.model.Booking;
import bot.reservation.model.Room;
import bot.reservation.model.User;
import bot.reservation.repository.BookingRepository;
import bot.reservation.repository.RoomRepository;
import bot.reservation.repository.UserRepository;
import bot.reservation.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto addBooking(CreateBooking createBooking) {
        User user = userRepository.findById(createBooking.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Працівник не знайдений"));

        Room room = roomRepository.findById(createBooking.getRoomId()).orElseThrow(() -> new EntityNotFoundException("кімната не знайдено"));

        Booking booking = bookingMapper.toEntity(createBooking);
        booking.setUser(user);
        booking.setRoom(room);
        booking.setStartTime(createBooking.getStartTime());
        booking.setEndTime(createBooking.getEndTime());
        bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getMyBooking() {
        return bookingRepository.findAll().stream().map(bookingMapper::toDto).toList();
    }

    @Override
    public Page<BookingDto> getAllBooking(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(bookingMapper::toDto);
    }

    @Override
    public BookingDto updateBookingById(Long bookingId, UpdateBookingDto updateBookingDto) {
        return null;
    }

    @Override
    public void deleteBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Бронювання не знайдено"));
        bookingRepository.deleteById(booking.getId());
    }

    @Override
    public Set<Integer> getBookedHours(BookingAvailabilityDto request) {
        LocalDateTime startOfDay = request.getDate().atStartOfDay();
        LocalDateTime endOfDay = request.getDate().atTime(LocalTime.MAX);

        /*return bookingRepository.findByRoomIdAndDateRange(request.getRoomId(), startOfDay, endOfDay).stream()
                .flatMap(b -> IntStream.range(b.getStartTime().getHour(), b.getEndTime().getHour()).boxed())
                .collect(Collectors.toSet());*/
        return null;
    }

    @Override
    @Transactional
    public void createBookings(CreateBookingListDto request) {
        User user = userRepository.findByTelegramId(request.getTelegramUserId())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setTelegramId(request.getTelegramUserId());
                    newUser.setUsername(request.getUserName());
                    return userRepository.save(newUser);
                });

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Кімната не знайдена"));

        for (Integer hour : request.getHours()) {
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setRoom(room);
            booking.setStartTime(request.getDate().atTime(hour, 0));
            booking.setEndTime(request.getDate().atTime(hour + 1, 0));
            bookingRepository.save(booking);
        }
    }

    @Override
    public void book(long roomId, LocalDate date, Set<Integer> hours, Long userId, String userName) {

    }

    @Override
    public Set<Integer> getBookedHours(Long id, LocalDate date) {
        return Set.of();
    }
}
