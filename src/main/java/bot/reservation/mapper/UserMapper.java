package bot.reservation.mapper;

import bot.reservation.config.MapperConfig;
import bot.reservation.dto.user.UpdateTelegramId;
import bot.reservation.dto.user.UpdateUserDto;
import bot.reservation.dto.user.UserCreateDto;
import bot.reservation.dto.user.UserDto;
import bot.reservation.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserDto toDto(User user);

    void updateUser(@MappingTarget User user, UpdateUserDto updateUserDto);

    void updateTelegramId(@MappingTarget User user, UpdateTelegramId updateTelegramId);

    User toModel(UserCreateDto createDto);
}