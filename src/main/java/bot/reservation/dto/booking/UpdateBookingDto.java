package bot.reservation.dto.booking;

import jakarta.persistence.Column;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UpdateBookingDto {
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;
}
