package bot.reservation.mapper;

import bot.reservation.config.MapperConfig;
import bot.reservation.dto.room.CreateRoomDto;
import bot.reservation.dto.room.RoomDto;
import bot.reservation.dto.room.UpdateRoomDto;
import bot.reservation.model.Room;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface RoomMapper {
    RoomDto toDto(Room room);

    Room toEntity(CreateRoomDto createRoomDto);

    void updateRoom(@MappingTarget Room room, UpdateRoomDto updateRoomDto);
}
