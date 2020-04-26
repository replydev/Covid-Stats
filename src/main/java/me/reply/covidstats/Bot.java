package me.reply.covidstats;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot extends TelegramLongPollingBot {

    private static Bot instance;
    private final CommandHandler commandHandler;
    private Config config;

    private HashMap<String,String> users;  //userid,region

    public boolean isInUserList(String userid){
        return users.containsKey(userid);
    }

    public String getRegionFromUser(String userid){
        return users.get(userid);
    }

    private final static List<String> regions = Arrays.asList("Abruzzo","Basilicata","P.A Bolzano","Calabria","Campania","Emilia-Romagna","Friuli Venezia Giulia","Lazio","Liguria","Lombardia","Marche","Molise","Piemonte","Puglia","Sardegna","Sicilia","Toscana","P.A Trento","Umbria","Valle d'Aosta","Veneto");

    public boolean setRegion(String userid,String region){
        if(!regions.contains(region)){
            return false;
        }
        for(String user : users.keySet()){
            if(user.equals(userid)){
                users.put(user,region); //overwrite the user if he already exists
                return true;
            }
        }
        return true;
    }

    public String getRegions(){
        StringBuilder builder = new StringBuilder();
        int n = 1;
        for(String s : regions){
            builder.append(n).append(": ").append(s).append("\n");
            n++;
        }
        return builder.toString();
    }

    public static Bot getInstance(){
        return instance;
    }

    public Config getConfig(){
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
        commandHandler = new CommandHandler(50);
        users = new HashMap<>();
        startDailyUpdateTask();
    }

    public void onUpdateReceived(Update update) {
        String userid = update.getMessage().getFrom().getId().toString();
        if(!isInUserList(userid)){
            System.out.println("Adding new user to memory");
            users.put(userid,null);
        }
        if(update.getMessage().isCommand())
            commandHandler.handle(update.getMessage().getText(),update.getMessage().getChatId(),userid);
    }

    //heroku support
    public String getBotUsername() {
        return config.BOT_USERNAME.equals("username_here") ? System.getenv("USERNAME") : config.BOT_USERNAME;
    }
    public String getBotToken() {
        return config.BOT_TOKEN.equals("token_here") ? System.getenv("TOKEN") : config.BOT_TOKEN;
    }


    private void startDailyUpdateTask(){
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
        ZonedDateTime nextRun = now.withHour(18).withMinute(10).withSecond(0);  //at 18:10 it will update data
        if(now.compareTo(nextRun) > 0)
            nextRun = nextRun.plusDays(1);

        Duration duration = Duration.between(now, nextRun);
        long initalDelay = duration.getSeconds();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.print("Scheduler service is updating data...");
                DataFetcher.downloadFiles();
                System.out.println(" ...done");
            } catch (IOException e) {
                e.printStackTrace();
            }
                },
                initalDelay,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS);
    }
}
