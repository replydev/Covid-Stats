import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public class Bot extends TelegramLongPollingBot {

    private static Bot instance;
    private final CommandHandler commandHandler;
    private Config config;
    public static Bot getInstance(){
        return instance;
    }

    public Bot(){
        instance = this;
        commandHandler = new CommandHandler(50);
        try {
            config = Config.load("config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {
        if(update.getMessage().isCommand())
            commandHandler.handle(update.getMessage().getText(),update.getMessage().getChatId());
    }

    public String getBotUsername() {
        return config.BOT_USERNAME;
    }

    public String getBotToken() {
        return config.BOT_TOKEN;
    }
}
