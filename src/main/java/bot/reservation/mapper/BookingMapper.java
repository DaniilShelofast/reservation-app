package bot.reservation.mapper;

import bot.reservation.config.MapperConfig;
import bot.reservation.dto.booking.BookingDto;
import bot.reservation.dto.booking.CreateBooking;
import bot.reservation.dto.booking.UpdateBookingDto;
import bot.reservation.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {
    BookingDto toDto(Booking booking);

    Booking toEntity(CreateBooking createBooking);

    void updateBooking(@MappingTarget Booking booking, UpdateBookingDto updateBookingDto);
}
