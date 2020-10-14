package me.reply.covidstats.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class CovidData {
    final Logger logger = LoggerFactory.getLogger(CovidData.class);

    private final Vector<DayData> covidData;
    private Date startDate;

    public CovidData() {
        covidData = new Vector<>();
    }

    public void add(DayData dayData){
        if(covidData.size() == 0) {
            try {
                startDate = new SimpleDateFormat("dd-MM-yyyy").parse(dayData.getDayDate());
            } catch (ParseException e) {
                System.err.println("Si è verificato un errore, verifica nel file di log");
                logger.error(e.toString());
            }
        }
        covidData.add(dayData);
    }

    public File currentlyInfectedGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Attualmente contagiati - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        for(DayData d: covidData){
            y.add(d.getCurrently_infected());
            x.add(new SimpleDateFormat("dd-MM-yyyy").parse(d.getDayDate()));
        }
        return ChartUtils.generateImage(plotTitle,x,y,rawFilename);
    }

    public File newCurrentlyInfectedGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Differenza attualmente contagiati - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        //first element of graph
        x.add(startDate);
        y.add(0);
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getCurrently_infected() - covidData.get(i - 1).getCurrently_infected();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            x.add(d);
            y.add(difference);
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }

    public File recoveredGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Guariti - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(d.getDayDate());
            x.add(date);
            y.add(d.getRecovered());
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }

    public File newRecoveredGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Guariti per giorno - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        x.add(startDate);
        y.add(0); //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getRecovered() - covidData.get(i - 1).getRecovered();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            x.add(d);
            y.add(difference);
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }

    public File deathGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Decessi - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(d.getDayDate());
            x.add(date);
            y.add(d.getDeath());
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }

    public File newDeathGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Decessi per giorno - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        x.add(startDate);
        y.add(0);//first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getDeath() - covidData.get(i - 1).getDeath();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            x.add(d);
            y.add(difference);
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }

    public File totalCasesGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Casi totali - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        for (DayData covidDatum : covidData) {
            int totalCases = covidDatum.getCurrently_infected() + covidDatum.getRecovered() + covidDatum.getDeath();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidDatum.getDayDate());
            x.add(d);
            y.add(totalCases);
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }

    public File newTotalCasesGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Nuovi contagi - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        x.add(startDate);
        y.add(0); //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int totalCasesToday = covidData.get(i).getCurrently_infected() + covidData.get(i).getRecovered() + covidData.get(i).getDeath();
            int totalCasesYesterday = covidData.get(i - 1).getCurrently_infected() + covidData.get(i - 1).getRecovered() + covidData.get(i - 1).getDeath();
            int difference = totalCasesToday - totalCasesYesterday;
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            x.add(d);
            y.add(difference);
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }

    public File tamponsGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Tamponi - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(d.getDayDate());
            x.add(date);
            y.add(d.getTampons());
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }

    public File newTamponsGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Tamponi giornalieri - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        x.add(startDate);
        y.add(0); //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getTampons() - covidData.get(i - 1).getTampons();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            x.add(d);
            y.add(difference);
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }

    public File hospitalizedWithSymptomsGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Ricoverati con sintomi - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(d.getDayDate());
            x.add(date);
            y.add(d.getHospitalized_with_symptoms());
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }

    public File newHospitalizedWithSymptomsGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Nuovi ricoverati con sintomi - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        x.add(startDate);
        y.add(0); //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getHospitalized_with_symptoms() - covidData.get(i - 1).getHospitalized_with_symptoms();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            x.add(d);
            y.add(difference);
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }

    public File intensive_therapyGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Terapia intensiva - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(d.getDayDate());
            x.add(date);
            y.add(d.getIntensive_therapy());
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }

    public File newIntensive_therapy(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Incremento terapie intensive - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        x.add(startDate);
        y.add(0); //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getIntensive_therapy() - covidData.get(i - 1).getIntensive_therapy();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            x.add(d);
            y.add(difference);
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }

    public File totalHospitalized(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Totale ospedalizzati - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(d.getDayDate());
            x.add(date);
            y.add(d.getTotal_hospitalized());
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }

    public File newTotalHospitalized(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Incremento totale ospedalizzati - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        x.add(startDate);
        y.add(0); //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getTotal_hospitalized() - covidData.get(i - 1).getTotal_hospitalized();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            x.add(d);
            y.add(difference);
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }

    public File householdIsolation(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Isolamento domiciliare - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(d.getDayDate());
            x.add(date);
            y.add(d.getHousehold_isolation());
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }

    public File newHouseholdIsolation(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Incremento isolamento domiciliare - " + regionName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        x.add(startDate);
        y.add(0); //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getHousehold_isolation() - covidData.get(i - 1).getHousehold_isolation();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            x.add(d);
            y.add(difference);
        }
        return ChartUtils.generateImage(plotTitle,x,y, rawFilename);
    }
}
