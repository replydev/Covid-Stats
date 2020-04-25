package me.reply.covidstats;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

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
    private static Config config;

    private static CovidData covidData;

    public static Bot getInstance(){
        return instance;
    }

    public static Config getConfig(){
        return config;
    }

    public static CovidData getCovidData(){
        if(covidData == null) {
            try {
                covidData = DataFetcher.fetchData();
                System.out.println("Covid data loaded to memory");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return covidData;
    }

    public static void updateCovidData(CovidData data){
        covidData = data;
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
        startDailyTask();
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


    private void startDailyTask(){
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
        ZonedDateTime nextRun = now.withHour(18).withMinute(0).withSecond(0);  //at 18:00 it will update data
        if(now.compareTo(nextRun) > 0)
            nextRun = nextRun.plusDays(1);

        Duration duration = Duration.between(now, nextRun);
        long initalDelay = duration.getSeconds();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                covidData = DataFetcher.fetchData();
                System.out.println("Scheduler service is updating data...");
            } catch (IOException e) {
                e.printStackTrace();
            }
                },
                initalDelay,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS);
    }
}
