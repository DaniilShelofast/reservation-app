package bot.reservation.config;

import bot.reservation.bot.BotProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BotProperties.class)
public class BotConfig {
}
