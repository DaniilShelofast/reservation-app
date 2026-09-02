package bot.reservation.service;

import bot.reservation.model.SelectionState;

public interface SessionService {
    SelectionState getOrCreate(Long chatId);

    void clear(Long chatId);
}
