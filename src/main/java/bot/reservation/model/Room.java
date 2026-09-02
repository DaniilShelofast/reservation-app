package bot.reservation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@Table(name = "rooms")
@SQLDelete(sql = "UPDATE rooms SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;
    private Integer capacity;
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isDeleted = false;

    public int getWorkingHourStart() {
        return 0;
    }

    public int getWorkingHourEnd() {
        return 0;
    }
}
