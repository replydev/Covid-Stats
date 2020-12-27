package me.reply.covidstats;

import com.google.gson.Gson;
import com.vdurmont.emoji.EmojiParser;
import me.reply.covidstats.data.ChartUtils;
import me.reply.covidstats.data.DataFetcher;
import me.reply.covidstats.utils.Config;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot extends TelegramLongPollingBot {

    private static Bot instance;
    private final CommandHandler commandHandler;
    private Config config;
    private final UsersManager usersManager;

    private final Logger logger = LoggerFactory.getLogger(Bot.class);

    public static Bot getInstance(){
        return instance;
    }
    public Config getConfig(){
        return config;
    }
    public UsersManager getUsersManager(){
        return usersManager;
    }

    public Bot(){
        usersManager = new UsersManager();
        try {
            Gson g = new Gson();
            config = Config.load("config/config.json");
            config.loadAdminsFromFile("config/admins.list");
            File backupFile = new File("config/users_backup.json");
            if(backupFile.exists()){
                logger.info("Carico gli utenti dal backup");
                User[] temp = g.fromJson(FileUtils.readFileToString(backupFile,"UTF-8"),User[].class);
                usersManager.addAll(temp);
                logger.info("Caricamento completato");
            }
        } catch (IOException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
        instance = this;
        commandHandler = new CommandHandler(50);
        startDailyUpdateTask();
    }

    public void onUpdateReceived(Update update) {
        String userid = update.getMessage().getFrom().getId().toString();
        if(!usersManager.isInUserList(userid)){
            logger.info("Aggiungo un nuovo utente: " + userid);
            usersManager.addUser(new User(userid));
        }
        if(update.getMessage().hasText())
            commandHandler.handle(update.getMessage().getText(),update.getMessage().getChatId().toString(),userid);
    }

    public String getBotUsername() {
        return config.getBot_username();
    }
    public String getBotToken() {
        return config.getBot_token();
    }

    private void startDailyUpdateTask(){
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
        ZonedDateTime nextRun = now.withHour(config.getUpdateHour()).withMinute(config.getUpdateMinute()).withSecond(0);  //download updated json at hour written in config file
        if(now.compareTo(nextRun) > 0)
            nextRun = nextRun.plusDays(1);

        Duration duration = Duration.between(now, nextRun);
        long initialDelay = duration.getSeconds();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                logger.info("Il servizio di aggiornamento sta scaricando i nuovi dati...");
                while(!DataFetcher.updateFiles()){
                    logger.info("Non ho ancora trovato aggiornamenti, attendo 5 minuti...");
                    Thread.sleep(5 * 60 * 1000);
                }
                logger.info("Aggiornamento completato. Pulisco la cache..");
                ChartUtils.clearCache();
                messageToAllUsers("Ciao! :smile: Ho appena aggiornato i dati :chart_with_downwards_trend: relativi all'epidemia, perché non dai un'occhiata? :mag:");
            } catch (IOException | InterruptedException e) {
                System.err.println("Si è verificato un errore, verifica nel file di log");
                logger.error(e.toString());
            }
                },
                initialDelay,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS);
    }

    public void messageToAllUsers(String text){
        logger.info("Il servizio di invio notifiche sta svolgendo il suo lavoro...");
        int count = 0;
        for(int i = 0; i < usersManager.getUsers().size(); i++){
            User user = usersManager.getUsers().get(i);
            if(!user.isShowNotification())
                continue;
            SendMessage.SendMessageBuilder message = SendMessage.builder()
                    .text(EmojiParser.parseToUnicode(text))
                    .chatId(user.getUserId());
            try {
                execute(message.build());
                count++;
            } catch (TelegramApiException e) {
                if(e.toString().contains("bot was blocked by the user")){
                    logger.info(user.getUserId() + " ha bloccato il bot, lo rimuovo dalla lista utenti");
                    usersManager.setMarkedForRemove(i,true);
                }
                else{
                    System.err.println("Si è verificato un errore, verifica nel file di log");
                    logger.error(e.toString());
                }
            }
        }
        usersManager.cleanUsers();
        logger.info("Ho inviato " + count + " messaggi su " + usersManager.registeredUsers() + " utenti");
    }

    public File getLogFile(){
        return new File("log.txt");
    }
}
