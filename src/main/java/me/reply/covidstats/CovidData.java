package me.reply.covidstats;

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
import java.util.Random;
import java.util.Vector;

public class CovidData {

    private final Vector<DayData> covidData;

    private Date startDate;

    public CovidData() throws ParseException {
        covidData = new Vector<>();
        startDate = new SimpleDateFormat("yyyy-MM-dd").parse("2020-02-24");
    }

    public void add(DayData dayData){
        covidData.add(dayData);
    }

    public File currentlyInfectedGraph() throws IOException, ParseException {
        String plotTitle = "Currently Infected";
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(d.getDayDate());
            TimeSeries.add(new Day(date) ,d.getCurrently_infected());
        }
        return generateImage(TimeSeries,plotTitle,"Day","Number");
    }

    public File newCurrentlyInfectedGraph() throws IOException, ParseException {
        String plotTitle = "Currently Infected per day";
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        TimeSeries.add(new Day(startDate),0);  //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getCurrently_infected() - covidData.get(i - 1).getCurrently_infected();
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(covidData.get(i).getDayDate());
            TimeSeries.add(new Day(d),difference);
        }
        return generateImage(TimeSeries,plotTitle,"Day","Number");
    }

    public File recoveredGraph() throws IOException, ParseException {
        String plotTitle = "Recovered";
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(d.getDayDate());
            TimeSeries.add(new Day(date) ,d.getRecovered());
        }
        return generateImage(TimeSeries,plotTitle,"Day","Number");
    }

    public File newRecoveredGraph() throws IOException, ParseException {
        String plotTitle = "Recovered per day";
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        TimeSeries.add(new Day(startDate),0);  //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getRecovered() - covidData.get(i - 1).getRecovered();
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(covidData.get(i).getDayDate());
            TimeSeries.add(new Day(d),difference);
        }
        return generateImage(TimeSeries,plotTitle,"Day","Number");
    }

    public File deathGraph() throws IOException, ParseException {
        String plotTitle = "Deaths";
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(d.getDayDate());
            TimeSeries.add(new Day(date) ,d.getDeath());
        }
        return generateImage(TimeSeries,plotTitle,"Day","Number");
    }

    public File newDeathGraph() throws IOException, ParseException {
        String plotTitle = "Deaths per day";
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        TimeSeries.add(new Day(startDate),0);  //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getDeath() - covidData.get(i - 1).getDeath();
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(covidData.get(i).getDayDate());
            TimeSeries.add(new Day(d),difference);
        }
        return generateImage(TimeSeries,plotTitle,"Day","Number");
    }

    public File totalCasesGraph() throws IOException, ParseException {
        String plotTitle = "Total cases";
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        for (DayData covidDatum : covidData) {
            int totalCases = covidDatum.getCurrently_infected() + covidDatum.getRecovered() + covidDatum.getDeath();
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(covidDatum.getDayDate());
            TimeSeries.add(new Day(d), totalCases);
        }
        return generateImage(TimeSeries,plotTitle,"Day","Number");
    }

    public File newTotalCasesGraph() throws IOException, ParseException {
        String plotTitle = "New cases";
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        TimeSeries.add(new Day(startDate),0);  //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int totalCasesToday = covidData.get(i).getCurrently_infected() + covidData.get(i).getRecovered() + covidData.get(i).getDeath();
            int totalCasesYesterday = covidData.get(i - 1).getCurrently_infected() + covidData.get(i - 1).getRecovered() + covidData.get(i - 1).getDeath();
            int difference = totalCasesToday - totalCasesYesterday;
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(covidData.get(i).getDayDate());
            TimeSeries.add(new Day(d),difference);
        }
        return generateImage(TimeSeries,plotTitle,"Day","Number");
    }

    public File tamponsGraph() throws IOException, ParseException {
        String plotTitle = "Tampons";
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        for(DayData d: covidData){
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(d.getDayDate());
            TimeSeries.add(new Day(date) ,d.getTampons());
        }
        return generateImage(TimeSeries,plotTitle,"Day","Number");
    }

    public File newTamponsGraph() throws IOException, ParseException {
        String plotTitle = "Tampons per day";
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        TimeSeries.add(new Day(startDate),0);  //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getTampons() - covidData.get(i - 1).getTampons();
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(covidData.get(i).getDayDate());
            TimeSeries.add(new Day(d),difference);
        }
        return generateImage(TimeSeries,plotTitle,"Day","Number");
    }

    private File generateImage(TimeSeries timeSeries, String title, String xLabel, String yLabel) throws IOException {
        TimeSeriesCollection data = new TimeSeriesCollection();
        data.addSeries(timeSeries);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title,
                xLabel,
                yLabel,
                data,
                false,
                false,
                false
        );

        BufferedImage bufferedImage = chart.createBufferedImage(1500,900);
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", bas);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] byteArray=bas.toByteArray();

        String filename = randomFilename();
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage image = ImageIO.read(in);
        File outputfile = new File(filename);
        ImageIO.write(image, "png", outputfile);
        return outputfile;
    }

    private String randomFilename(){
        char[] alphabet = "abcdefghijklmnopqrstuvxyz1234567890".toCharArray();
        StringBuilder builder = new StringBuilder();
        Random r = new Random();
        for(int i = 0; i < 10; i++){
            builder.append(alphabet[r.nextInt(alphabet.length)]);
        }
        return builder.append(".png").toString();
    }
}
