package me.reply.covidstats.data;

@SuppressWarnings("unused")
public class JsonObject{
    private String data;
    private String stato;
    private int ricoverati_con_sintomi;
    private int terapia_intensiva;
    private int totale_ospedalizzati;
    private int totale_positivi;
    private int variazione_totale_positivi;
    private int nuovi_positivi;
    private int dimessi_guariti;
    private int deceduti;
    private int totale_casi;
    private int tamponi;
    private int casi_testati;
    private String note_it;
    private String note_end;

    public String getData() {
        return data;
    }

    public String getStato() {
        return stato;
    }

    public int getRicoverati_con_sintomi() {
        return ricoverati_con_sintomi;
    }

    public int getTerapia_intensiva() {
        return terapia_intensiva;
    }

    public int getTotale_ospedalizzati() {
        return totale_ospedalizzati;
    }

    public int getTotale_positivi() {
        return totale_positivi;
    }

    public int getVariazione_totale_positivi() {
        return variazione_totale_positivi;
    }

    public int getNuovi_positivi() {
        return nuovi_positivi;
    }

    public int getDimessi_guariti() {
        return dimessi_guariti;
    }

    public int getDeceduti() {
        return deceduti;
    }

    public int getTotale_casi() {
        return totale_casi;
    }

    public int getTamponi() {
        return tamponi;
    }

    public int getCasi_testati() {
        return casi_testati;
    }

    public String getNote_it() {
        return note_it;
    }

    public String getNote_end() {
        return note_end;
    }
}
