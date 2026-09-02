package bot.reservation.exception;

import lombok.Getter;
import java.util.List;

@Getter
public class BookingConflictException extends RuntimeException {
    private final List<Integer> conflictingHours;

    public BookingConflictException(List<Integer> conflictingHours) {
        super("Години вже заброньовані: " + conflictingHours);
        this.conflictingHours = conflictingHours;
    }
}
