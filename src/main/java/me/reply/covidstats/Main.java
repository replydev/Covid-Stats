package me.reply.covidstats;

import com.google.gson.Gson;
import me.reply.covidstats.data.ChartUtils;
import me.reply.covidstats.data.DataFetcher;
import me.reply.covidstats.utils.Config;
import me.reply.covidstats.utils.SIGINT_Thread;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.IOException;

public class Main {
    private static Logger logger;
    private static Config config;
    private static final UsersManager usersManager = new UsersManager();

    public static void main(String[] args) {
        logger = LoggerFactory.getLogger(Main.class);
        logger.info(intro());
        try {
            initialize(strInArray("-download",args));
        } catch (IOException e) {
            System.err.println("Si è verificato un errore di inizializzazione, verifica nel file di log");
            logger.error(e.toString());
        }
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new Bot(usersManager,config));
        } catch (TelegramApiException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
    }

    private static boolean noData(File f){
        if(f == null)
            return true;
        if(!f.exists())
            return true;
        if(!f.isDirectory())
            return true;
        File[] files = f.listFiles();
        if(files == null)
            return true;
        return files.length != 3;
    }

    private static void initialize(boolean download) throws IOException {
        logger.info("Genero le cartelle...");
        File dataFolder = new File("data/");
        boolean noData = noData(dataFolder);
        FileUtils.forceMkdir(dataFolder);
        FileUtils.forceMkdir(new File("config/"));
        ChartUtils.clearCache();
        if(download || noData){
            logger.info("Scarico i dati e aggiorno la memoria...");
            DataFetcher.downloadFiles();
        }
        else{
            logger.info("Aggiorno la memoria con dati prescaricati...");
            DataFetcher.loadData();
        }
        try {
            Gson g = new Gson();
            logger.info("Carico i file di configurazione...");
            config = Config.load("config/config.json");
            config.loadAdminsFromFile("config/admins.list");
            File backupFile = new File("config/users_backup.json");
            if(backupFile.exists()){
                User[] temp = g.fromJson(FileUtils.readFileToString(backupFile,"UTF-8"),User[].class);
                usersManager.addAll(temp);
                logger.info("Caricamento completato!");
            }
        } catch (IOException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
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

    private static String intro(){
        return " ██████  ██████  ██    ██ ██ ██████        ███████ ████████  █████  ████████ ███████ \n" +
                "██      ██    ██ ██    ██ ██ ██   ██       ██         ██    ██   ██    ██    ██      \n" +
                "██      ██    ██ ██    ██ ██ ██   ██ █████ ███████    ██    ███████    ██    ███████ \n" +
                "██      ██    ██  ██  ██  ██ ██   ██            ██    ██    ██   ██    ██         ██ \n" +
                " ██████  ██████    ████   ██ ██████        ███████    ██    ██   ██    ██    ███████";
    }
}
