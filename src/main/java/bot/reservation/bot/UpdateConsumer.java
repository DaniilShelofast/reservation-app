package bot.reservation.bot;

import bot.reservation.dto.booking.BookingDto;
import bot.reservation.service.BookingService;
import lombok.SneakyThrows;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;


@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final BookingService bookingService;

    public UpdateConsumer(BookingService bookingService) {
        this.bookingService = bookingService;
        this.telegramClient =
                new OkHttpTelegramClient(
                        "8215840726:AAH-mgDv1z1IrPnCxsRZu52v7qogF-tpE4M"
                );
    }

    @SneakyThrows
    @Override
    public void consume(Update update) {
        if (update.hasMessage()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                sendMainManu(chatId);
            } else {
                sendMessage(chatId, "я вас не розумію");
            }
        } else if (update.hasCallbackQuery()) {
            handlerCallbackQuery(update.getCallbackQuery());
        }
    }

    private void handlerCallbackQuery(CallbackQuery callbackQuery) {
        var data = callbackQuery.getData();
        // Безпечніше брати chatId з повідомлення, пов'язаного з callback
        var chatId = callbackQuery.getMessage().getChatId();

        switch (data) {
            case "delete" -> {
                // Приклад: для видалення потрібен ID. На практиці в callbackData передають наприклад "delete_5"
                // Long bookingId = 5L;
                // bookingService.deleteBookingById(bookingId);
                sendMessage(chatId, "Введіть ID бронювання, яке хочете видалити:");
            }
            case "create" -> {
                // Приклад: створення потребує об'єкта CreateBooking з даними від користувача
                // CreateBooking createBooking = new CreateBooking(...);
                // BookingDto createdBooking = bookingService.addBooking(createBooking);
                sendMessage(chatId, "Будь ласка, вкажіть параметри для створення бронювання.");
            }
            case "get_all" -> {
                List<BookingDto> bookings = bookingService.getMyBooking();

                if (bookings == null || bookings.isEmpty()) {
                    sendMessage(chatId, "У вас немає активних бронювань.");
                } else {
                    StringBuilder responseMessage = new StringBuilder("Ваші бронювання:\n\n");
                    for (BookingDto booking : bookings) {
                        // Заміни .getId() та інші поля на ті, що є у твоєму BookingDto
                        responseMessage.append("ID: ").append(booking.getId()).append("\n");
                        // responseMessage.append("Кімната: ").append(booking.getRoomName()).append("\n\n");
                    }
                    sendMessage(chatId, responseMessage.toString());
                }
            }
            default -> sendMessage(chatId, "Не зрозуміла команда");
        }
    }

    @SneakyThrows
    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder().text(message).chatId(chatId).build();
        telegramClient.execute(sendMessage);
    }

    @SneakyThrows
    private void sendMainManu(Long chatId) {
        SendMessage sendMessage = SendMessage.builder().text("виберіть дію: ").chatId(chatId).build();

        var button1 = InlineKeyboardButton.builder().text("видалити бронювання").callbackData("delete").build();
        var button2 = InlineKeyboardButton.builder().text("забронювати кімнату").callbackData("create").build();
        var button3 = InlineKeyboardButton.builder().text("переглянути свої бронювання").callbackData("get_all").build();

        List<InlineKeyboardRow> keyboardRows = List.of(new InlineKeyboardRow(button1), new InlineKeyboardRow(button2), new InlineKeyboardRow(button3));
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(keyboardRows);
        sendMessage.setReplyMarkup(keyboardMarkup);
        telegramClient.execute(sendMessage);
    }
}
