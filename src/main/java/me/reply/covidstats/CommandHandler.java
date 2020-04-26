package me.reply.covidstats;

import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;
import java.util.concurrent.*;

public class CommandHandler {

    private final ExecutorService threads;

    private final Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    private final ReplyKeyboardMarkup mainKeyboard;
    private final ReplyKeyboardMarkup regionsKeyboard;

    public void handle(String command, long chatId, String userId){

        switch(EmojiParser.parseToAliases(command)){
            case "/start":
                threads.submit(() -> sendMainKeyboard(chatId));
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
            case ":warning: Infected":
                threads.submit(() -> infectedJob(Bot.getInstance().getRegionFromUser(userId),chatId));
                break;
            case ":diamond_shape_with_a_dot_inside: Recovered":
                threads.submit(() -> recoveredJob(Bot.getInstance().getRegionFromUser(userId),chatId));
                break;
            case ":angel: Deaths":
                threads.submit(() -> deathsJob(Bot.getInstance().getRegionFromUser(userId),chatId));
                break;
            case ":heavy_exclamation_mark: Cases":
                threads.submit(() -> casesJob(Bot.getInstance().getRegionFromUser(userId),chatId));
                break;
            case ":syringe: Tampons":
                threads.submit(() -> tamponsJob(Bot.getInstance().getRegionFromUser(userId),chatId));
                break;
            case ":mount_fuji: Set region":
                switchToRegionsKeyboard(chatId);
                break;
            case ":page_facing_up: Source code":
                threads.submit(() -> sendMessage("This bot is open and wants to make easier the data sharing all around the world! - https://github.com/replydev/Covid-Stats",chatId));
                break;
            case "Abruzzo":
            case "Basilicata":
            case "P.A Bolzano":
            case "Calabria":
            case "Campania":
            case "Emilia-Romagna":
            case "Friuli Venezia Giulia":
            case "Italia":
            case "Lazio":
            case "Liguria":
            case "Marche":
            case "Molise":
            case "Piemonte":
            case "Puglia":
            case "Sardegna":
            case "Sicilia":
            case "Toscana":
            case "P.A Trento":
            case "Umbria":
            case "Valle d'Aosta":
            case "Veneto":
                threads.submit(() -> {
                    if(!Bot.getInstance().setRegion(userId,command)){
                        sendMessage("\"" + command + "\" is not a valid region!\nChoose a valid region: \n" + Bot.getInstance().getRegions(),chatId);
                    }
                });
                break;
            case "Go back":
                sendMainKeyboard(chatId);
                break;
            default:
        }
    }

    public CommandHandler(int threadsNum){
        threads = Executors.newFixedThreadPool(threadsNum);

        mainKeyboard = ReplyKeyboardBuilder.createReply()
                .row()
                .addText(EmojiParser.parseToUnicode(":warning: Infected"))
                .addText(EmojiParser.parseToUnicode(":diamond_shape_with_a_dot_inside: Recovered"))
                .addText(EmojiParser.parseToUnicode(":angel: Deaths"))
                .row()
                .addText(EmojiParser.parseToUnicode(":heavy_exclamation_mark: Cases"))
                .addText(EmojiParser.parseToUnicode(":syringe: Tampons"))
                .addText(EmojiParser.parseToUnicode(":mount_fuji: Set region"))
                .row()
                .addText(EmojiParser.parseToUnicode(":page_facing_up: Source code"))
                .build();

        // "Italy","Abruzzo","Basilicata","P.A Bolzano","Calabria","Campania","Emilia-Romagna","Friuli Venezia Giulia","Lazio",
        // "Liguria","Lombardia","Marche","Molise","Piemonte",
        // "Puglia","Sardegna","Sicilia","Toscana","P.A Trento","Umbria","Valle d'Aosta","Veneto"
        regionsKeyboard = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Abbruzzo")
                .addText("Basilicata")
                .addText("P.A Bolzano")
                .row()
                .addText("Calabria")
                .addText("Campania")
                .addText("Emilia-Romagna")
                .row()
                .addText("Friuli Venezia Giulia")
                .addText("Italia")
                .addText("Lazio")
                .row()
                .addText("Liguria")
                .addText("Lombardia")
                .addText("Marche")
                .row()
                .addText("Molise")
                .addText("Piemonte")
                .addText("Puglia")
                .row()
                .addText("Sardegna")
                .addText("Sicilia")
                .addText("Toscana")
                .row()
                .addText("P.A Trento")
                .addText("Umbria")
                .addText("Valle d'Aosta")
                .row()
                .addText("Veneto")
                .addText("Go back")
                .build();
    }

    private void sendMainKeyboard(long chatId){
        SendMessage keyboard = new SendMessage()
                .setText("Welcome to Covid Stats Bot, tell me what to do.")
                .setReplyMarkup(mainKeyboard)
                .setChatId(chatId);
        try {
            Bot.getInstance().execute(keyboard);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void switchToRegionsKeyboard(long chatId){
        SendMessage keyboard = new SendMessage()
                .setText("Select a region:")
                .setReplyMarkup(regionsKeyboard)
                .setChatId(chatId);
        try {
            Bot.getInstance().execute(keyboard);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
