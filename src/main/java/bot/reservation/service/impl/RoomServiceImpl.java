package bot.reservation.service.impl;

import bot.reservation.dto.room.CreateRoomDto;
import bot.reservation.dto.room.RoomDto;
import bot.reservation.dto.room.UpdateRoomDto;
import bot.reservation.exception.EntityNotFoundException;
import bot.reservation.mapper.RoomMapper;
import bot.reservation.model.Room;
import bot.reservation.repository.RoomRepository;
import bot.reservation.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Override
    public RoomDto addRoom(CreateRoomDto createRoomDto) {
        Room room = roomMapper.toEntity(createRoomDto);
        roomRepository.save(room);
        return roomMapper.toDto(room);
    }

    @Override
    public RoomDto updateRoomById(Long roomId, UpdateRoomDto updateRoomDto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Can't room by " + roomId));
        room.setName(updateRoomDto.getName());
        room.setDescription(updateRoomDto.getDescription());
        room.setCapacity(updateRoomDto.getCapacity());
        room.setActive(updateRoomDto.isActive());
        roomMapper.updateRoom(room, updateRoomDto);
        roomRepository.save(room);
        return roomMapper.toDto(room);
    }

    @Override
    public void deleteByRoomId(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Can't room by " + roomId));
        roomRepository.deleteById(room.getId());
    }

    @Override
    public Page<RoomDto> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable)
                .map(roomMapper::toDto);
    }

    @Override
    public Page<RoomDto> getAvailableRooms(Pageable pageable) {
        return null;
    }
}
