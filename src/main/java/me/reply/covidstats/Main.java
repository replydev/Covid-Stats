package me.reply.covidstats;

import me.reply.covidstats.data.ChartUtils;
import me.reply.covidstats.data.DataFetcher;
import me.reply.covidstats.utils.SIGINT_Thread;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.File;
import java.io.IOException;

public class Main {
    private static Logger logger;

    public static void main(String[] args) {
        logger = LoggerFactory.getLogger(Main.class);
        logger.info("---------------NEW RUN----------------");
        try {
            initialize(strInArray("-download",args));
        } catch (IOException e) {
            System.err.println("Si è verificato un errore di inizializzazione, verifica nel file di log");
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

    private static void initialize(boolean download) throws IOException {
        logger.info("Genero le cartelle");
        FileUtils.forceMkdir(new File("data/"));
        FileUtils.forceMkdir(new File("config/"));
        ChartUtils.clearCache();
        if(download){
            logger.info("Scarico i file contenenti i dati...");
            DataFetcher.downloadFiles();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new SIGINT_Thread()));
    }

    private static boolean strInArray(String s, String[] array){
        if(s.equalsIgnoreCase("-download") && System.getenv("DOWNLOAD_DATA") != null){
            return true;
        }
        for(String temp : array){
            if (temp.equalsIgnoreCase(s)){
                return true;
            }
        }
        return false;
    }
}
