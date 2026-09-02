package bot.reservation.controller;

import bot.reservation.dto.user.UserCreateDto;
import bot.reservation.dto.user.UserDto;
import bot.reservation.exception.RegistrationException;
import bot.reservation.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    private final UserService userService;

    /*@PostMapping("/registration")
    public UserDto register(@RequestBody @Valid UserCreateDto userCreateDto)
            throws RegistrationException {
        return userService.register(userCreateDto);
    }*/
}
