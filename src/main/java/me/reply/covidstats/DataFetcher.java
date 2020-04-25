package me.reply.covidstats;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DataFetcher {

    public static CovidData fetchData() throws IOException {
        File dataFile = new File("data.json");
        String GIT_URL = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json";
        FileUtils.copyURLToFile(new URL(GIT_URL),dataFile);

        Gson g = new Gson();

        JsonObject[] object = new JsonObject[100];
        try{
             object = g.fromJson(FileUtils.readFileToString(dataFile,"UTF-8"),JsonObject[].class);
        }catch (Exception e){
            e.printStackTrace();
        }

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

    private static String getGoodDate(String jsonDate){
        jsonDate = jsonDate.substring(0,10); //2020-02-24
        String [] split = jsonDate.split("-");
        assert split.length == 3;
        return split[2] + "-" + split[1] + "-" + split[0];
    }
}
