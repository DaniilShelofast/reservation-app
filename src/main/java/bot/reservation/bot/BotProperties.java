package bot.reservation.bot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "bot")
public class BotProperties {
    @Value(value = "${bot.token}")
    private String token;

    private int maxMonthsAhead = 2;
}
