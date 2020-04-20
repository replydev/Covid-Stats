import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;
import java.util.Vector;

public class CovidData {

    private Vector<DayData> covidData;

    public CovidData(){
        covidData = new Vector<>();
    }

    public void add(DayData dayData){
        covidData.add(dayData);
    }

    public File currentlyInfectedGraph() throws IOException {
        String plotTitle = "Currently Infected";
        XYSeries xySeries = new XYSeries(plotTitle);
        int count = 1;
        for(DayData d: covidData){
            xySeries.add(count ,d.getCurrently_infected());
            count++;
        }
        return generateImage(xySeries,plotTitle,"Day","Number");
    }
    public File recoveredGraph() throws IOException {
        String plotTitle = "Recovered";
        XYSeries xySeries = new XYSeries(plotTitle);
        int count = 1;
        for(DayData d: covidData){
            xySeries.add(count ,d.getRecovered());
            count++;
        }
        return generateImage(xySeries,plotTitle,"Day","Number");
    }
    public File deathGraph() throws IOException {
        String plotTitle = "Deaths";
        XYSeries xySeries = new XYSeries(plotTitle);
        int count = 1;
        for(DayData d: covidData){
            xySeries.add(count ,d.getDeath());
            count++;
        }
        return generateImage(xySeries,plotTitle,"Day","Number");
    }
    public File tamponsGraph() throws IOException {
        String plotTitle = "Tampons";
        XYSeries xySeries = new XYSeries(plotTitle);
        int count = 1;
        for(DayData d: covidData){
            xySeries.add(count ,d.getTampons());
            count++;
        }
        return generateImage(xySeries,plotTitle,"Day","Number");
    }


    private File generateImage(XYSeries xySeries,String title,String xLabel,String yLabel) throws IOException {
        XYSeriesCollection data = new XYSeriesCollection(xySeries);
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xLabel,
                yLabel,
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
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
