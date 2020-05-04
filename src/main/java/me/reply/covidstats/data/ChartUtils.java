package me.reply.covidstats.data;

import me.reply.covidstats.utils.Utils;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.io.File;
import java.io.IOException;

public class ChartUtils {

    public static File generateImage(XYChart chart) throws IOException {
        String filename = Utils.randomFilename("");
        BitmapEncoder.saveBitmapWithDPI(chart, filename, BitmapEncoder.BitmapFormat.PNG, 300);
        return new File(filename + ".png");
    }

    public static XYChart createChart(String plotTitle){
        XYChart chart = new XYChartBuilder().title(plotTitle).xAxisTitle("Data").yAxisTitle("Valore").build();
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setDatePattern("dd-MM");
        chart.getStyler().setDecimalPattern("0");
        return chart;
    }
}
