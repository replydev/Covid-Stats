import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandHandler {

    private final ExecutorService threads;

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
                if(args.size() != 5){
                    sendMessage("Please provide correct args",chatId);
                    return;
                }
                DayData dayData = new DayData(
                        Integer.parseInt(args.get(0)),
                        Integer.parseInt(args.get(1)),
                        Integer.parseInt(args.get(2)),
                        Integer.parseInt(args.get(3)),
                        args.get(4)
                );
                try {
                    dbManager.addData(dayData);
                } catch (SQLException throwable) {
                    System.out.println(throwable.getSQLState());
                    throwable.printStackTrace();
                }
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
            default:
        }
    }

    public CommandHandler(int threadsNum){
        threads = Executors.newFixedThreadPool(threadsNum);
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
}
