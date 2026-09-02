package bot.reservation.repository;

import bot.reservation.model.Booking;
import bot.reservation.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b "
            + "where b.room.id = :roomId "
            + "and b.status <> :cancelled "
            + "and b.startTime < :dayEnd and b.endTime > :dayStart")
    List<Booking> findActiveForRoomOnDay(
            @Param("roomId") Long roomId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd,
            @Param("cancelled") BookingStatus cancelled
    );

    /**
     * true, якщо є хоч одне активне бронювання, що перетинається з [start, end).
     * Використовується як фінальна атомарна перевірка перед збереженням.
     */
    @Query("select case when count(b) > 0 then true else false end from Booking b "
            + "where b.room.id = :roomId "
            + "and b.status <> :cancelled "
            + "and b.startTime < :end and b.endTime > :start")
    boolean existsOverlapping(
            @Param("roomId") Long roomId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("cancelled") BookingStatus cancelled
    );

    Set<Integer> findByRoomIdAndDateRange(Long roomId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
