package me.reply.covidstats;

import com.vdurmont.emoji.EmojiParser;
import me.reply.covidstats.data.ChartUtils;
import me.reply.covidstats.data.CovidData;
import me.reply.covidstats.data.DataFetcher;
import me.reply.covidstats.data.province.ProvinceCovidData;
import me.reply.covidstats.utils.Keyboards;
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
                threads.submit(() -> infectedJob(
                        Bot.getInstance().getUsersManager().getIdFromUser(userId),
                        Bot.getInstance().getUsersManager().getRegionFromUser(userId),
                        Bot.getInstance().getUsersManager().getProvinceFromUser(userId),
                        chatId)
                );
                break;
            case ":diamond_shape_with_a_dot_inside: Guariti":
                threads.submit(() -> recoveredJob(
                        Bot.getInstance().getUsersManager().getIdFromUser(userId),
                        Bot.getInstance().getUsersManager().getRegionFromUser(userId),
                        Bot.getInstance().getUsersManager().getProvinceFromUser(userId),
                        chatId)
                );
                break;
            case ":angel: Decessi":
                threads.submit(() -> deathsJob(
                        Bot.getInstance().getUsersManager().getIdFromUser(userId),
                        Bot.getInstance().getUsersManager().getRegionFromUser(userId),
                        Bot.getInstance().getUsersManager().getProvinceFromUser(userId),
                        chatId)
                );
                break;
            case ":bangbang: Casi":
                threads.submit(() -> casesJob(
                        Bot.getInstance().getUsersManager().getIdFromUser(userId),
                        Bot.getInstance().getUsersManager().getRegionFromUser(userId),
                        Bot.getInstance().getUsersManager().getProvinceFromUser(userId),
                        chatId)
                );
                break;
            case ":syringe: Tamponi":
                threads.submit(() -> tamponsJob(
                        Bot.getInstance().getUsersManager().getIdFromUser(userId),
                        Bot.getInstance().getUsersManager().getRegionFromUser(userId),
                        Bot.getInstance().getUsersManager().getProvinceFromUser(userId),
                        chatId)
                );
                break;
            case ":mount_fuji: Seleziona regione":
                switchToRegionsKeyboard(userId,chatId);
                break;
            case ":office: Seleziona provincia":
                switchToProvinceKeyboard(userId,chatId);
                break;
            case ":computer: Codice sorgente":
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
                    Bot.getInstance().getUsersManager().setRegion(userId,command);
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
                    String regionToSet = getRegionFromProvince(command); //Milano -> Lombardia
                    if(regionToSet != null){
                        Bot.getInstance().getUsersManager().setRegion(userId,regionToSet);
                        Bot.getInstance().getUsersManager().setProvince(userId,command);
                    }
                    else{
                        Bot.getInstance().getUsersManager().setProvince(userId,"Nessuna provincia");
                    }
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
                    Bot.getInstance().getUsersManager().setNotification(userId,true);
                    sendMessage((":white_check_mark: Hai abilitato nel notifiche giornaliere"),chatId);
                });
                break;
            case ":no_bell: Notifiche disabilitate":
                threads.submit(() -> {
                    Bot.getInstance().getUsersManager().setNotification(userId,false);
                    sendMessage((":x: Hai disabilitato nel notifiche giornaliere"),chatId);
                });
                break;
            case "/update":
                threads.submit(() -> {
                    if(isNotAdmin(userId)){
                        sendMessage((":x: Comando riservato"),chatId);
                        return;
                    }
                    try {
                        if(DataFetcher.updateFiles()){
                            ChartUtils.clearCache();
                            sendMessage("Operazione completata con successo.", chatId);
                        }
                        else
                            sendMessage("Non ho trovato aggiornamenti.", chatId);
                    } catch (IOException e) {
                        System.err.println("Si è verificato un errore, verifica nel file di log");
                        logger.error(e.toString());
                    }
                });
                break;
            case "/stop":
                threads.submit(() -> {
                    if(isNotAdmin(userId)){
                        sendMessage((":x: Comando riservato"),chatId);
                        return;
                    }
                    logger.info("Un username ha inviato il comando di chiusura, sto terminando il programma...");
                    sendMessage("Sto terminando il bot",chatId);
                    System.exit(0); //when i shutdown i call sigint runnable
                });
                break;
            case "/confirm":
                threads.submit(() -> {
                    if(isNotAdmin(userId)){
                        sendMessage((":x: Comando riservato"),chatId);
                        return;
                    }
                    String text = Bot.getInstance().getUsersManager().getNotificationTextFromUser(userId);
                    if(text == null){
                        sendMessage((":x: Nessun messaggio da inviare!"),chatId);
                        return;
                    }
                    sendMessage("Sto inviando la tua notifica a tutti gli utenti...",chatId);
                    Bot.getInstance().messageToAllUsers(text);
                    Bot.getInstance().getUsersManager().setNotificationText(userId,null);
                });
                break;
            default:
                threads.submit(() -> {
                    if(isNotAdmin(userId)){
                        return;
                    }
                    Bot.getInstance().getUsersManager().setNotificationText(userId,EmojiParser.parseToUnicode(command));
                    sendMessage("Ok, digita /confirm per inviare \"" + Bot.getInstance().getUsersManager().getNotificationTextFromUser(userId) + "\" a tutti gli utenti",chatId);
                });
        }
    }

    private String getRegionFromProvince(String province) {
        switch (province){
            case "Chieti":
            case "L'Aquila":
            case "Pescara":
            case "Teramo":
                return "Abruzzo";
            case "Matera":
            case "Potenza":
                return "Basilicata";
            case "Bolzano":
                return "P.A. Bolzano";
            case "Catanzaro":
            case "Cosenza":
            case "Crotone":
            case "Reggio di Calabria":
            case "Vibo Valentia":
                return "Calabria";
            case "Avellino":
            case "Benevento":
            case "Caserta":
            case "Napoli":
            case "Salerno":
                return "Campania";
            case "Bologna":
            case "Ferrara":
            case "Forlì-Cesena":
            case "Cesena":
            case "Modena":
            case "Parma":
            case "Piacenza":
            case "Reggio nell'Emilia":
            case "Ravenna":
            case "Rimini":
                return "Emilia-Romagna";
            case "Gorizia":
            case "Pordenone":
            case "Trieste":
            case "Udine":
                return "Friuli Venezia Giulia";
            case "Frosinone":
            case "Latina":
            case "Rieti":
            case "Roma":
            case "Viterbo":
                return "Lazio";
            case "Genova":
            case "Imperia":
            case "La Spezia":
            case "Savona":
                return "Liguria";
            case "Bergamo":
            case "Brescia":
            case "Como":
            case "Cremona":
            case "Lecco":
            case "Lodi":
            case "Mantova":
            case "Milano":
            case "Monza e della Brianza":
            case "Pavia":
            case "Sondrio":
            case "Varese":
                return "Lombardia";
            case "Ancona":
            case "Ascoli Piceno":
            case "Fermo":
            case "Macerata":
            case "Pesaro e Urbino":
                return "Marche";
            case "Campobasso":
            case "Isernia":
                return "Molise";
            case "Alessandria":
            case "Asti":
            case "Biella":
            case "Cuneo":
            case "Novara":
            case "Torino":
            case "Verbano-Cusio-Ossola":
            case "Vercelli":
                return "Piemonte";
            case "Bari":
            case "Barletta-Andria-Trani":
            case "Brindisi":
            case "Foggia":
            case "Lecce":
            case "Taranto":
                return "Puglia";
            case "Cagliari":
            case "Carbonia-Iglesias":
            case "Medio Campidano":
            case "Nuoro":
            case "Ogliastra":
            case "Olbia-Tempio":
            case "Oristano":
            case "Sassari":
                return "Sardegna";
            case "Agrigento":
            case "Caltanissetta":
            case "Catania":
            case "Enna":
            case "Messina":
            case "Palermo":
            case "Ragusa":
            case "Siracusa":
            case "Trapani":
                return "Sicilia";
            case "Arezzo":
            case "Firenze":
            case "Grosseto":
            case "Livorno":
            case "Lucca":
            case "Massa-Carrara":
            case "Pisa":
            case "Pistoia":
            case "Prato":
            case "Siena":
                return "Toscana";
            case "Trento":
                return "P.A. Trento";
            case "Perugia":
            case "Terni":
                return "Umbria";
            case "Aosta":
                return "Valle d'Aosta";
            case "Belluno":
            case "Padova":
            case "Rovigo":
            case "Treviso":
            case "Venezia":
            case "Verona":
            case "Vicenza":
                return "Veneto";
            case "Nessuna provincia":
            default: return null;
        }
    }

    public CommandHandler(int threadsNum){
        threads = Executors.newFixedThreadPool(threadsNum);
        this.keyboards = new Keyboards();
    }

    private void sendMainKeyboard(String userid,long chatId){
        SendMessage keyboard = Bot.getInstance().getUsersManager().getProvinceFromUser(userid) == null ? new SendMessage()
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
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
    }

    private void switchToRegionsKeyboard(String userId,long chatId){
        SendMessage keyboard = new SendMessage()
                .setReplyMarkup(keyboards.getRegionsKeyboard())
                .setChatId(chatId);

        String region = Bot.getInstance().getUsersManager().getRegionFromUser(userId);
        if(region == null)
            keyboard.setText("Non hai alcuna regione selezionata");
        else
            keyboard.setText("Attualmente hai selezionato la regione \"" + region + "\", selezionane un'altra:");

        try {
            Bot.getInstance().execute(keyboard);
        } catch (TelegramApiException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
    }

    private void switchToProvinceKeyboard(String userid,long chatId){
        SendMessage keyboard = new SendMessage()
                .setChatId(chatId);
        String region = Bot.getInstance().getUsersManager().getRegionFromUser(userid);
        if(region == null){
            sendMessage((":x: Seleziona prima una regione"),chatId);
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
                sendMessage((":x: Errore: regione non valida (" + region + ")"),chatId);
                break;
        }

        String province = Bot.getInstance().getUsersManager().getProvinceFromUser(userid);
        if(province == null)
            keyboard.setText("Non hai alcuna provincia selezionata");
        else
            keyboard.setText("Attualmente hai selezionato la provincia \"" + province + "\", selezionane un'altra:");

        try {
            Bot.getInstance().execute(keyboard);
        } catch (TelegramApiException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
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
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
    }

    private void sendMessage(String text, long chatId){
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(EmojiParser.parseToUnicode(text));
        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
    }

    private void sendPhoto(File f, long chatId, String text){
        SendPhoto photo = new SendPhoto()
                .setPhoto(f)
                .setCaption(text)
                .setChatId(chatId);
        try {
            Bot.getInstance().execute(photo);
        } catch (TelegramApiException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
    }

    private void infectedJob(int id,String region,String province,long chatId){
        if(!canMakeRequest(id)){
            sendMessage((":x: Aspetta che la tua richiesta precedente venga terminata!"),chatId);
            return;
        }
        Bot.getInstance().getUsersManager().setCanMakeRequest(id,false);
        if(province != null) {
            sendMessage((":x: Dati non disponibili per le provincie!"), chatId);
            Bot.getInstance().getUsersManager().setCanMakeRequest(id,true);
            return;
        }
        try {
            CovidData data = DataFetcher.fetchData(region);
            File f = data.currentlyInfectedGraph(region);
            String text,text1;
            if(region == null){
                text = "Ultimi dati: " + DataFetcher.getItalyCurrentlyInfected();
                text1 = "Ultimi dati: " + DataFetcher.getItalyNewCurrentlyInfected();
            }

            else{
                text = "Ultimi dati: " + DataFetcher.getRegionsCurrentlyInfected(region);
                text1 = "Ultimi dati: " + DataFetcher.getRegionsNewCurrentlyInfected(region);
            }
            sendPhoto(f,chatId,text);
            f = data.newCurrentlyInfectedGraph(region);
            sendPhoto(f,chatId, text1);
        } catch ( IOException | ParseException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
        Bot.getInstance().getUsersManager().setCanMakeRequest(id,true);
    }

    private void recoveredJob(int id,String region,String province,long chatId){
        if(!canMakeRequest(id)){
            sendMessage(":x: Aspetta che la tua richiesta precedente venga terminata!",chatId);
            return;
        }
        Bot.getInstance().getUsersManager().setCanMakeRequest(id,false);
        if(province != null) {
            sendMessage((":x: Dati non disponibili per le provincie!"), chatId);
            Bot.getInstance().getUsersManager().setCanMakeRequest(id,true);
            return;
        }
        try {
            CovidData data = DataFetcher.fetchData(region);
            File f = data.recoveredGraph(region);
            String text,text1;
            if(region == null){
                text = "Ultimi dati: " + DataFetcher.getItalyRecovered();
                text1 = "Ultimi dati: " + DataFetcher.getItalyNewRecovered();
            }

            else{
                text = "Ultimi dati: " + DataFetcher.getRegionsRecovered(region);
                text1 = "Ultimi dati: " + DataFetcher.getRegionsNewRecovered(region);
            }
            sendPhoto(f,chatId, text);
            f = data.newRecoveredGraph(region);
            sendPhoto(f,chatId, text1);
        } catch (IOException | ParseException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
        Bot.getInstance().getUsersManager().setCanMakeRequest(id,true);
    }

    private void deathsJob(int id,String region,String province,long chatId){
        if(!canMakeRequest(id)){
            sendMessage(":x: Aspetta che la tua richiesta precedente venga terminata!",chatId);
            return;
        }
        Bot.getInstance().getUsersManager().setCanMakeRequest(id,false);
        if(province != null) {
            sendMessage((":x: Dati non disponibili per le provincie!"), chatId);
            Bot.getInstance().getUsersManager().setCanMakeRequest(id,true);
            return;
        }
        try {
            CovidData data = DataFetcher.fetchData(region);
            File f = data.deathGraph(region);
            String text,text1;
            if(region == null){
                text = "Ultimi dati: " + DataFetcher.getItalyDeaths();
                text1 = "Ultimi dati: " + DataFetcher.getItalyNewDeaths();
            }

            else{
                text = "Ultimi dati: " + DataFetcher.getRegionsDeaths(region);
                text1 = "Ultimi dati: " + DataFetcher.getRegionsNewDeaths(region);
            }
            sendPhoto(f,chatId, text);
            f = data.newDeathGraph(region);
            sendPhoto(f,chatId, text1);
        } catch (IOException | ParseException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
        Bot.getInstance().getUsersManager().setCanMakeRequest(id,true);
    }

    private void casesJob(int id,String region,String province,long chatId){
        if(!canMakeRequest(id)){
            sendMessage(":x: Aspetta che la tua richiesta precedente venga terminata!",chatId);
            return;
        }
        Bot.getInstance().getUsersManager().setCanMakeRequest(id,false);
        if(province != null){
            try{
                ProvinceCovidData data = DataFetcher.fetchProvinceData(province);
                File f = data.totalCasesGraph(province);
                String text,text1;
                text = "Ultimi dati: " + DataFetcher.getprovinceTotalCases(province);
                text1 = "Ultimi dati: " + DataFetcher.getprovinceNewCases(province);
                sendPhoto(f,chatId, text);
                f = data.newTotalCasesGraph(province);
                sendPhoto(f,chatId, text1);
            }catch (IOException | ParseException e){
                System.err.println("Si è verificato un errore, verifica nel file di log");
                logger.error(e.toString());
            }
        }
        else{
            try {
                CovidData data = DataFetcher.fetchData(region);
                File f = data.totalCasesGraph(region);
                String text,text1;
                if(region == null){
                    text = "Ultimi dati: " + DataFetcher.getItalyTotalCases();
                    text1 = "Ultimi dati: " + DataFetcher.getItalyNewCases();
                }

                else{
                    text = "Ultimi dati: " + DataFetcher.getRegionsTotalCases(region);
                    text1 = "Ultimi dati: " + DataFetcher.getRegionsNewCases(region);
                }
                sendPhoto(f,chatId, text);
                f = data.newTotalCasesGraph(region);
                sendPhoto(f,chatId, text1);
            } catch (IOException | ParseException e) {
                System.err.println("Si è verificato un errore, verifica nel file di log");
                logger.error(e.toString());
            }
        }
        Bot.getInstance().getUsersManager().setCanMakeRequest(id,true);
    }

    private void tamponsJob(int id,String region,String province,long chatId){
        if(!canMakeRequest(id)){
            sendMessage(":x: Aspetta che la tua richiesta precedente venga terminata!",chatId);
            return;
        }
        Bot.getInstance().getUsersManager().setCanMakeRequest(id,false);
        if(province != null) {
            sendMessage((":x: Dati non disponibili per le provincie!"), chatId);
            Bot.getInstance().getUsersManager().setCanMakeRequest(id,true);
            return;
        }
        try {
            CovidData data = DataFetcher.fetchData(region);
            File f = data.tamponsGraph(region);
            String text,text1;
            if(region == null){
                text = "Ultimi dati: " + DataFetcher.getItalyTampons();
                text1 = "Ultimi dati: " + DataFetcher.getItalyNewTampons();
            }

            else{
                text = "Ultimi dati: " + DataFetcher.getRegionsTampons(region);
                text1 = "Ultimi dati: " + DataFetcher.getRegionsNewTampons(region);
            }
            sendPhoto(f,chatId, text);
            f = data.newTamponsGraph(region);
            sendPhoto(f,chatId, text1);
        } catch (IOException | ParseException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
        Bot.getInstance().getUsersManager().setCanMakeRequest(id,true);
    }

    private boolean isNotAdmin(String id){
        return !Bot.getInstance().getConfig().isInAdminsList(id);
    }
    private boolean canMakeRequest(int id){
        return Bot.getInstance().getUsersManager().getUsers().get(id).canMakeRequest();
    }
}
