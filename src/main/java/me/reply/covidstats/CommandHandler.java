package me.reply.covidstats;

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
                        Bot.updateCovidData(DataFetcher.fetchData());
                        sendMessage("Done",chatId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
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

    private void infectedJob(long chatId){
        try {
            CovidData covidData = Bot.getCovidData();
            File f = covidData.currentlyInfectedGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
            f = covidData.newCurrentlyInfectedGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
        } catch ( IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private void recoveredJob(long chatId){
        try {
            CovidData covidData = Bot.getCovidData();
            File f = covidData.recoveredGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
            f = covidData.newRecoveredGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
        } catch (IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private void deathsJob(long chatId){
        try {
            CovidData covidData = Bot.getCovidData();
            File f = covidData.deathGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
            f = covidData.newDeathGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
        } catch (IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private void casesJob(long chatId){
        try {
            CovidData covidData = Bot.getCovidData();
            File f = covidData.totalCasesGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
            f = covidData.newTotalCasesGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
        } catch (IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private void tamponsJob(long chatId){
        try {
            CovidData covidData = Bot.getCovidData();
            File f = covidData.tamponsGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
            f = covidData.newTamponsGraph();
            sendPhoto(f,chatId);
            if(!f.delete()) System.out.println("Error during file removal");
        } catch (IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }
    private boolean isNotAdmin(String id){
        return !Bot.getConfig().isInUserlist(id);
    }
}
