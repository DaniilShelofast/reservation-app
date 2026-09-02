package bot.reservation.calendar;

import bot.reservation.bot.CallbackData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class CalendarKeyboardBuilder {

    private static final String[] MONTH_NAMES = {
            "Січень", "Лютий", "Березень", "Квітень", "Травень", "Червень",
            "Липень", "Серпень", "Вересень", "Жовтень", "Листопад", "Грудень"
    };
    private static final String[] WEEKDAY_SHORT = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд"};

    public InlineKeyboardMarkup buildMonthCalendar(long roomId, YearMonth month, int maxMonthsAhead) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();

        rows.add(headerRow(roomId, month, currentMonth, maxMonthsAhead));
        rows.add(weekdayHeaderRow());
        rows.addAll(dayRows(roomId, month));

        InlineKeyboardRow cancelRow = new InlineKeyboardRow();
        cancelRow.add(InlineKeyboardButton.builder().text("Скасувати").callbackData(CallbackData.NOOP).build());
        rows.add(cancelRow);

        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    private InlineKeyboardRow headerRow(long roomId, YearMonth month, YearMonth currentMonth, int maxMonthsAhead) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        boolean canGoBack = month.isAfter(currentMonth);
        YearMonth maxMonth = currentMonth.plusMonths(maxMonthsAhead);
        boolean canGoForward = month.isBefore(maxMonth);

        row.add(InlineKeyboardButton.builder()
                .text(canGoBack ? "‹" : " ")
                .callbackData(canGoBack ? CallbackData.calendar(roomId, month.minusMonths(1)) : CallbackData.NOOP)
                .build());
        row.add(InlineKeyboardButton.builder()
                .text(MONTH_NAMES[month.getMonthValue() - 1] + " " + month.getYear())
                .callbackData(CallbackData.NOOP)
                .build());
        row.add(InlineKeyboardButton.builder()
                .text(canGoForward ? "›" : " ")
                .callbackData(canGoForward ? CallbackData.calendar(roomId, month.plusMonths(1)) : CallbackData.NOOP)
                .build());
        return row;
    }

    private InlineKeyboardRow weekdayHeaderRow() {
        InlineKeyboardRow row = new InlineKeyboardRow();
        for (String day : WEEKDAY_SHORT) {
            row.add(InlineKeyboardButton.builder().text(day).callbackData(CallbackData.NOOP).build());
        }
        return row;
    }

    private List<InlineKeyboardRow> dayRows(long roomId, YearMonth month) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        LocalDate first = month.atDay(1);
        LocalDate today = LocalDate.now();
        int leadingBlanks = first.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();

        InlineKeyboardRow row = new InlineKeyboardRow();
        for (int i = 0; i < leadingBlanks; i++) {
            row.add(blankButton());
        }

        for (int day = 1; day <= month.lengthOfMonth(); day++) {
            LocalDate date = month.atDay(day);
            boolean selectable = !date.isBefore(today);
            row.add(InlineKeyboardButton.builder()
                    .text(String.valueOf(day))
                    .callbackData(selectable ? CallbackData.day(roomId, date) : CallbackData.NOOP)
                    .build());
            if (row.size() == 7) {
                rows.add(row);
                row = new InlineKeyboardRow();
            }
        }
        if (row.size() > 0) {
            while (row.size() < 7) {
                row.add(blankButton());
            }
            rows.add(row);
        }
        return rows;
    }

    private InlineKeyboardButton blankButton() {
        return InlineKeyboardButton.builder().text(" ").callbackData(CallbackData.NOOP).build();
    }

    public InlineKeyboardMarkup buildHoursKeyboard(
            long roomId,
            LocalDate date,
            int workingHourStart,
            int workingHourEnd,
            Set<Integer> bookedHours,
            Set<Integer> selectedHours
    ) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        InlineKeyboardRow row = new InlineKeyboardRow();

        for (int hour = workingHourStart; hour < workingHourEnd; hour++) {
            boolean booked = bookedHours.contains(hour);
            boolean selected = selectedHours.contains(hour);
            String label = String.format("%02d:00", hour);
            if (booked) {
                label = "🚫 " + label;
            } else if (selected) {
                label = "✅ " + label;
            }
            row.add(InlineKeyboardButton.builder()
                    .text(label)
                    .callbackData(booked ? CallbackData.NOOP : CallbackData.hour(roomId, date, hour))
                    .build());
            if (row.size() == 4) {
                rows.add(row);
                row = new InlineKeyboardRow();
            }
        }
        if (row.size() > 0) {
            rows.add(row);
        }

        InlineKeyboardRow actionRow = new InlineKeyboardRow();
        actionRow.add(InlineKeyboardButton.builder()
                .text("‹ Назад до календаря")
                .callbackData(CallbackData.backToCalendar(roomId, YearMonth.from(date)))
                .build());
        if (!selectedHours.isEmpty()) {
            actionRow.add(InlineKeyboardButton.builder()
                    .text("Підтвердити (" + selectedHours.size() + ")")
                    .callbackData(CallbackData.confirm(roomId, date))
                    .build());
        }
        rows.add(actionRow);

        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }
}
