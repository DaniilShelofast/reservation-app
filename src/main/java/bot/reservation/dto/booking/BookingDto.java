package bot.reservation.dto.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private Long roomId;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private LocalDateTime createdAt;
}
