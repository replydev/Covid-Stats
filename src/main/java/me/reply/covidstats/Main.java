package me.reply.covidstats;

import me.reply.covidstats.data.ChartUtils;
import me.reply.covidstats.data.DataFetcher;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.File;
import java.io.IOException;

public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            FileUtils.forceMkdir(new File("data/"));
            FileUtils.forceMkdir(new File("config/"));
            FileUtils.forceMkdir(new File(ChartUtils.CHARTS_FOLDER));
            logger.info("Scarico i file contenenti i dati...");
            DataFetcher.downloadFiles();
            logger.info("Download completato");
        } catch (IOException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
    }
}
