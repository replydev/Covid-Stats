import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Bot extends TelegramLongPollingBot {

    private static Bot instance;
    private final CommandHandler commandHandler;
    public static Bot getInstance(){
        return instance;
    }

    public Bot(){
        instance = this;
        commandHandler = new CommandHandler(50);
    }

    public void onUpdateReceived(Update update) {
        if(update.getMessage().isCommand())
            commandHandler.handle(update.getMessage().getText(),update.getMessage().getChatId());
    }

    public String getBotUsername() {
        return "username_here";
    }

    public String getBotToken() {
        return "token_here";
    }
}
