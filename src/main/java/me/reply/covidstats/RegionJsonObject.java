package me.reply.covidstats;

import com.google.gson.annotations.SerializedName;

public class RegionJsonObject extends JsonObject{

    /*
        "codice_regione": 5,
        "denominazione_regione": "Veneto",
        "lat": 45.43490485,
        "long": 12.33845213
        */

    private int codice_regione;
    private String denominazione_regione;
    private double lat;

    @SerializedName("long")
    private double longi;

    public int getCodice_regione() {
        return codice_regione;
    }

    public String getDenominazione_regione() {
        return denominazione_regione;
    }

    public double getLat() {
        return lat;
    }

    public double getLongi() {
        return longi;
    }
}
