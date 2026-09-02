package bot.reservation.dto.booking;

import jakarta.persistence.Column;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateBooking {
    @Column(nullable = false)
    private Long roomId;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;
}
