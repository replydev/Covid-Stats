package me.reply.covidstats;

import com.vdurmont.emoji.EmojiParser;
import me.reply.covidstats.data.CovidData;
import me.reply.covidstats.data.DataFetcher;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.*;

public class CommandHandler {

    private final ExecutorService threads;

    private final Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    private final ReplyKeyboardMarkup mainKeyboard;
    private final ReplyKeyboardMarkup regionsKeyboard;
    private final ReplyKeyboardMarkup settingsKeyboard;

    public void handle(String command, long chatId, String userId){

        String commandAliases = EmojiParser.parseToAliases(command);
        switch(commandAliases){
            case "/start":
                threads.submit(() -> sendMainKeyboard(chatId));
                break;
            case ":warning: Attualmente contagiati":
                threads.submit(() -> infectedJob(Bot.getInstance().getRegionFromUser(userId),chatId));
                break;
            case ":diamond_shape_with_a_dot_inside: Guariti":
                threads.submit(() -> recoveredJob(Bot.getInstance().getRegionFromUser(userId),chatId));
                break;
            case ":angel: Decessi":
                threads.submit(() -> deathsJob(Bot.getInstance().getRegionFromUser(userId),chatId));
                break;
            case ":bangbang: Casi":
                threads.submit(() -> casesJob(Bot.getInstance().getRegionFromUser(userId),chatId));
                break;
            case ":syringe: Tamponi":
                threads.submit(() -> tamponsJob(Bot.getInstance().getRegionFromUser(userId),chatId));
                break;
            case ":mount_fuji: Seleziona regione":
                switchToRegionsKeyboard(userId,chatId);
                break;
            case ":page_facing_up: Codice sorgente":
                threads.submit(() -> sendMessage(EmojiParser.parseToUnicode(":smile_cat: Sviluppato da @zreply.\n:page_facing_up: Il codice sorgente di questo software è open source, qualsiasi modifica utile ed appropriata è la benvenuta!\n:link: https://github.com/replydev/Covid-Stats"),chatId));
                break;
            case "Abruzzo":
            case "Basilicata":
            case "P.A. Bolzano":
            case "Calabria":
            case "Campania":
            case "Emilia-Romagna":
            case "Friuli Venezia Giulia":
            case "Italia":
            case "Lazio":
            case "Liguria":
            case "Lombardia":
            case "Marche":
            case "Molise":
            case "Piemonte":
            case "Puglia":
            case "Sardegna":
            case "Sicilia":
            case "Toscana":
            case "P.A. Trento":
            case "Umbria":
            case "Valle d'Aosta":
            case "Veneto":
                threads.submit(() -> {
                    if(!Bot.getInstance().setRegion(userId,command))
                        sendMessage("\"" + command + "\" non è una regione valida!\nInserisci una tra queste: \n" + Bot.getInstance().getRegions(),chatId);
                    else{
                        sendMessage("Hai selezionato una nuova regione: " + command,chatId);
                        sendMainKeyboard(chatId);
                    }
                });
                break;
            case "Torna indietro":
                sendMainKeyboard(chatId);
                break;
            case ":wrench: Impostazioni":
                switchToSettingsKeyboard(chatId);
                break;
            case ":bell: Notifiche abilitate":
                threads.submit(() -> {
                    Bot.getInstance().setNotification(userId,true);
                    sendMessage(EmojiParser.parseToUnicode(":white_check_mark: Hai abilitato nel notifiche giornaliere"),chatId);
                });
                break;
            case ":bell: Notifiche disabilitate":
                threads.submit(() -> {
                    Bot.getInstance().setNotification(userId,false);
                    sendMessage(EmojiParser.parseToUnicode(":x: Hai disabilitato nel notifiche giornaliere"),chatId);
                });
                break;
            case "/update":
                threads.submit(() -> {
                    if(isNotAdmin(userId)){
                        sendMessage("Comando riservato agli admin!",chatId);
                        return;
                    }
                    try {
                        if(DataFetcher.updateFiles())
                            sendMessage("Operazione completata con successo.", chatId);
                        else
                            sendMessage("Non ho trovato aggiornamenti.", chatId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case "/stop":
                threads.submit(() -> {
                    if(isNotAdmin(userId)){
                        sendMessage("Comando riservato agli admin!",chatId);
                        return;
                    }
                    try {
                        logger.info("Called /stop command by admin, exiting...");
                        Bot.getInstance().backupUserList(chatId);
                        System.exit(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            default:
        }
    }

    public CommandHandler(int threadsNum){
        threads = Executors.newFixedThreadPool(threadsNum);

        mainKeyboard = ReplyKeyboardBuilder.createReply()
                .row()
                .addText(EmojiParser.parseToUnicode(":warning: Attualmente contagiati"))
                .addText(EmojiParser.parseToUnicode(":diamond_shape_with_a_dot_inside: Guariti"))
                .addText(EmojiParser.parseToUnicode(":angel: Decessi"))
                .row()
                .addText(EmojiParser.parseToUnicode(":bangbang: Casi"))
                .addText(EmojiParser.parseToUnicode(":syringe: Tamponi"))
                .addText(EmojiParser.parseToUnicode(":mount_fuji: Seleziona regione"))
                .row()
                .addText(EmojiParser.parseToUnicode(":wrench: Impostazioni"))
                .addText(EmojiParser.parseToUnicode(":page_facing_up: Codice sorgente"))
                .build();

        regionsKeyboard = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Abruzzo")
                .addText("Basilicata")
                .addText("P.A. Bolzano")
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
                .addText("P.A. Trento")
                .addText("Umbria")
                .addText("Valle d'Aosta")
                .row()
                .addText("Veneto")
                .addText("Torna indietro")
                .build();

        settingsKeyboard = ReplyKeyboardBuilder.createReply()
                .row()
                .addText(EmojiParser.parseToUnicode(":bell: Notifiche abilitate"))
                .addText(EmojiParser.parseToUnicode(":bell: Notifiche disabilitate"))
                .row()
                .addText("Torna indietro")
                .build();
    }

    private void sendMainKeyboard(long chatId){
        SendMessage keyboard = new SendMessage()
                .setText("Benvenuto su Covid Italy Charts BETA, dimmi cosa fare.")
                .setReplyMarkup(mainKeyboard)
                .setChatId(chatId);
        try {
            Bot.getInstance().execute(keyboard);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void switchToRegionsKeyboard(String userid,long chatId){
        SendMessage keyboard = new SendMessage()
                .setReplyMarkup(regionsKeyboard)
                .setChatId(chatId);

        String region = Bot.getInstance().getRegionFromUser(userid);
        if(region == null)
            keyboard.setText("Non hai alcuna regione selezionata");
        else
            keyboard.setText("Attualmente hai selezionato la regione \"" + Bot.getInstance().getRegionFromUser(userid) + "\", selezionane un'altra:");

        try {
            Bot.getInstance().execute(keyboard);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void switchToSettingsKeyboard(long chatid){
        SendMessage keyboard = new SendMessage()
                .setText("Impostazioni:")
                .setReplyMarkup(settingsKeyboard)
                .setChatId(chatid);
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
            File f = data.currentlyInfectedGraph(region);
            sendPhoto(f,chatId);
            FileUtils.forceDelete(f);
            f = data.newCurrentlyInfectedGraph(region);
            sendPhoto(f,chatId);
            FileUtils.forceDelete(f);
        } catch ( IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private void recoveredJob(String region,long chatId){
        try {
            CovidData data = DataFetcher.fetchData(region);
            File f = data.recoveredGraph(region);
            sendPhoto(f,chatId);
            FileUtils.forceDelete(f);
            f = data.newRecoveredGraph(region);
            sendPhoto(f,chatId);
            FileUtils.forceDelete(f);
        } catch (IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private void deathsJob(String region,long chatId){
        try {
            CovidData data = DataFetcher.fetchData(region);
            File f = data.deathGraph(region);
            sendPhoto(f,chatId);
            FileUtils.forceDelete(f);
            f = data.newDeathGraph(region);
            sendPhoto(f,chatId);
            FileUtils.forceDelete(f);
        } catch (IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private void casesJob(String region,long chatId){
        try {
            CovidData data = DataFetcher.fetchData(region);
            File f = data.totalCasesGraph(region);
            sendPhoto(f,chatId);
            FileUtils.forceDelete(f);
            f = data.newTotalCasesGraph(region);
            sendPhoto(f,chatId);
            FileUtils.forceDelete(f);
        } catch (IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private void tamponsJob(String region,long chatId){
        try {
            CovidData data = DataFetcher.fetchData(region);
            File f = data.tamponsGraph(region);
            sendPhoto(f,chatId);
            FileUtils.forceDelete(f);
            f = data.newTamponsGraph(region);
            sendPhoto(f,chatId);
            FileUtils.forceDelete(f);
        } catch (IOException | ParseException throwable) {
            throwable.printStackTrace();
        }
    }

    private boolean isNotAdmin(String id){
        return !Bot.getInstance().getConfig().isInUserlist(id);
    }
}
