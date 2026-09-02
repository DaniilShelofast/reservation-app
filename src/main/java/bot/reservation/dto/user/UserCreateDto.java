package bot.reservation.dto.user;

import bot.reservation.model.RoleName;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserCreateDto {
    @Column(nullable = false, unique = true)
    private Long telegramId;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    private String username;
    @Column(nullable = false, unique = true)
    private String phone;
    private String roleName;
}
