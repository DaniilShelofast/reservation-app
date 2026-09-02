package bot.reservation.dto.booking;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class BookingAvailabilityDto {
    private Long roomId;
    private LocalDate date;
}
