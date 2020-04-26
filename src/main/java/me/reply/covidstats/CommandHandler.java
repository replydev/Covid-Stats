package me.reply.covidstats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.*;

public class CommandHandler {

    private final ExecutorService threads;

    private final Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    public void handle(String command, long chatId, String userId){
        Vector<String> commandSplitted = new Vector<>(Arrays.asList(command.split(" ")));
        String commandName = commandSplitted.firstElement();
        Vector<String> args = new Vector<>(commandSplitted);
        args.remove(0);

        switch(commandName){
            case "/start":
                threads.submit(() -> sendMessage("Welcome to CovidBot",chatId));
                break;
            case "/update":
                threads.submit(() -> {
                    if(isNotAdmin(userId))
                        sendMessage("You must be an admin to run this command!",chatId);
                    try {
                        DataFetcher.downloadFiles();
                        sendMessage("Done",chatId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "/infected":
                threads.submit(() -> infectedJob(Bot.getInstance().getRegionFromUser(userId),chatId));
                break;
            case "/recovered":
                threads.submit(() -> recoveredJob(Bot.getInstance().getRegionFromUser(userId),chatId));
                break;
            case "/deaths":
                threads.submit(() -> deathsJob(Bot.getInstance().getRegionFromUser(userId),chatId));
                break;
            case "/cases":
                threads.submit(() -> casesJob(Bot.getInstance().getRegionFromUser(userId),chatId));
                break;
            case "/tampons":
                threads.submit(() -> tamponsJob(Bot.getInstance().getRegionFromUser(userId),chatId));
                break;
            case "/setregion":
                threads.submit(() -> {
                    if(!Bot.getInstance().setRegion(userId,argsAsString(args))){
                        sendMessage("\"" + args.firstElement() + "\" is not a valid region!\nChoose a valid region: \n" + Bot.getInstance().getRegions(),chatId);
                    }
                });
                break;
            case "/sourcecode":
                threads.submit(() -> sendMessage("This bot is open and wants to make easier the data sharing all around the world! - https://github.com/replydev/Covid-Stats",chatId));
            default:
        }
    }

    public CommandHandler(int threadsNum){
        threads = Executors.newFixedThreadPool(threadsNum);
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

    private void infectedJob(String region,long chatId){
        try {
            CovidData data = DataFetcher.fetchData(region);
            File f = data.currentlyInfectedGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) logger.error("Error during file removal");
            f = data.newCurrentlyInfectedGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) logger.error("Error during file removal");
        } catch ( IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private void recoveredJob(String region,long chatId){
        try {
            CovidData data = DataFetcher.fetchData(region);
            File f = data.recoveredGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) logger.error("Error during file removal");
            f = data.newRecoveredGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) logger.error("Error during file removal");
        } catch (IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private void deathsJob(String region,long chatId){
        try {
            CovidData data = DataFetcher.fetchData(region);
            File f = data.deathGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) logger.error("Error during file removal");
            f = data.newDeathGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) logger.error("Error during file removal");
        } catch (IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private void casesJob(String region,long chatId){
        try {
            CovidData data = DataFetcher.fetchData(region);
            File f = data.totalCasesGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) logger.error("Error during file removal");
            f = data.newTotalCasesGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) logger.error("Error during file removal");
        } catch (IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private void tamponsJob(String region,long chatId){
        try {
            CovidData data = DataFetcher.fetchData(region);
            File f = data.tamponsGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) logger.error("Error during file removal");
            f = data.newTamponsGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) logger.error("Error during file removal");
        } catch (IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private boolean isNotAdmin(String id){
        return !Bot.getInstance().getConfig().isInUserlist(id);
    }

    private String argsAsString(Vector<String> args){
        StringBuilder builder = new StringBuilder();
        for(String s : args){
            builder.append(s).append(" ");
        }
        builder.deleteCharAt(builder.length() - 1); //remove last space
        return builder.toString();
    }
}
