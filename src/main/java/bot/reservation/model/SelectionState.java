package bot.reservation.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashSet;
import java.util.Set;

@Setter
@Getter
public class SelectionState {
    private Long roomId;
    private YearMonth shownMonth;
    private LocalDate selectedDate;
    private final Set<Integer> selectedHours = new LinkedHashSet<>();

    public void toggleHour(int hour) {
        if (!selectedHours.remove(hour)) {
            selectedHours.add(hour);
        }
    }

    public void clearHours() {
        selectedHours.clear();
    }
}
