package bot.reservation.dto.room;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateRoomDto {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    @Positive
    private Integer capacity;
}
