package me.reply.covidstats;

import org.apache.commons.io.FileExistsException;
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
import java.util.List;
import java.util.Vector;
import java.util.concurrent.*;

public class CommandHandler {

    private final ExecutorService threads;

    private DBManager dbManager;

    public void handle(String command, long chatId, String userId){
        Vector<String> commandSplitted = new Vector<>(Arrays.asList(command.split(" ")));
        String commandName = commandSplitted.firstElement();
        Vector<String> args = new Vector<>(commandSplitted);
        args.remove(0);

        switch(commandName){
            case "/start":
                threads.submit(() -> sendMessage("Welcome to CovidBot",chatId));
                break;
            case "/add":
                threads.submit(() -> addJob(userId,chatId,args));
                break;
            case "/infected":
                threads.submit(() -> infectedJob(chatId));
                break;
            case "/recovered":
                threads.submit(() -> recoveredJob(chatId));
                break;
            case "/deaths":
                threads.submit(() -> deathsJob(chatId));
                break;
            case "/cases":
                threads.submit(() -> casesJob(chatId));
                break;
            case "/tampons":
                threads.submit(() -> tamponsJob(chatId));
                break;
            case "/backup":
                threads.submit(() -> {
                    if(isNotAdmin(userId)){
                        sendMessage("You must be an admin to use this command!",chatId);
                        return;
                    }
                    try {
                        try{
                            backupJob();
                            sendMessage("Backup completed",chatId);
                        }catch (FileExistsException e){
                            sendMessage("You have already backup your data today",chatId);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        sendMessage("An error occurred: " + e.getMessage(),chatId);
                    }
                });
                break;
            case "/sourcecode":
                threads.submit(() -> sendMessage("This bot is open and wants to make easier the data sharing all around the world! - https://github.com/replydev/Covid-Stats",chatId));
            default:
        }
    }

    public CommandHandler(int threadsNum, Config config){
        threads = Executors.newFixedThreadPool(threadsNum);
        try {
            dbManager = new DBManager("database.db",config.START_DATE);
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
        try{
            FileUtils.moveFileToDirectory(tempFile, new File("backups/"),true);
        }catch (FileExistsException e){
            FileUtils.forceDelete(tempFile);
            throw new FileExistsException();
        }
    }

    private void addJob(String userId, long chatId, List<String> args){
        if(isNotAdmin(userId)){
            sendMessage("You must be an admin to use this command!",chatId);
            return;
        }
        if(args.size() != 4){
            sendMessage("Please provide correct args",chatId);
            return;
        }
        DayData dayData = new DayData(
                Integer.parseInt(args.get(0)),
                Integer.parseInt(args.get(1)),
                Integer.parseInt(args.get(2)),
                Integer.parseInt(args.get(3)),
                null //Insert in addData() method
        );
        try {
            dbManager.addData(dayData);
            sendMessage("Operation completed successfully",chatId);
        } catch (SQLException throwable) {
            System.out.println(throwable.getSQLState());
            throwable.printStackTrace();
        }
    }

    private boolean isNotAdmin(String id){
        return !Bot.getConfig().isInUserlist(id);
    }
}
