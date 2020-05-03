package me.reply.covidstats.data.province;

import me.reply.covidstats.utils.Utils;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class ProvinceCovidData {
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
                e.printStackTrace();
            }
        }
        covidData.add(provinceDayData);
    }

    public File totalCasesGraph(String provinceName) throws IOException, ParseException {
        String plotTitle = "Casi totali - Provincia di " + provinceName;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        XYChart chart = new XYChartBuilder().title(plotTitle).xAxisTitle("Data").yAxisTitle("Valore").build();
        chart.getStyler().setLegendVisible(false);
        for (ProvinceDayData covidDatum : covidData) {
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidDatum.getDate());
            x.add(d);
            y.add(covidDatum.getTotal_cases());
        }
        chart.addSeries(plotTitle,x,y);
        return generateImage(chart);
    }

    public File newTotalCasesGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Nuovi contagi - Provincia di " + regionName;
        Vector<Integer> y = new Vector<>();
        Vector<Date> x = new Vector<>();
        XYChart chart = new XYChartBuilder().title(plotTitle).xAxisTitle("Data").yAxisTitle("Valore").build();
        chart.getStyler().setLegendVisible(false);
        x.add(startDate);
        y.add(0); //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getTotal_cases() - covidData.get(i - 1).getTotal_cases();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDate());
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
