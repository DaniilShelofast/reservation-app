package bot.reservation.service.impl;

import bot.reservation.model.SelectionState;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SessionServiceImpl {
    private final ConcurrentMap<Long, SelectionState> states = new ConcurrentHashMap<>();

    public SelectionState getOrCreate(Long chatId) {
        return states.computeIfAbsent(chatId, id -> new SelectionState());
    }

    public void clear(Long chatId) {
        states.remove(chatId);
    }
}
