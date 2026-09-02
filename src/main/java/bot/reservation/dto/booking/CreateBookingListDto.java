package bot.reservation.dto.booking;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class CreateBookingListDto {
    private Long roomId;
    private LocalDate date;
    private Set<Integer> hours;
    private Long telegramUserId;
    private String userName;
}
