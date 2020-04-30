package me.reply.covidstats.data.province;

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
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        for (ProvinceDayData covidDatum : covidData) {
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidDatum.getDate());
            TimeSeries.add(new Day(d), covidDatum.getTotal_cases());
        }
        return generateImage(TimeSeries,plotTitle);
    }

    public File newTotalCasesGraph(String regionName) throws IOException, ParseException {
        if(regionName == null) regionName = "Italia";
        String plotTitle = "Nuovi contagi - Provincia di " + regionName;
        TimeSeries TimeSeries = new TimeSeries(plotTitle);
        TimeSeries.add(new Day(startDate),0);  //first element of graph
        for(int i = 1; i < covidData.size(); i++){
            int difference = covidData.get(i).getTotal_cases() - covidData.get(i - 1).getTotal_cases();
            Date d = new SimpleDateFormat("dd-MM-yyyy").parse(covidData.get(i).getDate());
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
