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
import java.util.Arrays;

public class DataFetcher {

    private static final File italyFile = new File("data/italy.json");
    private static final File regionFile = new File("data/region.json");
    private static final File provinceFile = new File("data/province.json");

    private static JsonObject[] italyJsonObjects;
    private static RegionJsonObject[] regionsJsonObjects;
    private static ProvinceJsonObject[] provinceJsonObjects;

    private static JsonObject lastItaly;

    private static final String ITALY_URL = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json";
    private static final String REGIONS_URL = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-regioni.json";
    private static final String PROVINCE_URL = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-province.json";

    public static void downloadFiles() throws IOException {
        if(italyFile.exists())
            FileUtils.forceDelete(italyFile);
        if(regionFile.exists())
            FileUtils.forceDelete(regionFile);
        if(provinceFile.exists())
            FileUtils.forceDelete(provinceFile);

        FileUtils.copyURLToFile(new URL(ITALY_URL), italyFile);
        FileUtils.copyURLToFile(new URL(REGIONS_URL),regionFile);
        FileUtils.copyURLToFile(new URL(PROVINCE_URL), provinceFile);
        loadData();
    }

    public static boolean updateFiles() throws IOException {
        if(!italyFile.exists()){
            downloadFiles();
            return true;
        }
        if(!regionFile.exists()){
            downloadFiles();
            return true;
        }
        if(!provinceFile.exists()){
            downloadFiles();
            return true;
        }

        File tempDataFile = new File("tempDataFile.json");
        File tempRegionFile = new File("tempRegionFile.json");
        File tempProvinceFile = new File("tempProvinceFile.json");

        FileUtils.copyURLToFile(new URL(ITALY_URL),tempDataFile);
        FileUtils.copyURLToFile(new URL(REGIONS_URL),tempRegionFile);
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
            return loadData();
        }
        FileUtils.forceDelete(tempDataFile);
        FileUtils.forceDelete(tempRegionFile);
        FileUtils.forceDelete(tempProvinceFile);
        return false;
    }

    public static boolean loadData() throws IOException {
        Gson g = new Gson();
        JsonObject[] italyJsonObjects_tmp = g.fromJson(FileUtils.readFileToString(italyFile,"UTF-8"),JsonObject[].class);
        RegionJsonObject[] regionsJsonObjects_tmp = g.fromJson(FileUtils.readFileToString(regionFile,"UTF-8"),RegionJsonObject[].class);
        ProvinceJsonObject[] provinceJsonObjects_tmp = g.fromJson(FileUtils.readFileToString(provinceFile,"UTF-8"),ProvinceJsonObject[].class);

        if(!Arrays.equals(italyJsonObjects_tmp,italyJsonObjects) || !Arrays.equals(regionsJsonObjects_tmp,regionsJsonObjects) || !Arrays.equals(provinceJsonObjects_tmp,provinceJsonObjects)){
            italyJsonObjects = italyJsonObjects_tmp;
            regionsJsonObjects = regionsJsonObjects_tmp;
            provinceJsonObjects = provinceJsonObjects_tmp;
            lastItaly = italyJsonObjects[italyJsonObjects.length - 1];
            return true;
        }
        else return false;
    }


    public static CovidData fetchData() {
        CovidData covidData = new CovidData();
        for(JsonObject jsonObject : italyJsonObjects){
            DayData dayData = new DayData(
                    jsonObject.getTotale_positivi(),
                    jsonObject.getDimessi_guariti(),
                    jsonObject.getDeceduti(),
                    jsonObject.getTamponi(),
                    jsonObject.getRicoverati_con_sintomi(),
                    jsonObject.getTerapia_intensiva(),
                    jsonObject.getTotale_ospedalizzati(),
                    jsonObject.getIsolamento_domiciliare(),
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
                        jsonObject.getRicoverati_con_sintomi(),
                        jsonObject.getTerapia_intensiva(),
                        jsonObject.getTotale_ospedalizzati(),
                        jsonObject.getIsolamento_domiciliare(),
                        //dateTimeFormatter.format(jsonObject.getData())
                        getGoodDate(jsonObject.getData())
                );
                covidData.add(dayData);
            }
        }
        return covidData;
    }

    public static ProvinceCovidData fetchProvinceData(String province) {
        ProvinceCovidData provinceCovidData = new ProvinceCovidData();
        for(ProvinceJsonObject object : provinceJsonObjects){
            if(object.getDenominazione_provincia().equalsIgnoreCase(province)){
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
                i--;
                break;
            }
        }

        for(; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
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
                i--;
                break;
            }
        }

        for(; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
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
                i--;
                break;
            }
        }

        for(; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
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
                i--;
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

    public static int getItalyHospitalizedWithSymptoms() {
        return lastItaly.getRicoverati_con_sintomi();
    }

    public static int getItalyNewHospitalizedWithSymptoms() {
        return lastItaly.getRicoverati_con_sintomi() - italyJsonObjects[italyJsonObjects.length - 2].getRicoverati_con_sintomi();
    }

    public static int getRegionHospitalizedWithSymptoms(String region) {
        for(int i = regionsJsonObjects.length - 1; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region))
                return regionsJsonObjects[i].getRicoverati_con_sintomi();
        }
        return -1;
    }

    public static int getRegionNewHospitalizedWithSymptoms(String region) {
        int val1 = 0,val2 = 0;
        int i = regionsJsonObjects.length - 1;

        for(; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
                val1 = regionsJsonObjects[i].getRicoverati_con_sintomi();
                i--;
                break;
            }
        }

        for(; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
                val2 = regionsJsonObjects[i].getRicoverati_con_sintomi();
                break;
            }
        }

        return val1-val2;
    }

    public static int getItalyIntensiveTherapy() {
        return lastItaly.getTerapia_intensiva();
    }

    public static int getItalyNewIntensiveTherapy() {
        return lastItaly.getTerapia_intensiva() - italyJsonObjects[italyJsonObjects.length - 2].getTerapia_intensiva();
    }

    public static int getRegionIntensiveTherapy(String region) {
        for(int i = regionsJsonObjects.length - 1; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region))
                return regionsJsonObjects[i].getTerapia_intensiva();
        }
        return -1;
    }

    public static int getRegionNewIntensiveTherapy(String region) {
        int val1 = 0,val2 = 0;
        int i = regionsJsonObjects.length - 1;

        for(; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
                val1 = regionsJsonObjects[i].getTerapia_intensiva();
                i--;
                break;
            }
        }

        for(; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
                val2 = regionsJsonObjects[i].getTerapia_intensiva();
                break;
            }
        }

        return val1-val2;
    }

    public static int getItalyTotalHospitalized() {
        return lastItaly.getTotale_ospedalizzati();
    }

    public static int getItalyNewTotalHospitalized() {
        return lastItaly.getTotale_ospedalizzati() - italyJsonObjects[italyJsonObjects.length - 2].getTotale_ospedalizzati();
    }

    public static int getRegionTotalHospitalized(String region) {
        for(int i = regionsJsonObjects.length - 1; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region))
                return regionsJsonObjects[i].getTotale_ospedalizzati();
        }
        return -1;
    }

    public static int getRegionNewTotalHospitalized(String region) {
        int val1 = 0,val2 = 0;
        int i = regionsJsonObjects.length - 1;

        for(; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
                val1 = regionsJsonObjects[i].getTotale_ospedalizzati();
                i--;
                break;
            }
        }

        for(; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
                val2 = regionsJsonObjects[i].getTotale_ospedalizzati();
                break;
            }
        }

        return val1-val2;
    }


    public static int getItalyHouseholdIsolation() {
        return lastItaly.getIsolamento_domiciliare();
    }

    public static int getItalyNewHouseholdIsolation() {
        return lastItaly.getIsolamento_domiciliare() - italyJsonObjects[italyJsonObjects.length - 2].getIsolamento_domiciliare();
    }

    public static int getRegionHouseholdIsolation(String region) {
        for(int i = regionsJsonObjects.length - 1; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region))
                return regionsJsonObjects[i].getIsolamento_domiciliare();
        }
        return -1;
    }

    public static int getRegionNewHouseholdIsolation(String region) {
        int val1 = 0,val2 = 0;
        int i = regionsJsonObjects.length - 1;

        for(; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
                val1 = regionsJsonObjects[i].getIsolamento_domiciliare();
                i--;
                break;
            }
        }

        for(; i >= 0; i--){
            if(regionsJsonObjects[i].getDenominazione_regione().equalsIgnoreCase(region)){
                val2 = regionsJsonObjects[i].getIsolamento_domiciliare();
                break;
            }
        }

        return val1-val2;
    }
}
