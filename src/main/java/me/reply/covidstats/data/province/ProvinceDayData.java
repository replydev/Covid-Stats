package me.reply.covidstats.data.province;

public class ProvinceDayData {
    private final int total_cases;
    private final String date;

    public ProvinceDayData(int total_cases, String date) {
        this.total_cases = total_cases;
        this.date = date;
    }

    public int getTotal_cases() {
        return total_cases;
    }
    public String getDate() {
        return date;
    }
}
