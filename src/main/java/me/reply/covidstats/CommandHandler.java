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
        switch (commandAliases) {
            case "/start" -> threads.submit(() -> sendMainKeyboard(userId, chatId));
            case ":warning: Attualmente contagiati" -> threads.submit(() -> infectedJob(
                    Bot.getInstance().getUsersManager().getIdFromUser(userId),
                    Bot.getInstance().getUsersManager().getRegionFromUser(userId),
                    Bot.getInstance().getUsersManager().getProvinceFromUser(userId),
                    chatId)
            );
            case ":diamond_shape_with_a_dot_inside: Guariti" -> threads.submit(() -> recoveredJob(
                    Bot.getInstance().getUsersManager().getIdFromUser(userId),
                    Bot.getInstance().getUsersManager().getRegionFromUser(userId),
                    Bot.getInstance().getUsersManager().getProvinceFromUser(userId),
                    chatId)
            );
            case ":angel: Decessi" -> threads.submit(() -> deathsJob(
                    Bot.getInstance().getUsersManager().getIdFromUser(userId),
                    Bot.getInstance().getUsersManager().getRegionFromUser(userId),
                    Bot.getInstance().getUsersManager().getProvinceFromUser(userId),
                    chatId)
            );
            case ":bangbang: Casi" -> threads.submit(() -> casesJob(
                    Bot.getInstance().getUsersManager().getIdFromUser(userId),
                    Bot.getInstance().getUsersManager().getRegionFromUser(userId),
                    Bot.getInstance().getUsersManager().getProvinceFromUser(userId),
                    chatId)
            );
            case ":syringe: Tamponi" -> threads.submit(() -> tamponsJob(
                    Bot.getInstance().getUsersManager().getIdFromUser(userId),
                    Bot.getInstance().getUsersManager().getRegionFromUser(userId),
                    Bot.getInstance().getUsersManager().getProvinceFromUser(userId),
                    chatId)
            );
            case ":sick: Ricoverati con sintomi" -> threads.submit(() -> hospitalizedWithSymptomsJob(
                    Bot.getInstance().getUsersManager().getIdFromUser(userId),
                    Bot.getInstance().getUsersManager().getRegionFromUser(userId),
                    Bot.getInstance().getUsersManager().getProvinceFromUser(userId),
                    chatId)
            );
            case ":microbe: Terapia intensiva" -> threads.submit(() -> intensiveThreapyJob(
                    Bot.getInstance().getUsersManager().getIdFromUser(userId),
                    Bot.getInstance().getUsersManager().getRegionFromUser(userId),
                    Bot.getInstance().getUsersManager().getProvinceFromUser(userId),
                    chatId)
            );
            case ":hospital: Totale ospedalizzati" -> threads.submit(() -> totalHospitalizedJob(
                    Bot.getInstance().getUsersManager().getIdFromUser(userId),
                    Bot.getInstance().getUsersManager().getRegionFromUser(userId),
                    Bot.getInstance().getUsersManager().getProvinceFromUser(userId),
                    chatId)
            );
            case ":house: Isolamento domiciliare" -> threads.submit(() -> householdIsolationJob(
                    Bot.getInstance().getUsersManager().getIdFromUser(userId),
                    Bot.getInstance().getUsersManager().getRegionFromUser(userId),
                    Bot.getInstance().getUsersManager().getProvinceFromUser(userId),
                    chatId)
            );
            case ":mount_fuji: Seleziona regione" -> switchToRegionsKeyboard(userId, chatId);
            case ":office: Seleziona provincia" -> switchToProvinceKeyboard(userId, chatId);
            case ":computer: Codice sorgente" -> threads.submit(() -> sendMessage(EmojiParser.parseToUnicode(":smile_cat: Sviluppato da @zreply.\n:page_facing_up: Il codice sorgente di questo software è open source, qualsiasi modifica utile ed appropriata è la benvenuta!\n:link: https://github.com/replydev/Covid-Stats"), chatId));
            case "Abruzzo", "Basilicata", "P.A. Bolzano", "Calabria", "Campania", "Emilia-Romagna", "Friuli Venezia Giulia", "Italia", "Lazio", "Liguria", "Lombardia", "Marche", "Molise", "Piemonte", "Puglia", "Sardegna", "Sicilia", "Toscana", "P.A. Trento", "Umbria", "Valle d'Aosta", "Veneto" -> threads.submit(() -> {
                Bot.getInstance().getUsersManager().setRegion(userId, command);
                sendMessage("Hai selezionato una nuova regione: " + command, chatId);
                sendMainKeyboard(userId, chatId);
            });
            case "Nessuna provincia", "Agrigento", "Alessandria", "Ancona", "Aosta", "Arezzo", "Ascoli Piceno", "Asti", "Avellino", "Bari", "Barletta-Andria-Trani", "Belluno", "Benevento", "Bergamo", "Biella", "Bologna", "Bolzano", "Brescia", "Brindisi", "Cagliari", "Caltanissetta", "Campobasso", "Carbonia-Iglesias", "Caserta", "Catania", "Catanzaro", "Chieti", "Como", "Cosenza", "Cremona", "Crotone", "Cuneo", "Enna", "Fermo", "Ferrara", "Firenze", "Foggia", "Forlì-Cesena", "Frosinone", "Genova", "Gorizia", "Grosseto", "Imperia", "Isernia", "L'Aquila", "La Spezia", "Latina", "Lecce", "Lecco", "Livorno", "Lodi", "Lucca", "Macerata", "Mantova", "Massa-Carrara", "Matera", "Medio Campidano", "Messina", "Milano", "Modena", "Monza e della Brianza", "Napoli", "Novara", "Nuoro", "Ogliastra", "Olbia-Tempio", "Oristano", "Padova", "Palermo", "Parma", "Pavia", "Perugia", "Pesaro e Urbino", "Pescara", "Piacenza", "Pisa", "Pistoia", "Pordenone", "Potenza", "Prato", "Ragusa", "Ravenna", "Reggio di Calabria", "Reggio nell'Emilia", "Rieti", "Rimini", "Roma", "Rovigo", "Salerno", "Sassari", "Savona", "Siena", "Siracusa", "Sondrio", "Taranto", "Teramo", "Terni", "Torino", "Trapani", "Trento", "Treviso", "Trieste", "Udine", "Varese", "Venezia", "Verbano-Cusio-Ossola", "Vercelli", "Verona", "Vibo Valentia", "Vicenza", "Viterbo" -> threads.submit(() -> {
                String regionToSet = getRegionFromProvince(command); //Milano -> Lombardia
                if (regionToSet != null) {
                    Bot.getInstance().getUsersManager().setRegion(userId, regionToSet);
                    Bot.getInstance().getUsersManager().setProvince(userId, command);
                } else {
                    Bot.getInstance().getUsersManager().setProvince(userId, "Nessuna provincia");
                }
                sendMessage("Hai selezionato una nuova provincia: " + command, chatId);
                sendMainKeyboard(userId, chatId);
            });
            case "Torna indietro" -> sendMainKeyboard(userId, chatId);
            case ":wrench: Impostazioni" -> switchToSettingsKeyboard(chatId);
            case ":bell: Notifiche abilitate" -> threads.submit(() -> {
                Bot.getInstance().getUsersManager().setNotification(userId, true);
                sendMessage((":white_check_mark: Hai abilitato nel notifiche giornaliere"), chatId);
            });
            case ":no_bell: Notifiche disabilitate" -> threads.submit(() -> {
                Bot.getInstance().getUsersManager().setNotification(userId, false);
                sendMessage((":x: Hai disabilitato nel notifiche giornaliere"), chatId);
            });
            case "/update" -> threads.submit(() -> {
                if (isNotAdmin(userId)) {
                    sendMessage((":x: Comando riservato"), chatId);
                    return;
                }
                try {
                    if (DataFetcher.updateFiles()) {
                        ChartUtils.clearCache();
                        sendMessage("Operazione completata con successo.", chatId);
                    } else
                        sendMessage("Non ho trovato aggiornamenti.", chatId);
                } catch (IOException e) {
                    System.err.println("Si è verificato un errore, verifica nel file di log");
                    logger.error(e.toString());
                }
            });
            case "/stop" -> threads.submit(() -> {
                if (isNotAdmin(userId)) {
                    sendMessage((":x: Comando riservato"), chatId);
                    return;
                }
                logger.info("Un username ha inviato il comando di chiusura, sto terminando il programma...");
                sendMessage("Sto terminando il bot", chatId);
                System.exit(0); //when i shutdown i call sigint runnable
            });
            case "/confirm" -> threads.submit(() -> {
                if (isNotAdmin(userId)) {
                    sendMessage((":x: Comando riservato"), chatId);
                    return;
                }
                String text = Bot.getInstance().getUsersManager().getNotificationTextFromUser(userId);
                if (text == null) {
                    sendMessage((":x: Nessun messaggio da inviare!"), chatId);
                    return;
                }
                sendMessage("Sto inviando la tua notifica a tutti gli utenti...", chatId);
                Bot.getInstance().messageToAllUsers(text);
                Bot.getInstance().getUsersManager().setNotificationText(userId, null);
            });
            default -> threads.submit(() -> {
                if (isNotAdmin(userId)) {
                    return;
                }
                Bot.getInstance().getUsersManager().setNotificationText(userId, EmojiParser.parseToUnicode(command));
                sendMessage("Ok, digita /confirm per inviare \"" + Bot.getInstance().getUsersManager().getNotificationTextFromUser(userId) + "\" a tutti gli utenti", chatId);
            });
        }
    }

    private String getRegionFromProvince(String province) {
        return switch (province) {
            case "Chieti", "L'Aquila", "Pescara", "Teramo" -> "Abruzzo";
            case "Matera", "Potenza" -> "Basilicata";
            case "Bolzano" -> "P.A. Bolzano";
            case "Catanzaro", "Cosenza", "Crotone", "Reggio di Calabria", "Vibo Valentia" -> "Calabria";
            case "Avellino", "Benevento", "Caserta", "Napoli", "Salerno" -> "Campania";
            case "Bologna", "Ferrara", "Forlì-Cesena", "Cesena", "Modena", "Parma", "Piacenza", "Reggio nell'Emilia", "Ravenna", "Rimini" -> "Emilia-Romagna";
            case "Gorizia", "Pordenone", "Trieste", "Udine" -> "Friuli Venezia Giulia";
            case "Frosinone", "Latina", "Rieti", "Roma", "Viterbo" -> "Lazio";
            case "Genova", "Imperia", "La Spezia", "Savona" -> "Liguria";
            case "Bergamo", "Brescia", "Como", "Cremona", "Lecco", "Lodi", "Mantova", "Milano", "Monza e della Brianza", "Pavia", "Sondrio", "Varese" -> "Lombardia";
            case "Ancona", "Ascoli Piceno", "Fermo", "Macerata", "Pesaro e Urbino" -> "Marche";
            case "Campobasso", "Isernia" -> "Molise";
            case "Alessandria", "Asti", "Biella", "Cuneo", "Novara", "Torino", "Verbano-Cusio-Ossola", "Vercelli" -> "Piemonte";
            case "Bari", "Barletta-Andria-Trani", "Brindisi", "Foggia", "Lecce", "Taranto" -> "Puglia";
            case "Cagliari", "Carbonia-Iglesias", "Medio Campidano", "Nuoro", "Ogliastra", "Olbia-Tempio", "Oristano", "Sassari" -> "Sardegna";
            case "Agrigento", "Caltanissetta", "Catania", "Enna", "Messina", "Palermo", "Ragusa", "Siracusa", "Trapani" -> "Sicilia";
            case "Arezzo", "Firenze", "Grosseto", "Livorno", "Lucca", "Massa-Carrara", "Pisa", "Pistoia", "Prato", "Siena" -> "Toscana";
            case "Trento" -> "P.A. Trento";
            case "Perugia", "Terni" -> "Umbria";
            case "Aosta" -> "Valle d'Aosta";
            case "Belluno", "Padova", "Rovigo", "Treviso", "Venezia", "Verona", "Vicenza" -> "Veneto";
            default -> null;
        };
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
        switch (region) {
            case "Abruzzo" -> keyboard = keyboard.setReplyMarkup(keyboards.getAbruzzo());
            case "Basilicata" -> keyboard = keyboard.setReplyMarkup(keyboards.getBasilicata());
            case "P.A. Bolzano" -> keyboard = keyboard.setReplyMarkup(keyboards.getBolzano());
            case "Calabria" -> keyboard = keyboard.setReplyMarkup(keyboards.getCalabria());
            case "Campania" -> keyboard = keyboard.setReplyMarkup(keyboards.getCampania());
            case "Emilia-Romagna" -> keyboard = keyboard.setReplyMarkup(keyboards.getEmilia_romagna());
            case "Friuli Venezia Giulia" -> keyboard = keyboard.setReplyMarkup(keyboards.getFriuli_venezia_giulia());
            case "Lazio" -> keyboard = keyboard.setReplyMarkup(keyboards.getLazio());
            case "Liguria" -> keyboard = keyboard.setReplyMarkup(keyboards.getLiguria());
            case "Lombardia" -> keyboard = keyboard.setReplyMarkup(keyboards.getLombardia());
            case "Marche" -> keyboard = keyboard.setReplyMarkup(keyboards.getMarche());
            case "Molise" -> keyboard = keyboard.setReplyMarkup(keyboards.getMolise());
            case "Piemonte" -> keyboard = keyboard.setReplyMarkup(keyboards.getPiemonte());
            case "Puglia" -> keyboard = keyboard.setReplyMarkup(keyboards.getPuglia());
            case "Sardegna" -> keyboard = keyboard.setReplyMarkup(keyboards.getSardegna());
            case "Sicilia" -> keyboard = keyboard.setReplyMarkup(keyboards.getSicilia());
            case "Toscana" -> keyboard = keyboard.setReplyMarkup(keyboards.getToscana());
            case "P.A. Trento" -> keyboard = keyboard.setReplyMarkup(keyboards.getTrento());
            case "Umbria" -> keyboard = keyboard.setReplyMarkup(keyboards.getUmbria());
            case "Valle d'Aosta" -> keyboard = keyboard.setReplyMarkup(keyboards.getValle_d_aosta());
            case "Veneto" -> keyboard = keyboard.setReplyMarkup(keyboards.getVeneto());
            default -> sendMessage((":x: Errore: regione non valida (" + region + ")"), chatId);
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
        if(canMakeRequest(id)){
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
        if(canMakeRequest(id)){
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
        if(canMakeRequest(id)){
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
        if(canMakeRequest(id)){
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
        if(canMakeRequest(id)){
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

    private void hospitalizedWithSymptomsJob(int id,String region,String province,long chatId){
        if(canMakeRequest(id)){
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
            File f = data.hospitalizedWithSymptomsGraph(region);
            String text,text1;
            if(region == null){
                text = "Ultimi dati: " + DataFetcher.getItalyHospitalizedWithSymptoms();
                text1 = "Ultimi dati: " + DataFetcher.getItalyNewHospitalizedWithSymptoms();
            }

            else{
                text = "Ultimi dati: " + DataFetcher.getRegionHospitalizedWithSymptoms(region);
                text1 = "Ultimi dati: " + DataFetcher.getRegionNewHospitalizedWithSymptoms(region);
            }
            sendPhoto(f,chatId, text);
            f = data.newHospitalizedWithSymptomsGraph(region);
            sendPhoto(f,chatId, text1);
        } catch (IOException | ParseException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
        Bot.getInstance().getUsersManager().setCanMakeRequest(id,true);
    }

    private void intensiveThreapyJob(int id,String region,String province,long chatId){
        if(canMakeRequest(id)){
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
            File f = data.intensive_therapyGraph(region);
            String text,text1;
            if(region == null){
                text = "Ultimi dati: " + DataFetcher.getItalyIntensiveTherapy();
                text1 = "Ultimi dati: " + DataFetcher.getItalyNewIntensiveTherapy();
            }

            else{
                text = "Ultimi dati: " + DataFetcher.getRegionIntensiveTherapy(region);
                text1 = "Ultimi dati: " + DataFetcher.getRegionNewIntensiveTherapy(region);
            }
            sendPhoto(f,chatId, text);
            f = data.newIntensive_therapy(region);
            sendPhoto(f,chatId, text1);
        } catch (IOException | ParseException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
        Bot.getInstance().getUsersManager().setCanMakeRequest(id,true);
    }

    private void totalHospitalizedJob(int id,String region,String province,long chatId){
        if(canMakeRequest(id)){
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
            File f = data.totalHospitalized(region);
            String text,text1;
            if(region == null){
                text = "Ultimi dati: " + DataFetcher.getItalyTotalHospitalized();
                text1 = "Ultimi dati: " + DataFetcher.getItalyNewTotalHospitalized();
            }

            else{
                text = "Ultimi dati: " + DataFetcher.getRegionTotalHospitalized(region);
                text1 = "Ultimi dati: " + DataFetcher.getRegionNewTotalHospitalized(region);
            }
            sendPhoto(f,chatId, text);
            f = data.newTotalHospitalized(region);
            sendPhoto(f,chatId, text1);
        } catch (IOException | ParseException e) {
            System.err.println("Si è verificato un errore, verifica nel file di log");
            logger.error(e.toString());
        }
        Bot.getInstance().getUsersManager().setCanMakeRequest(id,true);
    }

    private void householdIsolationJob(int id,String region,String province,long chatId){
        if(canMakeRequest(id)){
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
            File f = data.householdIsolation(region);
            String text,text1;
            if(region == null){
                text = "Ultimi dati: " + DataFetcher.getItalyHouseholdIsolation();
                text1 = "Ultimi dati: " + DataFetcher.getItalyNewHouseholdIsolation();
            }

            else{
                text = "Ultimi dati: " + DataFetcher.getRegionHouseholdIsolation(region);
                text1 = "Ultimi dati: " + DataFetcher.getRegionNewHouseholdIsolation(region);
            }
            sendPhoto(f,chatId, text);
            f = data.newHouseholdIsolation(region);
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
        return !Bot.getInstance().getUsersManager().getUsers().get(id).canMakeRequest();
    }
}
