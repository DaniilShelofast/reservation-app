package bot.reservation.bot;

import bot.reservation.calendar.CalendarKeyboardBuilder;
import bot.reservation.exception.BookingConflictException;
import bot.reservation.model.Room;
import bot.reservation.model.SelectionState;
import bot.reservation.repository.RoomRepository;
import bot.reservation.service.BookingService;
import bot.reservation.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class ReservationBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private static final Logger log = LoggerFactory.getLogger(ReservationBot.class);
    private static final DateTimeFormatter DATE_DISPLAY = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final String botToken;
    private final int maxMonthsAhead;
    private final TelegramClient telegramClient;
    private final RoomRepository roomRepository;
    private final BookingService bookingService;
    private final SessionService sessionService;
    private final CalendarKeyboardBuilder keyboardBuilder;

    public ReservationBot(
            BotProperties botProperties,
            RoomRepository roomRepository,
            BookingService bookingService,
            SessionService sessionService,
            CalendarKeyboardBuilder keyboardBuilder
    ) {
        this.botToken = botProperties.getToken();
        this.maxMonthsAhead = botProperties.getMaxMonthsAhead();
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.roomRepository = roomRepository;
        this.bookingService = bookingService;
        this.sessionService = sessionService;
        this.keyboardBuilder = keyboardBuilder;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                handleCallback(update.getCallbackQuery());
            }
        } catch (TelegramApiException ex) {
            log.error("Помилка звернення до Telegram API", ex);
        }
    }

    private void handleMessage(Message message) throws TelegramApiException {
        String text = message.getText().trim();
        Long chatId = message.getChatId();

        if ("/start".equals(text) || "/book".equals(text) || "/бронювати".equals(text)) {
            sessionService.clear(chatId);
            startBookingFlow(chatId);
        } else {
            send(chatId, "Щоб забронювати приміщення, надішліть команду /book");
        }
    }

    private void startBookingFlow(Long chatId) throws TelegramApiException {
        List<Room> rooms = roomRepository.findAll();
        if (rooms.isEmpty()) {
            send(chatId, "Наразі немає жодного доступного приміщення. Зверніться до адміністратора.");
            return;
        }
        if (rooms.size() == 1) {
            Room room = rooms.get(0);
            SelectionState state = sessionService.getOrCreate(chatId);
            state.setRoomId(room.getId());
            state.setShownMonth(YearMonth.now());
            sendCalendar(chatId, null, room, YearMonth.now());
            return;
        }
        // кілька приміщень - показуємо вибір
        var rows = rooms.stream()
                .map(r -> List.of(org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton.builder()
                        .text(r.getName())
                        .callbackData(CallbackData.room(r.getId()))
                        .build()))
                .map(org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow::new)
                .collect(Collectors.toList());
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text("Оберіть приміщення:")
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(rows).build())
                .build();
        telegramClient.execute(msg);
    }

    private void handleCallback(CallbackQuery query) throws TelegramApiException {
        Long chatId = query.getMessage().getChatId();
        Integer messageId = query.getMessage().getMessageId();
        CallbackData.Parsed parsed = CallbackData.parse(query.getData());

        telegramClient.execute(AnswerCallbackQuery.builder().callbackQueryId(query.getId()).build());

        if (parsed == null) {
            return;
        }
        SelectionState state = sessionService.getOrCreate(chatId);

        switch (parsed.type) {
            case ROOM -> {
                state.setRoomId(parsed.roomId);
                state.setShownMonth(YearMonth.now());
                Room room = requireRoom(parsed.roomId);
                editToCalendar(chatId, messageId, room, YearMonth.now());
            }
            case CALENDAR, BACK -> {
                state.setShownMonth(parsed.month);
                state.clearHours();
                Room room = requireRoom(parsed.roomId);
                editToCalendar(chatId, messageId, room, parsed.month);
            }
            case DAY -> {
                state.setSelectedDate(parsed.date);
                state.clearHours();
                Room room = requireRoom(parsed.roomId);
                editToHours(chatId, messageId, room, parsed.date, state);
            }
            case HOUR -> {
                state.toggleHour(parsed.hour);
                Room room = requireRoom(parsed.roomId);
                editToHours(chatId, messageId, room, parsed.date, state);
            }
            case CONFIRM -> confirmBooking(chatId, messageId, parsed.roomId, parsed.date, state, query);
            default -> { }
        }
    }

    private void confirmBooking(
            Long chatId,
            Integer messageId,
            long roomId,
            LocalDate date,
            SelectionState state,
            CallbackQuery query
    ) throws TelegramApiException {
        Set<Integer> hours = state.getSelectedHours();
        if (hours.isEmpty()) {
            return;
        }
        Long userId = query.getFrom().getId();
        String userName = displayName(query);

        try {
            bookingService.book(roomId, date, hours, userId, userName);
            String hoursText = hours.stream().sorted()
                    .map(h -> String.format("%02d:00", h))
                    .collect(Collectors.joining(", "));
            editText(chatId, messageId,
                    "✅ Бронювання підтверджено!\n" +
                            "Дата: " + DATE_DISPLAY.format(date) + "\n" +
                            "Години: " + hoursText);
            sessionService.clear(chatId);
        } catch (BookingConflictException ex) {
            String conflicts = ex.getConflictingHours().stream().sorted()
                    .map(h -> String.format("%02d:00", h))
                    .collect(Collectors.joining(", "));
            state.clearHours();
            Room room = requireRoom(roomId);
            editText(chatId, messageId,
                    "⚠️ На жаль, ці години щойно зайняли: " + conflicts + ". Оберіть інші.");
            editToHours(chatId, messageId, room, date, state);
        }
    }

    private void sendCalendar(Long chatId, Integer editMessageId, Room room, YearMonth month) throws TelegramApiException {
        InlineKeyboardMarkup markup = keyboardBuilder.buildMonthCalendar(room.getId(), month, maxMonthsAhead);
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text("Оберіть дату для \"" + room.getName() + "\":")
                .replyMarkup(markup)
                .build();
        telegramClient.execute(msg);
    }

    private void editToCalendar(Long chatId, Integer messageId, Room room, YearMonth month) throws TelegramApiException {
        InlineKeyboardMarkup markup = keyboardBuilder.buildMonthCalendar(room.getId(), month, maxMonthsAhead);
        EditMessageText edit = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text("Оберіть дату для \"" + room.getName() + "\":")
                .replyMarkup(markup)
                .build();
        telegramClient.execute(edit);
    }

    private void editToHours(Long chatId, Integer messageId, Room room, LocalDate date, SelectionState state) throws TelegramApiException {
        Set<Integer> booked = bookingService.getBookedHours(room.getId(), date);
        InlineKeyboardMarkup markup = keyboardBuilder.buildHoursKeyboard(
                room.getId(), date, room.getWorkingHourStart(), room.getWorkingHourEnd(), booked, state.getSelectedHours());
        EditMessageText edit = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text("Приміщення: " + room.getName() + "\nДата: " + DATE_DISPLAY.format(date) + "\nОберіть одну або кілька годин:")
                .replyMarkup(markup)
                .build();
        telegramClient.execute(edit);
    }

    private void editText(Long chatId, Integer messageId, String text) throws TelegramApiException {
        EditMessageText edit = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text)
                .build();
        telegramClient.execute(edit);
    }

    private void send(Long chatId, String text) throws TelegramApiException {
        telegramClient.execute(SendMessage.builder().chatId(chatId).text(text).build());
    }

    private Room requireRoom(long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalStateException("Приміщення не знайдено: " + roomId));
    }

    private String displayName(CallbackQuery query) {
        var user = query.getFrom();
        String name = user.getFirstName() == null ? "" : user.getFirstName();
        if (user.getLastName() != null) {
            name += " " + user.getLastName();
        }
        if (name.isBlank() && user.getUserName() != null) {
            name = "@" + user.getUserName();
        }
        return name.isBlank() ? String.valueOf(user.getId()) : name;
    }
}
