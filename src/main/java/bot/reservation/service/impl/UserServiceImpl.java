package bot.reservation.service.impl;

import bot.reservation.dto.user.*;
import bot.reservation.exception.EntityNotFoundException;
import bot.reservation.mapper.UserMapper;
import bot.reservation.model.User;
import bot.reservation.repository.UserRepository;
import bot.reservation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't user by id: " + userId));
        return userMapper.toDto(user);
    }

    @Override
    public UserDto updateUserById(Long userId, UpdateUserDto updateUserDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't user by id: " + userId));
        userMapper.updateUser(user, updateUserDto);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto updateTelegramId(Long userId, UpdateTelegramId updateTelegramId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't user by id: " + userId));
        userMapper.updateTelegramId(user, updateTelegramId);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public Page<UserDto> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    @Override
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't user by id" + userId));
        userRepository.deleteById(user.getId());
    }
}
