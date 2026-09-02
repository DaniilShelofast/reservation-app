package bot.reservation.dto.room;

import lombok.Data;

@Data
public class RoomDto {
    private Long id;
    private String name;
    private String description;
    private Integer capacity;
    private boolean isActive;
}
