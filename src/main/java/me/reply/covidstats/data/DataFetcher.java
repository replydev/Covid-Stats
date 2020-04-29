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

    public static boolean updateFiles() throws IOException {
        if(dataFile == null){
            downloadFiles();
            return true;
        }
        else if(!dataFile.exists()){
            downloadFiles();
            return true;
        }

        if(regionFile == null){
            downloadFiles();
            return true;
        }
        else if(!regionFile.exists()){
            downloadFiles();
            return true;
        }

        File tempDataFile = new File("tempDataFile.json");
        File tempRegionFile = new File("tempRegionFile.json");

        final String ITALY_URL = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json";
        FileUtils.copyURLToFile(new URL(ITALY_URL),tempDataFile);

        final String REGIONS_URL = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-regioni.json";
        FileUtils.copyURLToFile(new URL(REGIONS_URL),tempRegionFile);

        long datafileCRC32 = FileUtils.checksumCRC32(dataFile);
        long regionFileCRC32 = FileUtils.checksumCRC32(regionFile);
        long tempDataFileCRC32 = FileUtils.checksumCRC32(tempDataFile);
        long tempRegionFileCRC32 = FileUtils.checksumCRC32(tempRegionFile);

        if(datafileCRC32 != tempDataFileCRC32 || regionFileCRC32 != tempRegionFileCRC32){  //files are different, ministero della sanità has updated the data
            FileUtils.forceDelete(dataFile);
            FileUtils.forceDelete(regionFile);
            FileUtils.moveFile(tempDataFile,dataFile);
            FileUtils.moveFile(tempRegionFile,regionFile);
            return true;
        }
        FileUtils.forceDelete(tempDataFile);
        FileUtils.forceDelete(tempRegionFile);
        return false;
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
