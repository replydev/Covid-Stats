package me.reply.covidstats.data;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DataFetcher {

    private static File dataFile;
    private static File regionFile;

    private final static Logger logger = LoggerFactory.getLogger(DataFetcher.class);

    public static void downloadFiles() throws IOException {
        if(dataFile == null)
            dataFile = new File("data.json");
        else if(dataFile.exists())
            FileUtils.forceDelete(dataFile);

        if(regionFile == null)
            regionFile = new File("region.json");
        else if(regionFile.exists())
            FileUtils.forceDelete(regionFile);

        final String ITALY_URL = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json";
        FileUtils.copyURLToFile(new URL(ITALY_URL),dataFile);

        final String REGIONS_URL = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-regioni.json";
        FileUtils.copyURLToFile(new URL(REGIONS_URL),regionFile);
    }

    public static CovidData fetchData() throws IOException {
        Gson g = new Gson();
        JsonObject[] object = g.fromJson(FileUtils.readFileToString(dataFile,"UTF-8"),JsonObject[].class);

        CovidData covidData = new CovidData();
        for(JsonObject jsonObject : object){
            DayData dayData = new DayData(
                    jsonObject.getTotale_positivi(),
                    jsonObject.getDimessi_guariti(),
                    jsonObject.getDeceduti(),
                    jsonObject.getTamponi(),
                    //dateTimeFormatter.format(jsonObject.getData())
                    getGoodDate(jsonObject.getData())
            );
            covidData.add(dayData);
        }
        return covidData;
    }

    public static CovidData fetchData(String region) throws IOException {
        if(region == null)
            return fetchData();
        Gson g = new Gson();
        RegionJsonObject[] object = g.fromJson(FileUtils.readFileToString(regionFile,"UTF-8"),RegionJsonObject[].class);

        CovidData covidData = new CovidData();
        for(RegionJsonObject jsonObject : object){
            if(jsonObject.getDenominazione_regione().equalsIgnoreCase(region)){
                DayData dayData = new DayData(
                        jsonObject.getTotale_positivi(),
                        jsonObject.getDimessi_guariti(),
                        jsonObject.getDeceduti(),
                        jsonObject.getTamponi(),
                        //dateTimeFormatter.format(jsonObject.getData())
                        getGoodDate(jsonObject.getData())
                );
                covidData.add(dayData);
            }
        }
        return covidData;
    }

    private static String getGoodDate(String jsonDate){
        jsonDate = jsonDate.substring(0,10); //2020-02-24
        String [] split = jsonDate.split("-");
        assert split.length == 3;
        return split[2] + "-" + split[1] + "-" + split[0];
    }
}
