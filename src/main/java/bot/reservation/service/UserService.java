package bot.reservation.service;

import bot.reservation.dto.user.*;
import bot.reservation.exception.RegistrationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserDto getUser(Long userId);

    UserDto updateUserById(Long userId, UpdateUserDto updateUserDto);

    UserDto updateTelegramId(Long userId, UpdateTelegramId updateTelegramId);

    void deleteUserById(Long userId);

    Page<UserDto> findAllUsers(Pageable pageable);
}
