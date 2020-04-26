package me.reply.covidstats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;


public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            logger.info("Downloading files...");
            DataFetcher.downloadFiles();
            logger.info(" ...done");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
