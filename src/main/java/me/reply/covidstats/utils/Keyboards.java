package me.reply.covidstats.utils;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class Keyboards {

    private final ReplyKeyboardMarkup mainKeyboard;
    private final ReplyKeyboardMarkup regionsKeyboard;
    private final ReplyKeyboardMarkup settingsKeyboard;
    private final ReplyKeyboardMarkup mainKeyboardProvince;

    private final ReplyKeyboardMarkup abruzzo;
    private final ReplyKeyboardMarkup basilicata;
    private final ReplyKeyboardMarkup bolzano;
    private final ReplyKeyboardMarkup calabria;
    private final ReplyKeyboardMarkup campania;
    private final ReplyKeyboardMarkup emilia_romagna;
    private final ReplyKeyboardMarkup friuli_venezia_giulia;
    private final ReplyKeyboardMarkup lazio;
    private final ReplyKeyboardMarkup liguria;
    private final ReplyKeyboardMarkup lombardia;
    private final ReplyKeyboardMarkup marche;
    private final ReplyKeyboardMarkup molise;
    private final ReplyKeyboardMarkup piemonte;
    private final ReplyKeyboardMarkup puglia;
    private final ReplyKeyboardMarkup sardegna;
    private final ReplyKeyboardMarkup sicilia;
    private final ReplyKeyboardMarkup toscana;
    private final ReplyKeyboardMarkup trento;
    private final ReplyKeyboardMarkup umbria;
    private final ReplyKeyboardMarkup valle_d_aosta;
    private final ReplyKeyboardMarkup veneto;

    public Keyboards(){
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
                .addText(EmojiParser.parseToUnicode(":office: Seleziona provincia"))
                .addText(EmojiParser.parseToUnicode(":page_facing_up: Autocertificazione"))
                .addText(EmojiParser.parseToUnicode(":ok: Consigli per l'epidemia"))
                .row()
                .addText(EmojiParser.parseToUnicode(":wrench: Impostazioni"))
                .addText(EmojiParser.parseToUnicode(":computer: Codice sorgente"))
                .build();

        mainKeyboardProvince = ReplyKeyboardBuilder.createReply()
                .row()
                .addText(EmojiParser.parseToUnicode(":bangbang: Casi"))
                .addText(EmojiParser.parseToUnicode(":office: Seleziona provincia"))
                .row()
                .addText(EmojiParser.parseToUnicode(":page_facing_up: Autocertificazione"))
                .addText(EmojiParser.parseToUnicode(":ok: Consigli per l'epidemia"))
                .row()
                .addText(EmojiParser.parseToUnicode(":wrench: Impostazioni"))
                .addText(EmojiParser.parseToUnicode(":computer: Codice sorgente"))
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

        abruzzo = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Chieti")
                .addText("L'Aquila")
                .row()
                .addText("Pescara")
                .addText("Teramo")
                .row()
                .addText("Nessuna provincia")
                .build();

        basilicata = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Matera")
                .addText("Potenza")
                .row()
                .addText("Nessuna provincia")
                .build();
        bolzano = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Bolzano")
                .row()
                .addText("Nessuna provincia")
                .build();

        calabria = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Catanzaro")
                .addText("Cosenza")
                .addText("Crotone")
                .row()
                .addText("Reggio di Calabria")
                .addText("Vibo Valentia")
                .row()
                .addText("Nessuna provincia")
                .build();

        campania = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Avellino")
                .addText("Benevento")
                .addText("Caserta")
                .row()
                .addText("Napoli")
                .addText("Salerno")
                .row()
                .addText("Nessuna provincia")
                .build();

        emilia_romagna = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Bologna")
                .addText("Ferrara")
                .addText("Forlì-Cesena")
                .row()
                .addText("Modena")
                .addText("Parma")
                .row()
                .addText("Piacenza")
                .addText("Ravenna")
                .row()
                .addText("Reggio nell'Emilia")
                .addText("Rimini")
                .row()
                .addText("Nessuna provincia")
                .build();

        friuli_venezia_giulia = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Gorizia")
                .addText("Pordenone")
                .row()
                .addText("Trieste")
                .addText("Udine")
                .row()
                .addText("Nessuna provincia")
                .build();

        lazio = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Frosinone")
                .addText("Latina")
                .addText("Rieti")
                .row()
                .addText("Roma")
                .addText("Viterbo")
                .row()
                .addText("Nessuna provincia")
                .build();

        liguria = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Genova")
                .addText("Imperia")
                .row()
                .addText("La Spezia")
                .addText("Savona")
                .row()
                .addText("Nessuna provincia")
                .build();

        lombardia = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Bergamo")
                .addText("Brescia")
                .row()
                .addText("Como")
                .addText("Cremona")
                .row()
                .addText("Lecco")
                .addText("Lodi")
                .row()
                .addText("Mantova")
                .addText("Milano")
                .row()
                .addText("Monza e della Brianza")
                .addText("Pavia")
                .row()
                .addText("Sondrio")
                .addText("Varese")
                .row()
                .addText("Nessuna provincia")
                .build();

        marche = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Ancona")
                .addText("Ascoli Piceno")
                .addText("Fermo")
                .row()
                .addText("Macerata")
                .addText("Pesaro e Urbino")
                .row()
                .addText("Nessuna provincia")
                .build();

        molise = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Campobasso")
                .row()
                .addText("Isernia")
                .row()
                .addText("Nessuna provincia")
                .build();

        piemonte = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Alessandria")
                .addText("Asti")
                .row()
                .addText("Biella")
                .addText("Cuneo")
                .row()
                .addText("Novara")
                .addText("Torino")
                .row()
                .addText("Verbano-Cusio-Ossola")
                .addText("Vercelli")
                .row()
                .addText("Nessuna provincia")
                .build();

        puglia = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Bari")
                .addText("Barletta-Andria-Trani")
                .row()
                .addText("Brindisi")
                .addText("Foggia")
                .row()
                .addText("Lecce")
                .addText("Taranto")
                .row()
                .addText("Nessuna provincia")
                .build();

        sardegna = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Cagliari")
                .addText("Carbonia-Iglesias")
                .row()
                .addText("Medio Campidano")
                .addText("Nuoro")
                .row()
                .addText("Ogliastra")
                .addText("Olbia-Tempio")
                .row()
                .addText("Oristano")
                .addText("Sassari")
                .row()
                .addText("Nessuna provincia")
                .build();

        sicilia = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Agrigento")
                .addText("Caltanissetta")
                .addText("Catania")
                .row()
                .addText("Enna")
                .addText("Messina")
                .row()
                .addText("Palermo")
                .addText("Ragusa")
                .row()
                .addText("Siracusa")
                .addText("Trapani")
                .row()
                .addText("Nessuna provincia")
                .build();

        toscana = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Arezzo")
                .addText("Firenze")
                .row()
                .addText("Grosseto")
                .addText("Livorno")
                .row()
                .addText("Lucca")
                .addText("Massa-Carrara")
                .row()
                .addText("Pisa")
                .addText("Pistoia")
                .row()
                .addText("Prato")
                .addText("Siena")
                .row()
                .addText("Nessuna provincia")
                .build();

        trento = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Trento")
                .row()
                .addText("Nessuna provincia")
                .build();

        umbria = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Perugia")
                .addText("Terni")
                .row()
                .addText("Nessuna provincia")
                .build();

        valle_d_aosta = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Aosta")
                .row()
                .addText("Nessuna provincia")
                .build();

        veneto = ReplyKeyboardBuilder.createReply()
                .row()
                .addText("Belluno")
                .addText("Padova")
                .addText("Rovigo")
                .row()
                .addText("Treviso")
                .addText("Venezia")
                .row()
                .addText("Verona")
                .addText("Vicenza")
                .row()
                .addText("Nessuna provincia")
                .build();

        settingsKeyboard = ReplyKeyboardBuilder.createReply()
                .row()
                .addText(EmojiParser.parseToUnicode(":bell: Notifiche abilitate"))
                .addText(EmojiParser.parseToUnicode(":no_bell: Notifiche disabilitate"))
                .row()
                .addText("Torna indietro")
                .build();
    }

    public ReplyKeyboardMarkup getMainKeyboard() {
        return mainKeyboard;
    }
    public ReplyKeyboardMarkup getRegionsKeyboard() {
        return regionsKeyboard;
    }
    public ReplyKeyboardMarkup getSettingsKeyboard() {
        return settingsKeyboard;
    }
    public ReplyKeyboardMarkup getMainKeyboardProvince() {
        return mainKeyboardProvince;
    }
    public ReplyKeyboardMarkup getAbruzzo() {
        return abruzzo;
    }
    public ReplyKeyboardMarkup getBasilicata() {
        return basilicata;
    }
    public ReplyKeyboardMarkup getBolzano() {
        return bolzano;
    }
    public ReplyKeyboardMarkup getCalabria() {
        return calabria;
    }
    public ReplyKeyboardMarkup getCampania() {
        return campania;
    }
    public ReplyKeyboardMarkup getEmilia_romagna() {
        return emilia_romagna;
    }
    public ReplyKeyboardMarkup getFriuli_venezia_giulia() {
        return friuli_venezia_giulia;
    }
    public ReplyKeyboardMarkup getLazio() {
        return lazio;
    }
    public ReplyKeyboardMarkup getLiguria() {
        return liguria;
    }
    public ReplyKeyboardMarkup getLombardia() {
        return lombardia;
    }
    public ReplyKeyboardMarkup getMarche() {
        return marche;
    }
    public ReplyKeyboardMarkup getMolise() {
        return molise;
    }
    public ReplyKeyboardMarkup getPiemonte() {
        return piemonte;
    }
    public ReplyKeyboardMarkup getPuglia() {
        return puglia;
    }
    public ReplyKeyboardMarkup getSardegna() {
        return sardegna;
    }
    public ReplyKeyboardMarkup getSicilia() {
        return sicilia;
    }
    public ReplyKeyboardMarkup getToscana() {
        return toscana;
    }
    public ReplyKeyboardMarkup getTrento() {
        return trento;
    }
    public ReplyKeyboardMarkup getUmbria() {
        return umbria;
    }
    public ReplyKeyboardMarkup getValle_d_aosta() {
        return valle_d_aosta;
    }
    public ReplyKeyboardMarkup getVeneto() {
        return veneto;
    }
}
