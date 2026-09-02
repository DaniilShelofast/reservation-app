package bot.reservation.bot;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public final class CallbackData {

    public static final String NOOP = "x";
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private CallbackData() {
    }

    public static String room(long roomId) {
        return "r:" + roomId;
    }

    public static String calendar(long roomId, YearMonth month) {
        return "c:" + roomId + ":" + MONTH_FMT.format(month.atDay(1));
    }

    public static String day(long roomId, LocalDate date) {
        return "d:" + roomId + ":" + DATE_FMT.format(date);
    }

    public static String hour(long roomId, LocalDate date, int hour) {
        return "h:" + roomId + ":" + DATE_FMT.format(date) + ":" + hour;
    }

    public static String confirm(long roomId, LocalDate date) {
        return "ok:" + roomId + ":" + DATE_FMT.format(date);
    }

    public static String backToCalendar(long roomId, YearMonth month) {
        return "b:" + roomId + ":" + MONTH_FMT.format(month.atDay(1));
    }

    /** Розбирає callback_data на дії. Повертає null, якщо формат невідомий. */
    public static Parsed parse(String data) {
        if (data == null || data.isEmpty() || NOOP.equals(data)) {
            return null;
        }
        String[] parts = data.split(":");
        try {
            switch (parts[0]) {
                case "r":
                    return Parsed.room(Long.parseLong(parts[1]));
                case "c":
                    return Parsed.calendar(Long.parseLong(parts[1]), YearMonth.parse(parts[2]));
                case "d":
                    return Parsed.day(Long.parseLong(parts[1]), LocalDate.parse(parts[2]));
                case "h":
                    return Parsed.hour(Long.parseLong(parts[1]), LocalDate.parse(parts[2]), Integer.parseInt(parts[3]));
                case "ok":
                    return Parsed.confirm(Long.parseLong(parts[1]), LocalDate.parse(parts[2]));
                case "b":
                    return Parsed.back(Long.parseLong(parts[1]), YearMonth.parse(parts[2]));
                default:
                    return null;
            }
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public enum Type { ROOM, CALENDAR, DAY, HOUR, CONFIRM, BACK }

    public static final class Parsed {
        public final Type type;
        public final long roomId;
        public final YearMonth month;
        public final LocalDate date;
        public final int hour;

        private Parsed(Type type, long roomId, YearMonth month, LocalDate date, int hour) {
            this.type = type;
            this.roomId = roomId;
            this.month = month;
            this.date = date;
            this.hour = hour;
        }

        static Parsed room(long roomId) {
            return new Parsed(Type.ROOM, roomId, null, null, 0);
        }

        static Parsed calendar(long roomId, YearMonth month) {
            return new Parsed(Type.CALENDAR, roomId, month, null, 0);
        }

        static Parsed day(long roomId, LocalDate date) {
            return new Parsed(Type.DAY, roomId, null, date, 0);
        }

        static Parsed hour(long roomId, LocalDate date, int hour) {
            return new Parsed(Type.HOUR, roomId, null, date, hour);
        }

        static Parsed confirm(long roomId, LocalDate date) {
            return new Parsed(Type.CONFIRM, roomId, null, date, 0);
        }

        static Parsed back(long roomId, YearMonth month) {
            return new Parsed(Type.BACK, roomId, month, null, 0);
        }
    }
}
