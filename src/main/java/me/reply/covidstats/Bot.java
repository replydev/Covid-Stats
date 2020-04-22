package me.reply.covidstats;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public class Bot extends TelegramLongPollingBot {

    private static Bot instance;
    private final CommandHandler commandHandler;
    private static Config config;
    public static Bot getInstance(){
        return instance;
    }
    public static Config getConfig(){
        return config;
    }

    public Bot(){
        try {
            config = Config.load("config.yml");
            config.loadAdminsFromFile("admins.list");
        } catch (IOException e) {
            e.printStackTrace();
        }
        instance = this;
        commandHandler = new CommandHandler(50,config);
    }

    public void onUpdateReceived(Update update) {
        if(update.getMessage().isCommand())
            commandHandler.handle(update.getMessage().getText(),update.getMessage().getChatId(),update.getMessage().getFrom().getId().toString());
    }
    //heroku support
    public String getBotUsername() {
        return config.BOT_USERNAME.equals("username_here") ? System.getenv("USERNAME") : config.BOT_USERNAME;
    }
    public String getBotToken() {
        return config.BOT_TOKEN.equals("token_here") ? System.getenv("TOKEN") : config.BOT_TOKEN;
    }
}
