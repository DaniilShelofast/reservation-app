package bot.reservation.dto.user;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UpdateUserDto {
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    private String username;
    @Column(nullable = false, unique = true)
    private String phone;
}
