package me.reply.covidstats.data;

import me.reply.covidstats.Bot;
import me.reply.covidstats.utils.Utils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

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
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(d.getDayDate());
            TimeSeries.add(new Day(date) ,d.getCurrently_infected());
        }
        return generateImage(TimeSeries,plotTitle);
    }

    public File newCurrentlyInfectedGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Differenza attualmente contagiati - " + regionName;
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        TimeSeries.add(new Day(startDate),0);  //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getCurrently_infected() - covidData.get(i - 1).getCurrently_infected();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            TimeSeries.add(new Day(d),difference);
        }
        return generateImage(TimeSeries,plotTitle);
    }

    public File recoveredGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Guariti - " + regionName;
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(d.getDayDate());
            TimeSeries.add(new Day(date) ,d.getRecovered());
        }
        return generateImage(TimeSeries,plotTitle);
    }

    public File newRecoveredGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Guariti per giorno - " + regionName;
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        TimeSeries.add(new Day(startDate),0);  //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getRecovered() - covidData.get(i - 1).getRecovered();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            TimeSeries.add(new Day(d),difference);
        }
        return generateImage(TimeSeries,plotTitle);
    }

    public File deathGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Decessi - " + regionName;
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(d.getDayDate());
            TimeSeries.add(new Day(date) ,d.getDeath());
        }
        return generateImage(TimeSeries,plotTitle);
    }

    public File newDeathGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Decessi per giorno - " + regionName;
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        TimeSeries.add(new Day(startDate),0);  //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getDeath() - covidData.get(i - 1).getDeath();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            TimeSeries.add(new Day(d),difference);
        }
        return generateImage(TimeSeries,plotTitle);
    }

    public File totalCasesGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Casi totali - " + regionName;
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        for (DayData covidDatum : covidData) {
            int totalCases = covidDatum.getCurrently_infected() + covidDatum.getRecovered() + covidDatum.getDeath();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidDatum.getDayDate());
            TimeSeries.add(new Day(d), totalCases);
        }
        return generateImage(TimeSeries,plotTitle);
    }

    public File newTotalCasesGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Nuovi contagi - " + regionName;
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        TimeSeries.add(new Day(startDate),0);  //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int totalCasesToday = covidData.get(i).getCurrently_infected() + covidData.get(i).getRecovered() + covidData.get(i).getDeath();
            int totalCasesYesterday = covidData.get(i - 1).getCurrently_infected() + covidData.get(i - 1).getRecovered() + covidData.get(i - 1).getDeath();
            int difference = totalCasesToday - totalCasesYesterday;
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            TimeSeries.add(new Day(d),difference);
        }
        return generateImage(TimeSeries,plotTitle);
    }

    public File tamponsGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Tamponi - " + regionName;
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(d.getDayDate());
            TimeSeries.add(new Day(date) ,d.getTampons());
        }
        return generateImage(TimeSeries,plotTitle);
    }

    public File newTamponsGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Tamponi giornalieri - " + regionName;
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        TimeSeries.add(new Day(startDate),0);  //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getTampons() - covidData.get(i - 1).getTampons();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDayDate());
            TimeSeries.add(new Day(d),difference);
        }
        return generateImage(TimeSeries,plotTitle);
    }

    private File generateImage(TimeSeries timeSeries, String title) throws IOException {
        TimeSeriesCollection data = new TimeSeriesCollection();
        data.addSeries(timeSeries);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title,
                "Giorno",
                "Valore",
                data,
                false,
                false,
                false
        );

        BufferedImage bufferedImage = chart.createBufferedImage(Bot.getInstance().getConfig().CHART_WIDTH,Bot.getInstance().getConfig().CHART_HEIGHT);
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", bas);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] byteArray=bas.toByteArray();

        String filename = Utils.randomFilename(".png");
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage image = ImageIO.read(in);
        File outputfile = new File(filename);
        ImageIO.write(image, "png", outputfile);
        return outputfile;
    }
}
