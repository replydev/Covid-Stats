package me.reply.covidstats.data;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class RegionJsonObject extends JsonObject {
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
