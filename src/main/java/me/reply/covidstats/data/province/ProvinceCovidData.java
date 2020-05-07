package me.reply.covidstats.data.province;

import me.reply.covidstats.data.ChartUtils;
import org.knowm.xchart.XYChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class ProvinceCovidData {
    Logger logger = LoggerFactory.getLogger(ProvinceCovidData.class);

    private final Vector<ProvinceDayData> covidData;
    private Date startDate;

    public ProvinceCovidData() {
        covidData = new Vector<>();
    }

    public void add(ProvinceDayData provinceDayData){
        if(covidData.size() == 0) {
            try {
                startDate = new SimpleDateFormat("dd-MM-yyyy").parse(provinceDayData.getDate());
            } catch (ParseException e) {
                System.err.println("Si è verificato un errore, verifica nel file di log");
                logger.error(e.toString());
            }
        }
        covidData.add(provinceDayData);
    }

    public File totalCasesGraph(String provinceName) throws IOException, ParseException {
        String plotTitle = "Casi totali - Provincia di " + provinceName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        XYChart chart = ChartUtils.createChart(plotTitle);
        for (ProvinceDayData covidDatum : covidData) {
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidDatum.getDate());
            x.add(d);
            y.add(covidDatum.getTotal_cases());
        }
        chart.addSeries(plotTitle,x,y);
        return ChartUtils.generateImage(chart, rawFilename);
    }

    public File newTotalCasesGraph(String provinceName) throws IOException, ParseException {
        String plotTitle = "Nuovi contagi - Provincia di " + provinceName;
        String rawFilename = ChartUtils.computeFilename(plotTitle);
        File output = ChartUtils.getFileChart( rawFilename + ".png");
        if(output != null)
            return output;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        XYChart chart = ChartUtils.createChart(plotTitle);
        x.add(startDate);
        y.add(0); //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getTotal_cases() - covidData.get(i - 1).getTotal_cases();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDate());
            x.add(d);
            y.add(difference);
        }
        chart.addSeries(plotTitle,x,y);
        return ChartUtils.generateImage(chart, rawFilename);
    }
}
