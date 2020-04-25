package me.reply.covidstats;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;

public class DataFetcher {

    public static CovidData fetchData() throws IOException {
        File dataFile = new File("data.json");
        String GIT_URL = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json";
        FileUtils.copyURLToFile(new URL(GIT_URL),dataFile);

        Gson g = new Gson();


        JsonFile data = g.fromJson(FileUtils.readFileToString(dataFile,"UTF-8"),JsonFile.class);


        CovidData covidData = new CovidData();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for(JsonObject jsonObject : data.getData()){
            DayData dayData = new DayData(
                    jsonObject.getTotale_positivi(),
                    jsonObject.getDimessi_guariti(),
                    jsonObject.getDeceduti(),
                    jsonObject.getTamponi(),
                    dateTimeFormatter.format(jsonObject.getData())
            );
            covidData.add(dayData);
        }
        return covidData;
    }
}
