package bot.reservation.service;

import bot.reservation.dto.room.CreateRoomDto;
import bot.reservation.dto.room.RoomDto;
import bot.reservation.dto.room.UpdateRoomDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RoomService {
    RoomDto addRoom(CreateRoomDto createRoomDto);

    RoomDto updateRoomById(Long roomId, UpdateRoomDto updateRoomDto);

    void deleteByRoomId(Long roomId);

    Page<RoomDto> getAllRooms(Pageable pageable);

    Page<RoomDto> getAvailableRooms(Pageable pageable);
}
