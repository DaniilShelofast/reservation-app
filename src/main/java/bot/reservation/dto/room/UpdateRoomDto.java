package bot.reservation.dto.room;

import lombok.Data;

@Data
public class UpdateRoomDto {
    private String name;
    private String description;
    private Integer capacity;
    private boolean isActive;
}
