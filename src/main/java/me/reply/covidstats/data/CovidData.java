package me.reply.covidstats.data;

import me.reply.covidstats.Bot;
import me.reply.covidstats.utils.Utils;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class CovidData {

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
                e.printStackTrace();
            }
        }
        covidData.add(dayData);
    }

    public File currentlyInfectedGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Attualmente contagiati - " + regionName;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        XYChart chart = new XYChartBuilder().title(plotTitle).xAxisTitle("Data").yAxisTitle("Valore").build();
        chart.getStyler().setLegendVisible(false);
        for(DayData d: covidData){
            y.add(d.getCurrently_infected());
            x.add(new SimpleDateFormat("dd-MM-yyyy").parse(d.getDayDate()));
        }
        chart.addSeries(plotTitle,x,y);
        return generateImage(chart);
    }

    public File newCurrentlyInfectedGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Differenza attualmente contagiati - " + regionName;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        XYChart chart = new XYChartBuilder().title(plotTitle).xAxisTitle("Data").yAxisTitle("Valore").build();
        chart.getStyler().setLegendVisible(false);
        //first element of graph
        x.add(startDate);
        y.add(0);
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getCurrently_infected() - covidData.get(i - 1).getCurrently_infected();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            x.add(d);
            y.add(difference);
        }
        chart.addSeries(plotTitle,x,y);
        return generateImage(chart);
    }

    public File recoveredGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Guariti - " + regionName;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        XYChart chart = new XYChartBuilder().title(plotTitle).xAxisTitle("Data").yAxisTitle("Valore").build();
        chart.getStyler().setLegendVisible(false);
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(d.getDayDate());
            x.add(date);
            y.add(d.getRecovered());
        }
        chart.addSeries(plotTitle,x,y);
        return generateImage(chart);
    }

    public File newRecoveredGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Guariti per giorno - " + regionName;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        XYChart chart = new XYChartBuilder().title(plotTitle).xAxisTitle("Data").yAxisTitle("Valore").build();
        chart.getStyler().setLegendVisible(false);
        x.add(startDate);
        y.add(0); //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getRecovered() - covidData.get(i - 1).getRecovered();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            x.add(d);
            y.add(difference);
        }
        chart.addSeries(plotTitle,x,y);
        return generateImage(chart);
    }

    public File deathGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Decessi - " + regionName;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        XYChart chart = new XYChartBuilder().title(plotTitle).xAxisTitle("Data").yAxisTitle("Valore").build();
        chart.getStyler().setLegendVisible(false);
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(d.getDayDate());
            x.add(date);
            y.add(d.getDeath());
        }
        chart.addSeries(plotTitle,x,y);
        return generateImage(chart);
    }

    public File newDeathGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Decessi per giorno - " + regionName;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        XYChart chart = new XYChartBuilder().title(plotTitle).xAxisTitle("Data").yAxisTitle("Valore").build();
        chart.getStyler().setLegendVisible(false);
        x.add(startDate);
        y.add(0);//first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getDeath() - covidData.get(i - 1).getDeath();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            x.add(d);
            y.add(difference);
        }
        chart.addSeries(plotTitle,x,y);
        return generateImage(chart);
    }

    public File totalCasesGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Casi totali - " + regionName;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        XYChart chart = new XYChartBuilder().title(plotTitle).xAxisTitle("Data").yAxisTitle("Valore").build();
        chart.getStyler().setLegendVisible(false);
        for (DayData covidDatum : covidData) {
            int totalCases = covidDatum.getCurrently_infected() + covidDatum.getRecovered() + covidDatum.getDeath();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidDatum.getDayDate());
            x.add(d);
            y.add(totalCases);
        }
        chart.addSeries(plotTitle,x,y);
        return generateImage(chart);
    }

    public File newTotalCasesGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Nuovi contagi - " + regionName;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        XYChart chart = new XYChartBuilder().title(plotTitle).xAxisTitle("Data").yAxisTitle("Valore").build();
        chart.getStyler().setLegendVisible(false);
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
        chart.addSeries(plotTitle,x,y);
        return generateImage(chart);
    }

    public File tamponsGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Tamponi - " + regionName;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        XYChart chart = new XYChartBuilder().title(plotTitle).xAxisTitle("Data").yAxisTitle("Valore").build();
        chart.getStyler().setLegendVisible(false);
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(d.getDayDate());
            x.add(date);
            y.add(d.getTampons());
        }
        chart.addSeries(plotTitle,x,y);
        return generateImage(chart);
    }

    public File newTamponsGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Tamponi giornalieri - " + regionName;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        XYChart chart = new XYChartBuilder().title(plotTitle).xAxisTitle("Data").yAxisTitle("Valore").build();
        chart.getStyler().setLegendVisible(false);
        x.add(startDate);
        y.add(0); //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getTampons() - covidData.get(i - 1).getTampons();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            x.add(d);
            y.add(difference);
        }
        chart.addSeries(plotTitle,x,y);
        return generateImage(chart);
    }

    private File generateImage(XYChart chart) throws IOException {
        String filename = Utils.randomFilename("");
        BitmapEncoder.saveBitmapWithDPI(chart, filename, BitmapEncoder.BitmapFormat.PNG, 300);
        return new File(filename + ".png");
    }
}
