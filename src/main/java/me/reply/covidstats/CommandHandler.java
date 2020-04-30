package me.reply.covidstats;

import com.vdurmont.emoji.EmojiParser;
import me.reply.covidstats.data.CovidData;
import me.reply.covidstats.data.DataFetcher;
import me.reply.covidstats.data.ProvinceCovidData;
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
    private final ReplyKeyboardMarkup provinceKeyboard;

    public void handle(String command, long chatId, String userId){

        String commandAliases = EmojiParser.parseToAliases(command);
        switch(commandAliases){
            case "/start":
                threads.submit(() -> sendMainKeyboard(chatId));
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
                    if(!Bot.getInstance().setRegion(userId,command))
                        sendMessage("\"" + command + "\" non è una regione valida!\nInserisci una tra queste: \n" + Bot.getInstance().getRegions(),chatId);
                    else{
                        sendMessage("Hai selezionato una nuova regione: " + command,chatId);
                        sendMainKeyboard(chatId);
                    }
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
                    sendMainKeyboard(chatId);

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
                .addText(EmojiParser.parseToUnicode(":mount_fuji: Seleziona provincia"))
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

        provinceKeyboard = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Agrigento")
                .addText("Alessandria")
                .addText("Ancona")
                .addText("Aosta")
                .row()
                .addText("Arezzo")
                .addText("Ascoli Piceno")
                .addText("Asti")
                .addText("Avellino")
                .row()
                .addText("Bari")
                .addText("Barletta-Andria-Trani")
                .addText("Belluno")
                .addText("Benevento")
                .row()
                .addText("Bergamo")
                .addText("Biella")
                .addText("Bologna")
                .addText("Bolzano")
                .row()
                .addText("Brescia")
                .addText("Brindisi")
                .addText("Cagliari")
                .addText("Caltanissetta")
                .row()
                .addText("Campobasso")
                .addText("Carbonia-Iglesias")
                .addText("Caserta")
                .addText("Catania")
                .row()
                .addText("Catanzaro")
                .addText("Chieti")
                .addText("Como")
                .addText("Cosenza")
                .row()
                .addText("Cremona")
                .addText("Crotone")
                .addText("Cuneo")
                .addText("Enna")
                .row()
                .addText("Fermo")
                .addText("Ferrara")
                .addText("Firenze")
                .addText("Foggia")
                .row()
                .addText("Forlì-Cesena")
                .addText("Friuli Venezia Giulia")
                .addText("Frosinone")
                .addText("Genova")
                .row()
                .addText("Gorizia")
                .addText("Grosseto")
                .addText("Imperia")
                .addText("Isernia")
                .row()
                .addText("L'Aquila")
                .addText("La Spezia")
                .addText("Latina")
                .addText("Lecce")
                .row()
                .addText("Lecco")
                .addText("Livorno")
                .addText("Lodi")
                .addText("Lucca")
                .row()
                .addText("Macerata")
                .addText("Mantova")
                .addText("Massa-Carrara")
                .addText("Matera")
                .row()
                .addText("Medio Campidano")
                .addText("Messina")
                .addText("Milano")
                .addText("Modena")
                .row()
                .addText("Monza e della Brianza")
                .addText("Napoli")
                .addText("Novara")
                .addText("Nuoro")
                .row()
                .addText("Ogliastra")
                .addText("Olbia-Tempio")
                .addText("Oristano")
                .addText("Padova")
                .row()
                .addText("Palermo")
                .addText("Parma")
                .addText("Pavia")
                .addText("Perugia")
                .row()
                .addText("Pesaro e Urbino")
                .addText("Pescara")
                .addText("Piacenza")
                .addText("Pisa")
                .row()
                .addText("Pistoia")
                .addText("Pordenone")
                .addText("Potenza")
                .addText("Prato")
                .row()
                .addText("Ragusa")
                .addText("Ravenna")
                .addText("Reggio di Calabria")
                .addText("Reggio nell'Emilia")
                .row()
                .addText("Rieti")
                .addText("Rimini")
                .addText("Roma")
                .addText("Rovigo")
                .row()
                .addText("Salerno")
                .addText("Sassari")
                .addText("Savona")
                .addText("Siena")
                .row()
                .addText("Siracusa")
                .addText("Sondrio")
                .addText("Taranto")
                .addText("Teramo")
                .row()
                .addText("Terni")
                .addText("Torino")
                .addText("Trapani")
                .addText("Trento")
                .row()
                .addText("Treviso")
                .addText("Trieste")
                .addText("Udine")
                .addText("Varese")
                .row()
                .addText("Venezia")
                .addText("Verbano-Cusio-Ossola")
                .addText("Vercelli")
                .addText("Verona")
                .row()
                .addText("Vibo Valentia")
                .addText("Vicenza")
                .addText("Viterbo")
                .addText("Nessuna provincia")
                .row()
                .addText("Torna indietro")
                .build();

        settingsKeyboard = ReplyKeyboardBuilder.createReply()
                .row()
                .addText(EmojiParser.parseToUnicode(":bell: Notifiche abilitate"))
                .addText(EmojiParser.parseToUnicode(":no_bell: Notifiche disabilitate"))
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
            keyboard.setText("Attualmente hai selezionato la regione \"" + region + "\", selezionane un'altra:");

        try {
            Bot.getInstance().execute(keyboard);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void switchToProvinceKeyboard(String userid,long chatId){
        SendMessage keyboard = new SendMessage()
                .setReplyMarkup(provinceKeyboard)
                .setChatId(chatId);

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
