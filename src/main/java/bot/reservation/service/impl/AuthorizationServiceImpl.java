package bot.reservation.service.impl;

import bot.reservation.dto.user.TelegramLinkDto;
import bot.reservation.dto.user.UserCreateDto;
import bot.reservation.dto.user.UserDto;
import bot.reservation.exception.EntityNotFoundException;
import bot.reservation.exception.RegistrationException;
import bot.reservation.mapper.UserMapper;
import bot.reservation.model.Role;
import bot.reservation.model.RoleName;
import bot.reservation.model.User;
import bot.reservation.repository.RoleRepository;
import bot.reservation.repository.UserRepository;
import bot.reservation.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class AuthorizationServiceImpl implements AuthorizationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto register(UserCreateDto userCreateDto) throws RegistrationException {
        if (userRepository.existsByPhone(userCreateDto.getPhone())) {
            throw new RegistrationException("User with this phone already exists " + userCreateDto);
        }
        User user = userMapper.toModel(userCreateDto);
        user.setTelegramId(null);
        Set<RoleName> roles = Set.of(RoleName.ROLE_ADMIN, RoleName.ROLE_USER);
        Role role = roleRepository.findByRoleName(getRole(roles))
                .orElseThrow(() -> new EntityNotFoundException(RoleName.ROLE_USER + " Not found"));
        user.setRoles(Set.of(role));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto linkTelegram(TelegramLinkDto telegramLinkDto) {
        User user = userRepository.findByPhone(telegramLinkDto.getPhone())
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by phone " + telegramLinkDto));

        Optional<User> existingUserByTg = userRepository.findByTelegramId(telegramLinkDto.getTelegramId());
        if (existingUserByTg.isPresent() && !existingUserByTg.get().getId().equals(user.getId())) {
            throw new RegistrationException("This Telegram account is already linked to another employee.");
        }

        user.setTelegramId(telegramLinkDto.getTelegramId());
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    private RoleName getRole(Set<RoleName> roles) {
        for (RoleName role : roles) {
            if (role == RoleName.ROLE_ADMIN || role == RoleName.ROLE_USER) {
                return role;
            }
        }
        throw new EntityNotFoundException("");
    }
}
