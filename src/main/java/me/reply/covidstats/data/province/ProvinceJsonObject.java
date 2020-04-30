package me.reply.covidstats.data.province;

import com.google.gson.annotations.SerializedName;

public class ProvinceJsonObject {

    /*
    * "data": "2020-02-24T18:00:00",
        "stato": "ITA",
        "codice_regione": 13,
        "denominazione_regione": "Abruzzo",
        "codice_provincia": 69,
        "denominazione_provincia": "Chieti",
        "sigla_provincia": "CH",
        "lat": 42.35103167,
        "long": 14.16754574,
        "totale_casi": 0,
        "note_it": "",
        "note_en": ""*/


    private String data;
    private String stato;
    private int codice_regione;
    private String denominazione_regione;
    private int codice_provincia;
    private String denominazione_provincia;
    private String sigla_provincia;
    private double lat;
    @SerializedName("long")
    private double longi;
    private int totale_casi;
    private String note_it;
    private String note_en;

    public String getData() {
        return data;
    }

    public String getStato() {
        return stato;
    }

    public int getCodice_regione() {
        return codice_regione;
    }

    public String getDenominazione_regione() {
        return denominazione_regione;
    }

    public int getCodice_provincia() {
        return codice_provincia;
    }

    public String getDenominazione_provincia() {
        return denominazione_provincia;
    }

    public String getSigla_provincia() {
        return sigla_provincia;
    }

    public double getLat() {
        return lat;
    }

    public double getLongi() {
        return longi;
    }

    public int getTotale_casi() {
        return totale_casi;
    }

    public String getNote_it() {
        return note_it;
    }

    public String getNote_en() {
        return note_en;
    }
}
