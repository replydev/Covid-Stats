package me.reply.covidstats.data;

import org.apache.commons.codec.digest.DigestUtils;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class ChartUtils {

    public static File generateImage(XYChart chart, String rawFilename) throws IOException {
        String path = CHARTS_FOLDER + rawFilename;
        BitmapEncoder.saveBitmapWithDPI(chart, path, BitmapEncoder.BitmapFormat.PNG, 300);
        File f = new File(path + ".png");
        addChart(f);
        return f;
    }

    public static XYChart createChart(String plotTitle){
        XYChart chart = new XYChartBuilder().title(plotTitle).xAxisTitle("Data").yAxisTitle("Valore").build();
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setDatePattern("dd-MM");
        chart.getStyler().setDecimalPattern("0");
        chart.getStyler().setMarkerSize(0);
        return chart;
    }

    private static Vector<File> charts = new Vector<>();

    public static final String CHARTS_FOLDER = "charts/";

    public static String computeFilename(String plotTitle){
        return DigestUtils.md5Hex(plotTitle);
    }

    public static File getFileChart(String filename){
        for(File f : charts){
            if(f.getName().equalsIgnoreCase(filename))
                return f;
        }
        return null;
    }

    public static void addChart(File f){
        charts.add(f);
    }
}
