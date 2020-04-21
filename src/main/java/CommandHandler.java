import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.*;

public class CommandHandler {

    private final ExecutorService threads;
    private final ScheduledExecutorService canceller;

    private DBManager dbManager;

    public void handle(String command,long chatId){

        Vector<String> commandSplitted = new Vector<>(Arrays.asList(command.split(" ")));
        String commandName = commandSplitted.firstElement();
        Vector<String> args = new Vector<>(commandSplitted);
        args.remove(0);

        switch(commandName){
            case "/start":
                threads.submit(() -> sendMessage("Welcome to CovidBot",chatId));
                break;
            case "/add":
                Future<?> addFuture = threads.submit(() -> {
                    if(args.size() != 5){
                        sendMessage("Please provide correct args",chatId);
                        return;
                    }
                    DayData dayData = new DayData(
                            Integer.parseInt(args.get(0)),
                            Integer.parseInt(args.get(1)),
                            Integer.parseInt(args.get(2)),
                            Integer.parseInt(args.get(3)),
                            invertDate(args.get(4))
                    );
                    try {
                        dbManager.addData(dayData);
                        sendMessage("Operation completed successfully",chatId);
                    } catch (SQLException throwable) {
                        System.out.println(throwable.getSQLState());
                        throwable.printStackTrace();
                    }
                });
                canceller.schedule(() -> {
                    if(!addFuture.isDone())
                        addFuture.cancel(true);
                    System.out.println("A task as been terminated due to timeout");
                },60, TimeUnit.SECONDS);
                break;
            case "/infected":
                Future<?> f = threads.submit(() -> infectedJob(chatId));
                canceller.schedule(() -> {
                    if(!f.isDone())
                        f.cancel(true);
                    System.out.println("A task as been terminated due to timeout");
                },60, TimeUnit.SECONDS);
                break;
            case "/recovered":
                Future<?> f1 = threads.submit(() -> recoveredJob(chatId));
                canceller.schedule(() -> {
                    if(!f1.isDone())
                        f1.cancel(true);
                    System.out.println("A task as been terminated due to timeout");
                },60, TimeUnit.SECONDS);
                break;
            case "/deaths":
                Future<?> f2 = threads.submit(() -> deathsJob(chatId));
                canceller.schedule(() -> {
                    if(!f2.isDone())
                        f2.cancel(true);
                    System.out.println("A task as been terminated due to timeout");
                },60, TimeUnit.SECONDS);
                break;
            case "/cases":
                Future<?> f3 = threads.submit(() -> casesJob(chatId));
                canceller.schedule(() -> {
                    if(!f3.isDone())
                        f3.cancel(true);
                    System.out.println("A task as been terminated due to timeout");
                },60, TimeUnit.SECONDS);
                break;
            case "/tampons":
                Future<?> f4 = threads.submit(() -> {
                    tamponsJob(chatId);
                    sendMessage("Backup completed",chatId);
                });
                canceller.schedule(() -> {
                    if(!f4.isDone())
                        f4.cancel(true);
                    System.out.println("A task as been terminated due to timeout");
                },60, TimeUnit.SECONDS);
                break;
            case "/backup":
                Future<?> f5 = threads.submit(() -> {
                    try {
                        backupJob();
                        sendMessage("Backup completed",chatId);
                    } catch (IOException e) {
                        e.printStackTrace();
                        sendMessage("An error occurred: " + e.getMessage(),chatId);
                    }
                });
                canceller.schedule(() -> {
                    if(!f5.isDone())
                        f5.cancel(true);
                    System.out.println("A task as been terminated due to timeout");
                },60, TimeUnit.SECONDS);
                break;
            default:
        }
    }

    public CommandHandler(int threadsNum){
        threads = Executors.newFixedThreadPool(threadsNum);
        canceller = Executors.newSingleThreadScheduledExecutor();
        try {
            dbManager = new DBManager("database.db");
        } catch (SQLException | ClassNotFoundException throwable) {
            throwable.printStackTrace();
        }
    }

    private void sendMessage(String text, long chatId){
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(text);
        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void sendPhoto(File f, long chatId){
        SendPhoto photo = new SendPhoto()
                .setPhoto(f)
                .setChatId(chatId);
        try {
            Bot.getInstance().execute(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void infectedJob(long chatId){
        try {
            CovidData covidData = dbManager.getData();
            File f = covidData.currentlyInfectedGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
            f = covidData.newCurrentlyInfectedGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
        } catch (SQLException | IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private void recoveredJob(long chatId){
        try {
            CovidData covidData = dbManager.getData();
            File f = covidData.recoveredGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
            f = covidData.newRecoveredGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
        } catch (SQLException | IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private void deathsJob(long chatId){
        try {
            CovidData covidData = dbManager.getData();
            File f = covidData.deathGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
            f = covidData.newDeathGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
        } catch (SQLException | IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private void casesJob(long chatId){
        try {
            CovidData covidData = dbManager.getData();
            File f = covidData.totalCasesGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
            f = covidData.newTotalCasesGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
        } catch (SQLException | IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private void tamponsJob(long chatId){
        try {
            CovidData covidData = dbManager.getData();
            File f = covidData.tamponsGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
            f = covidData.newTamponsGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
        } catch (SQLException | IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }
    private void backupJob() throws IOException {
        File srcFile = new File("database.db");
        String date =  LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        File tempFile = new File("database-backup-" + date + ".db");
        FileUtils.copyFile(srcFile,tempFile);
        FileUtils.moveFileToDirectory(tempFile, new File("backups/"),true);
    }

    private String invertDate(String incorrectDate){
        String[] args = incorrectDate.split("-");
        if(args.length != 3)
            return null;
        return args[2] + "-" + args[1] + "-" + args[0];
    }
}
