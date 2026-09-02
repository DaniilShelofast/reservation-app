package bot.reservation.service;

import bot.reservation.dto.user.TelegramLinkDto;
import bot.reservation.dto.user.UserCreateDto;
import bot.reservation.dto.user.UserDto;
import bot.reservation.exception.RegistrationException;

public interface AuthorizationService {
    UserDto register(UserCreateDto userCreateDto) throws RegistrationException;

    UserDto linkTelegram(TelegramLinkDto telegramLinkDto);
}
