package bot.reservation.dto.user;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private Long telegramId;
    private String firstName;
    private String lastName;
    private String username;
    private String phone;
}
