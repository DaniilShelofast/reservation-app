package bot.reservation.controller;

import bot.reservation.dto.room.CreateRoomDto;
import bot.reservation.dto.room.RoomDto;
import bot.reservation.dto.room.UpdateRoomDto;
import bot.reservation.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rooms")
public class RoomController {
    public final RoomService roomService;

    @PostMapping
    public RoomDto addRoom(@RequestBody @Valid CreateRoomDto createRoomDto) {
        return roomService.addRoom(createRoomDto);
    }

    @PutMapping("/{roomId}")
    public RoomDto updateRoomById(@PathVariable Long roomId, @RequestBody UpdateRoomDto updateRoomDto) {
        return roomService.updateRoomById(roomId, updateRoomDto);
    }

    @DeleteMapping("/{roomId}")
    public void deleteByRoomId(@PathVariable Long roomId) {
        roomService.deleteByRoomId(roomId);
    }

    @GetMapping
    public Page<RoomDto> getAllRooms(Pageable pageable) {
        return roomService.getAllRooms(pageable);
    }

    @GetMapping("/available")
    public Page<RoomDto> getAvailableRooms(Pageable pageable) {
        return roomService.getAvailableRooms(pageable);
    }
}
