package me.reply.covidstats;

import com.vdurmont.emoji.EmojiParser;
import me.reply.covidstats.data.CovidData;
import me.reply.covidstats.data.DataFetcher;
import me.reply.covidstats.data.province.ProvinceCovidData;
import me.reply.covidstats.utils.Keyboards;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.*;

public class CommandHandler {

    private final ExecutorService threads;

    private final Logger logger = LoggerFactory.getLogger(CommandHandler.class);
    private final Keyboards keyboards;


    public void handle(String command, long chatId, String userId){

        String commandAliases = EmojiParser.parseToAliases(command);
        switch(commandAliases){
            case "/start":
                threads.submit(() -> sendMainKeyboard(userId,chatId));
                break;
            case ":warning: Attualmente contagiati":
                threads.submit(() -> infectedJob(Bot.getInstance().getRegionFromUser(userId),Bot.getInstance().getProvinceFromUser(userId),chatId));
                break;
            case ":diamond_shape_with_a_dot_inside: Guariti":
                threads.submit(() -> recoveredJob(Bot.getInstance().getRegionFromUser(userId),Bot.getInstance().getProvinceFromUser(userId),chatId));
                break;
            case ":angel: Decessi":
                threads.submit(() -> deathsJob(Bot.getInstance().getRegionFromUser(userId),Bot.getInstance().getProvinceFromUser(userId),chatId));
                break;
            case ":bangbang: Casi":
                threads.submit(() -> casesJob(Bot.getInstance().getRegionFromUser(userId),Bot.getInstance().getProvinceFromUser(userId),chatId));
                break;
            case ":syringe: Tamponi":
                threads.submit(() -> tamponsJob(Bot.getInstance().getRegionFromUser(userId),Bot.getInstance().getProvinceFromUser(userId),chatId));
                break;
            case ":mount_fuji: Seleziona regione":
                switchToRegionsKeyboard(userId,chatId);
                break;
            case ":mount_fuji: Seleziona provincia":
                switchToProvinceKeyboard(userId,chatId);
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
                    Bot.getInstance().setRegion(userId,command);
                    sendMessage("Hai selezionato una nuova regione: " + command,chatId);
                    sendMainKeyboard(userId,chatId);
                });
                break;
            case "Nessuna provincia":
            case "Agrigento":
            case "Alessandria":
            case "Ancona":
            case "Aosta":
            case "Arezzo":
            case "Ascoli Piceno":
            case "Asti":
            case "Avellino":
            case "Bari":
            case "Barletta-Andria-Trani":
            case "Belluno":
            case "Benevento":
            case "Bergamo":
            case "Biella":
            case "Bologna":
            case "Bolzano":
            case "Brescia":
            case "Brindisi":
            case "Cagliari":
            case "Caltanissetta":
            case "Campobasso":
            case "Carbonia-Iglesias":
            case "Caserta":
            case "Catania":
            case "Catanzaro":
            case "Chieti":
            case "Como":
            case "Cosenza":
            case "Cremona":
            case "Crotone":
            case "Cuneo":
            case "Enna":
            case "Fermo":
            case "Ferrara":
            case "Firenze":
            case "Foggia":
            case "Forlì-Cesena":
            case "Frosinone":
            case "Genova":
            case "Gorizia":
            case "Grosseto":
            case "Imperia":
            case "Isernia":
            case "L'Aquila":
            case "La Spezia":
            case "Latina":
            case "Lecce":
            case "Lecco":
            case "Livorno":
            case "Lodi":
            case "Lucca":
            case "Macerata":
            case "Mantova":
            case "Massa-Carrara":
            case "Matera":
            case "Medio Campidano":
            case "Messina":
            case "Milano":
            case "Modena":
            case "Monza e della Brianza":
            case "Napoli":
            case "Novara":
            case "Nuoro":
            case "Ogliastra":
            case "Olbia-Tempio":
            case "Oristano":
            case "Padova":
            case "Palermo":
            case "Parma":
            case "Pavia":
            case "Perugia":
            case "Pesaro e Urbino":
            case "Pescara":
            case "Piacenza":
            case "Pisa":
            case "Pistoia":
            case "Pordenone":
            case "Potenza":
            case "Prato":
            case "Ragusa":
            case "Ravenna":
            case "Reggio di Calabria":
            case "Reggio nell'Emilia":
            case "Rieti":
            case "Rimini":
            case "Roma":
            case "Rovigo":
            case "Salerno":
            case "Sassari":
            case "Savona":
            case "Siena":
            case "Siracusa":
            case "Sondrio":
            case "Taranto":
            case "Teramo":
            case "Terni":
            case "Torino":
            case "Trapani":
            case "Trento":
            case "Treviso":
            case "Trieste":
            case "Udine":
            case "Varese":
            case "Venezia":
            case "Verbano-Cusio-Ossola":
            case "Vercelli":
            case "Verona":
            case "Vibo Valentia":
            case "Vicenza":
            case "Viterbo":
                threads.submit(() -> {
                    Bot.getInstance().setProvince(userId,command);
                    sendMessage("Hai selezionato una nuova provincia: " + command,chatId);
                    sendMainKeyboard(userId,chatId);

                });
                break;
            case "Torna indietro":
                sendMainKeyboard(userId,chatId);
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
        this.keyboards = new Keyboards();
    }

    private void sendMainKeyboard(String userid,long chatId){
        SendMessage keyboard = Bot.getInstance().getProvinceFromUser(userid) == null ? new SendMessage()
                .setText("Benvenuto su Covid Italy Charts BETA, dimmi cosa fare.")
                .setReplyMarkup(keyboards.getMainKeyboard())
                .setChatId(chatId) :
                new SendMessage()
                        .setText("Benvenuto su Covid Italy Charts BETA, dimmi cosa fare.")
                        .setReplyMarkup(keyboards.getMainKeyboardProvince())
                        .setChatId(chatId);
        try {
            Bot.getInstance().execute(keyboard);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void switchToRegionsKeyboard(String userid,long chatId){
        SendMessage keyboard = new SendMessage()
                .setReplyMarkup(keyboards.getRegionsKeyboard())
                .setChatId(chatId);

        String region = Bot.getInstance().getRegionFromUser(userid);
        if(region == null)
            keyboard.setText("Non hai alcuna regione selezionata");
        else
            keyboard.setText("Attualmente hai selezionato la regione \"" + region + "\", selezionane un'altra:");

        try {
            Bot.getInstance().execute(keyboard);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void switchToProvinceKeyboard(String userid,long chatId){
        SendMessage keyboard = new SendMessage()
                .setChatId(chatId);
        String region = Bot.getInstance().getRegionFromUser(userid);
        if(region == null){
            sendMessage(EmojiParser.parseToUnicode(":x: Seleziona prima una regione"),chatId);
            return;
        }
        switch (region){
            case "Abruzzo":
                keyboard = keyboard.setReplyMarkup(keyboards.getAbruzzo());
                break;
            case "Basilicata":
                keyboard = keyboard.setReplyMarkup(keyboards.getBasilicata());
                break;
            case "P.A. Bolzano":
                keyboard = keyboard.setReplyMarkup(keyboards.getBolzano());
                break;
            case "Calabria":
                keyboard = keyboard.setReplyMarkup(keyboards.getCalabria());
                break;
            case "Campania":
                keyboard = keyboard.setReplyMarkup(keyboards.getCampania());
                break;
            case "Emilia-Romagna":
                keyboard = keyboard.setReplyMarkup(keyboards.getEmilia_romagna());
                break;
            case "Friuli Venezia Giulia":
                keyboard = keyboard.setReplyMarkup(keyboards.getFriuli_venezia_giulia());
                break;
            case "Lazio":
                keyboard = keyboard.setReplyMarkup(keyboards.getLazio());
                break;
            case "Liguria":
                keyboard = keyboard.setReplyMarkup(keyboards.getLiguria());
                break;
            case "Lombardia":
                keyboard = keyboard.setReplyMarkup(keyboards.getLombardia());
                break;
            case "Marche":
                keyboard = keyboard.setReplyMarkup(keyboards.getMarche());
                break;
            case "Molise":
                keyboard = keyboard.setReplyMarkup(keyboards.getMolise());
                break;
            case "Piemonte":
                keyboard = keyboard.setReplyMarkup(keyboards.getPiemonte());
                break;
            case "Puglia":
                keyboard = keyboard.setReplyMarkup(keyboards.getPuglia());
                break;
            case "Sardegna":
                keyboard = keyboard.setReplyMarkup(keyboards.getSardegna());
                break;
            case "Sicilia":
                keyboard = keyboard.setReplyMarkup(keyboards.getSicilia());
                break;
            case "Toscana":
                keyboard = keyboard.setReplyMarkup(keyboards.getToscana());
                break;
            case "P.A. Trento":
                keyboard = keyboard.setReplyMarkup(keyboards.getTrento());
                break;
            case "Umbria":
                keyboard = keyboard.setReplyMarkup(keyboards.getUmbria());
                break;
            case "Valle d'Aosta":
                keyboard = keyboard.setReplyMarkup(keyboards.getValle_d_aosta());
                break;
            case "Veneto":
                keyboard = keyboard.setReplyMarkup(keyboards.getVeneto());
                break;
            default:
                sendMessage(EmojiParser.parseToUnicode(":x: Errore: regione non valida (" + region + ")"),chatId);
                break;
        }


        String province = Bot.getInstance().getProvinceFromUser(userid);
        if(province == null)
            keyboard.setText("Non hai alcuna provincia selezionata");
        else
            keyboard.setText("Attualmente hai selezionato la provincia \"" + province + "\", selezionane un'altra:");

        try {
            Bot.getInstance().execute(keyboard);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void switchToSettingsKeyboard(long chatid){
        SendMessage keyboard = new SendMessage()
                .setText("Impostazioni:")
                .setReplyMarkup(keyboards.getSettingsKeyboard())
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

    private void infectedJob(String region,String province,long chatId){
        if(province != null) {
            sendMessage(EmojiParser.parseToUnicode(":x: Dati non disponibili per le provincie!"), chatId);
            return;
        }
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

    private void recoveredJob(String region,String province,long chatId){
        if(province != null) {
            sendMessage(EmojiParser.parseToUnicode(":x: Dati non disponibili per le provincie!"), chatId);
            return;
        }
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

    private void deathsJob(String region,String province,long chatId){
        if(province != null) {
            sendMessage(EmojiParser.parseToUnicode(":x: Dati non disponibili per le provincie!"), chatId);
            return;
        }
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

    private void casesJob(String region,String province,long chatId){
        if(province != null){
            try{
                ProvinceCovidData data = DataFetcher.fetchProvinceData(province);
                File f = data.totalCasesGraph(province);
                sendPhoto(f,chatId);
                FileUtils.forceDelete(f);
                f = data.newTotalCasesGraph(province);
                sendPhoto(f,chatId);
                FileUtils.forceDelete(f);
            }catch (IOException | ParseException e){
                e.printStackTrace();
            }
        }
        else{
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
    }

    private void tamponsJob(String region,String province,long chatId){
        if(province != null) {
            sendMessage(EmojiParser.parseToUnicode(":x: Dati non disponibili per le provincie!"), chatId);
            return;
        }
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
