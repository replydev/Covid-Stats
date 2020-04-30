package me.reply.covidstats.data;

import com.google.gson.Gson;
import me.reply.covidstats.data.province.ProvinceCovidData;
import me.reply.covidstats.data.province.ProvinceDayData;
import me.reply.covidstats.data.province.ProvinceJsonObject;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DataFetcher {

    private static File italyFile;
    private static File regionFile;
    private static File provinceFile;

    private static JsonObject[] italyJsonObjects;
    private static RegionJsonObject[] regionsJsonObjects;
    private static ProvinceJsonObject[] provinceJsonObjects;

    private final static Logger logger = LoggerFactory.getLogger(DataFetcher.class);

    public static void downloadFiles() throws IOException {
        if(italyFile == null)
            italyFile = new File("data.json");
        else if(italyFile.exists())
            FileUtils.forceDelete(italyFile);

        if(regionFile == null)
            regionFile = new File("region.json");
        else if(regionFile.exists())
            FileUtils.forceDelete(regionFile);

        if(provinceFile == null)
            provinceFile = new File("provice.json");
        else if(provinceFile.exists())
            FileUtils.forceDelete(provinceFile);

        final String ITALY_URL = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json";
        FileUtils.copyURLToFile(new URL(ITALY_URL), italyFile);

        final String REGIONS_URL = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-regioni.json";
        FileUtils.copyURLToFile(new URL(REGIONS_URL),regionFile);

        final String PROVINCE_URL = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-province.json";
        FileUtils.copyURLToFile(new URL(PROVINCE_URL), provinceFile);
        parseFiles();
    }

    public static boolean updateFiles() throws IOException {
        if(italyFile == null){
            downloadFiles();
            return true;
        }
        else if(!italyFile.exists()){
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

        if(provinceFile == null){
            downloadFiles();
            return true;
        }
        else if(!provinceFile.exists()){
            downloadFiles();
            return true;
        }

        File tempDataFile = new File("tempDataFile.json");
        File tempRegionFile = new File("tempRegionFile.json");
        File tempProvinceFile = new File("tempProvinceFile.json");

        final String ITALY_URL = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json";
        FileUtils.copyURLToFile(new URL(ITALY_URL),tempDataFile);

        final String REGIONS_URL = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-regioni.json";
        FileUtils.copyURLToFile(new URL(REGIONS_URL),tempRegionFile);

        final String PROVINCE_URL = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-province.json";
        FileUtils.copyURLToFile(new URL(PROVINCE_URL), tempProvinceFile);

        long datafileCRC32 = FileUtils.checksumCRC32(italyFile);
        long regionFileCRC32 = FileUtils.checksumCRC32(regionFile);
        long provinceFileCRC32 = FileUtils.checksumCRC32(provinceFile);
        long tempDataFileCRC32 = FileUtils.checksumCRC32(tempDataFile);
        long tempRegionFileCRC32 = FileUtils.checksumCRC32(tempRegionFile);
        long tempProvinceFileCRC32 = FileUtils.checksumCRC32(tempProvinceFile);

        if(datafileCRC32 != tempDataFileCRC32 || regionFileCRC32 != tempRegionFileCRC32 || provinceFileCRC32 != tempProvinceFileCRC32){  //files are different, ministero della sanità has updated the data
            FileUtils.forceDelete(italyFile);
            FileUtils.forceDelete(regionFile);
            FileUtils.forceDelete(provinceFile);
            FileUtils.moveFile(tempDataFile, italyFile);
            FileUtils.moveFile(tempRegionFile,regionFile);
            FileUtils.moveFile(tempProvinceFile,provinceFile);
            parseFiles();
            return true;
        }
        FileUtils.forceDelete(tempDataFile);
        FileUtils.forceDelete(tempRegionFile);
        FileUtils.forceDelete(tempProvinceFile);
        return false;
    }

    private static void parseFiles() throws IOException {
        Gson g = new Gson();
        logger.info("Leggo i file json.");
        italyJsonObjects = g.fromJson(FileUtils.readFileToString(italyFile,"UTF-8"),JsonObject[].class);
        regionsJsonObjects = g.fromJson(FileUtils.readFileToString(regionFile,"UTF-8"),RegionJsonObject[].class);
        provinceJsonObjects = g.fromJson(FileUtils.readFileToString(provinceFile,"UTF-8"),ProvinceJsonObject[].class);
        logger.info("Fatto");
    }


    public static CovidData fetchData() {
        CovidData covidData = new CovidData();
        for(JsonObject jsonObject : italyJsonObjects){
            DayData dayData = new DayData(
                    jsonObject.getTotale_positivi(),
                    jsonObject.getDimessi_guariti(),
                    jsonObject.getDeceduti(),
                    jsonObject.getTamponi(),
                    getGoodDate(jsonObject.getData())
            );
            covidData.add(dayData);
        }
        return covidData;
    }

    public static CovidData fetchData(String region)  {
        if(region == null)
            return fetchData();
        CovidData covidData = new CovidData();
        for(RegionJsonObject jsonObject : regionsJsonObjects){
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

    public static ProvinceCovidData fetchProvinceData(String provice) {
        ProvinceCovidData provinceCovidData = new ProvinceCovidData();
        for(ProvinceJsonObject object : provinceJsonObjects){
            if(object.getDenominazione_provincia().equalsIgnoreCase(provice)){
                ProvinceDayData provinceDayData = new ProvinceDayData(object.getTotale_casi(),getGoodDate(object.getData()));
                provinceCovidData.add(provinceDayData);
            }
        }
        return provinceCovidData;
    }

    private static String getGoodDate(String jsonDate){
        jsonDate = jsonDate.substring(0,10); //2020-02-24
        String [] split = jsonDate.split("-");
        assert split.length == 3;
        return split[2] + "-" + split[1] + "-" + split[0];
    }
}
