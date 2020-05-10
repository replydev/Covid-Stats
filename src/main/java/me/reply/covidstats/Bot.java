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
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot extends TelegramLongPollingBot {

    private static Bot instance;
    private final CommandHandler commandHandler;
    private Config config;
    private final List<User> users;
    private final Logger logger = LoggerFactory.getLogger(Bot.class);

    public String getNotificationTextFromUser(String userid){
        for(User user : users){
            if(user.getUserid().equals(userid))
                return user.getNotificationText();
        }
        return null;
    }

    public void setNotificationText(String userid,String text){
        for(User user : users){
            if(user.getUserid().equals(userid))
                user.setNotificationText(text);
        }
    }

    public boolean isInUserList(String userid){
        for(User u : users){
            if(u.getUserid().equals(userid))
                return true;
        }
        return false;
    }

    public String getProvinceFromUser(String userid){
        for(User u : users){
            if(u.getUserid().equalsIgnoreCase(userid))
                return u.getProvince();
        }
        return null;
    }

    public String getRegionFromUser(String userid){
        for(User u : users){
            if(u.getUserid().equals(userid))
                return u.getRegion();
        }
        return null;
    }

    public void backupUserList(long chatId) throws IOException {
        Gson g = new Gson();
        String json = g.toJson(users);
        File f = new File("config/users_backup.json");
        FileUtils.write(f,json,"UTF-8");
        logger.info("Ho salvato le impostazioni utente, le invio all'amministratore");
        SendDocument document = new SendDocument()
                .setDocument(f)
                .setChatId(chatId);
        try {
            execute(document);
        } catch (TelegramApiException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
        FileUtils.forceDelete(f);
    }

    public void setNotification(String userid,boolean value){
        for(User u : users){
            if(u.getUserid().equals(userid))
                u.setShowNotification(value);
        }
    }

    public void setRegion(String userid,String region){
        if(region.equalsIgnoreCase("Italia"))
            region = null;
        for(User user : users){
            if(user.getUserid().equals(userid)){
                user.setRegion(region);
                user.setProvince(null);
                return;
            }
        }
    }

    public void setProvince(String userId,String province){
        if(province.equalsIgnoreCase("Nessuna provincia"))
            province = null;
        for(User user : users){
            if(user.getUserid().equals(userId)){
                user.setProvince(province);
                return;
            }
        }
    }

    public static Bot getInstance(){
        return instance;
    }
    public Config getConfig(){
        return config;
    }

    public Bot(){
        users = new Vector<>();
        try {
            Gson g = new Gson();
            config = Config.load("config/config.yml");
            config.loadAdminsFromFile("config/admins.list");
            File backupFile = new File("config/users_backup.json");
            if(backupFile.exists()){
                logger.info("Carico gli utenti dal backup");
                User[] temp = g.fromJson(FileUtils.readFileToString(backupFile,"UTF-8"),User[].class);
                users.addAll(Arrays.asList(temp));
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
        if(!isInUserList(userid)){
            logger.info("Aggiungo un nuovo utente: " + userid);
            users.add(new User(userid,true));
        }
        if(update.getMessage().hasText())
            commandHandler.handle(update.getMessage().getText(),update.getMessage().getChatId(),userid);
    }

    public String getBotUsername() {
        return config.BOT_USERNAME;
    }
    public String getBotToken() {
        return config.BOT_TOKEN;
    }

    private void startDailyUpdateTask(){
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
        ZonedDateTime nextRun = now.withHour(config.getUpdateHour()).withMinute(config.getUpdateMinute()).withSecond(0);  //download updated json at hour written in config file
        if(now.compareTo(nextRun) > 0)
            nextRun = nextRun.plusDays(1);

        Duration duration = Duration.between(now, nextRun);
        long initalDelay = duration.getSeconds();

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
                initalDelay,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS);
    }

    public void messageToAllUsers(String text){
        logger.info("Il servizio di invio notifiche sta svolgendo il suo lavoro...");
        int count = 0;
        for(User user : users){
            if(!user.isShowNotification())
                continue;
            SendMessage message = new SendMessage()
                    .setText(EmojiParser.parseToUnicode(text))
                    .setChatId(user.getUserid());
            try {
                execute(message);
                count++;
            } catch (TelegramApiException e) {
                if(e.getMessage().contains("bot was blocked by the user")){
                    logger.info(user.getUserid() + " ha bloccato il bot, lo rimuovo dalla lista utenti");
                    users.remove(user);
                }
                else{
                    System.err.println("Si è verificato un errore, verifica nel file di log");
                    logger.error(e.toString());
                }
            }
        }
        logger.info("Ho inviato " + count + " messaggi su " + users.size() + " utenti");
    }
}
