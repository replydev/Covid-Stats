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

    private static JsonObject lastItaly;

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
        lastItaly = italyJsonObjects[italyJsonObjects.length - 1];
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

    public static int getItalyCurrentlyInfected(){
        return lastItaly.getTotale_positivi();
    }

    public static int getItalyNewCurrentlyInfected(){
        return lastItaly.getVariazione_totale_positivi();
    }

    public static int getItalyRecovered(){
        return lastItaly.getDimessi_guariti();
    }

    public static int getItalyNewRecovered(){
        return lastItaly.getDimessi_guariti() - italyJsonObjects[italyJsonObjects.length - 2].getDimessi_guariti();
    }

    public static int getItalyDeaths(){
        return lastItaly.getDeceduti();
    }

    public static int getItalyNewDeaths(){
        return lastItaly.getDeceduti() - italyJsonObjects[italyJsonObjects.length - 2].getDeceduti();
    }
    
    public static int getItalyTotalCases(){
        return lastItaly.getTotale_casi();
    }
    
    public static int getItalyNewCases(){
        return lastItaly.getNuovi_positivi();
    }
    
    public static int getItalyTampons(){
        return lastItaly.getTamponi();
    }
    
    public static int getItalyNewTampons(){
        return lastItaly.getTamponi() - italyJsonObjects[italyJsonObjects.length - 2].getTamponi();
    }

    public static int getRegionsCurrentlyInfected(String region){
        for(int i = regionsJsonObjects.length - 1; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region))
                return regionsJsonObjects[i].getTotale_positivi();
        }
        return -1;
    }

    public static int getRegionsNewCurrentlyInfected(String region){
        for(int i = regionsJsonObjects.length - 1; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region))
                return regionsJsonObjects[i].getVariazione_totale_positivi();
        }
        return -1;
    }

    public static int getRegionsRecovered(String region){
        for(int i = regionsJsonObjects.length - 1; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region))
                return regionsJsonObjects[i].getDimessi_guariti();
        }
        return -1;
    }

    public static int getRegionsNewRecovered(String region){
        int val1 = 0,val2 = 0;
        int i = regionsJsonObjects.length - 1;

        for(; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
                val1 = regionsJsonObjects[i].getDimessi_guariti();
                break;
            }
        }

        for(; i >= 0; i--){
            if(provinceJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
                val2 = regionsJsonObjects[i].getDimessi_guariti();
                break;
            }
        }

        return val1-val2;
    }

    public static int getRegionsDeaths(String region){
        for(int i = regionsJsonObjects.length - 1; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region))
                return regionsJsonObjects[i].getDeceduti();
        }
        return -1;
    }

    public static int getRegionsNewDeaths(String region){
        int val1 = 0,val2 = 0;
        int i = regionsJsonObjects.length - 1;

        for(; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
                val1 = regionsJsonObjects[i].getDeceduti();
                break;
            }
        }

        for(; i >= 0; i--){
            if(provinceJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
                val2 = regionsJsonObjects[i].getDeceduti();
                break;
            }
        }

        return val1-val2;
    }

    public static int getRegionsTotalCases(String region){
        for(int i = regionsJsonObjects.length - 1; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region))
                return regionsJsonObjects[i].getTotale_casi();
        }
        return -1;
    }

    public static int getRegionsNewCases(String region){
        for(int i = regionsJsonObjects.length - 1; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region))
                return regionsJsonObjects[i].getNuovi_positivi();
        }
        return -1;
    }

    public static int getRegionsTampons(String region){
        for(int i = regionsJsonObjects.length - 1; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region))
                return regionsJsonObjects[i].getTamponi();
        }
        return -1;
    }

    public static int getRegionsNewTampons(String region){
        int val1 = 0,val2 = 0;
        int i = regionsJsonObjects.length - 1;

        for(; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
                val1 = regionsJsonObjects[i].getTamponi();
                break;
            }
        }

        for(; i >= 0; i--){
            if(provinceJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
                val2 = regionsJsonObjects[i].getTamponi();
                break;
            }
        }

        return val1-val2;
    }

    public static int getprovinceTotalCases(String province){
        for(int i = provinceJsonObjects.length - 1; i >= 0; i--){
            if(provinceJsonObjects[i].getDenominazione_provincia().equalsIgnoreCase(province))
                return provinceJsonObjects[i].getTotale_casi();
        }
        return -1;
    }

    public static int getprovinceNewCases(String province){
        int val1 = 0,val2 = 0;
        int i = provinceJsonObjects.length - 1;

        for(; i >= 0; i--){
            if(provinceJsonObjects[i].getDenominazione_provincia().equalsIgnoreCase(province)){
                val1 = provinceJsonObjects[i].getTotale_casi();
                break;
            }
        }

        for(; i >= 0; i--){
            if(provinceJsonObjects[i].getDenominazione_provincia().equalsIgnoreCase(province)){
                val2 = provinceJsonObjects[i].getTotale_casi();
                break;
            }
        }

        return val1-val2;
    }
}
