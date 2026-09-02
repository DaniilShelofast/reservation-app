package bot.reservation.controller;

import bot.reservation.dto.user.UpdateTelegramId;
import bot.reservation.dto.user.UpdateUserDto;
import bot.reservation.dto.user.UserDto;
import bot.reservation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/{id}")
    public UserDto updateUserById(@PathVariable Long userId, @RequestBody UpdateUserDto updateUserDto) {
        return userService.updateUserById(userId, updateUserDto);
    }

    @PutMapping("/{id}/telegram")
    public UserDto updateTelegramId(@PathVariable Long userId, @RequestBody UpdateTelegramId updateTelegramId) {
        return userService.updateTelegramId(userId, updateTelegramId);
    }

    @GetMapping
    public Page<UserDto> findAllUsers(Pageable pageable) {
        return userService.findAllUsers(pageable);
    }
}
