package bot.reservation.repository;

import bot.reservation.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPhone(String phone);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByPhone(String phone);

    Optional<User> findByTelegramId(Long telegramId);

    List<User> findByTelegramIdIsNull();
}
