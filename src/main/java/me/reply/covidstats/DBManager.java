package me.reply.covidstats;

import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DBManager {
    private final Connection c;

    private int rows;
    private String currentDate;

    public String getCurrentDate(){
        return currentDate;
    }

    public DBManager(String filename,String startDate) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:" + filename);
        createDatabase();
        getCount(startDate);
    }

    public void close() throws SQLException {
        c.close();
    }

    private void getCount(String startDate) throws SQLException {
        Statement statement = c.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM covidData");
        rows = 0;
        currentDate = startDate;
        while(resultSet.next()){
            rows++;
            currentDate = Utils.invertDate(resultSet.getString("date"));
            currentDate = incDate(currentDate);
        }
        statement.close();
    }

    public void createDatabase() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS 'covidData' (\n" +
                "'id' INTEGER NOT NULL,\n" +
                "'currently_infected' INTEGER NOT NULL DEFAULT '0',\n" +
                "'recovered' INTEGER NOT NULL DEFAULT '0',\n" +
                "'deaths' INTEGER NOT NULL DEFAULT '0',\n" +
                "'tampons' INTEGER NOT NULL DEFAULT '0',\n" +
                "'date' DATE DEFAULT NULL,\n" +
                "PRIMARY KEY('id'))";
        Statement stmt = c.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }
    public String incDate(String date){
        LocalDate date1 = LocalDate.parse(date,DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        date1 = date1.plusDays(1);
        return date1.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public void addData(DayData dayData) throws SQLException {
        PreparedStatement preparedStatement = c.prepareStatement("INSERT INTO covidData (id,currently_infected,recovered,deaths,tampons,date) VALUES (?,?,?,?,?,?)");
        preparedStatement.setInt(1,rows);
        preparedStatement.setInt(2,dayData.getCurrently_infected());
        preparedStatement.setInt(3,dayData.getRecovered());
        preparedStatement.setInt(4,dayData.getDeath());
        preparedStatement.setInt(5,dayData.getTampons());
        preparedStatement.setObject(6, Utils.invertDate(currentDate));
        preparedStatement.executeUpdate();
        preparedStatement.close();
        rows++;
        currentDate = incDate(currentDate);
    }

    public CovidData getData() throws SQLException, ParseException {
        CovidData covidData = new CovidData();
        Statement statement = c.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM covidData");
        while(resultSet.next()){
            int currently_infected = resultSet.getInt("currently_infected");
            int recovered = resultSet.getInt("recovered");
            int deaths = resultSet.getInt("deaths");
            int tampons = resultSet.getInt("tampons");
            String date = resultSet.getString("date");
            DayData dayData = new DayData(currently_infected,recovered,deaths,tampons,date);
            covidData.add(dayData);
        }
        return covidData;
    }
}
