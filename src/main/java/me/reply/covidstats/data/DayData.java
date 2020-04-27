package me.reply.covidstats.data;

public class DayData {

    private final int currently_infected;
    private final int recovered;
    private final int death;
    private final int tampons;
    private final String dayDate;

    public DayData(int currently_infected, int recovered, int death, int tampons, String dayDate) {
        this.currently_infected = currently_infected;
        this.recovered = recovered;
        this.death = death;
        this.tampons = tampons;
        this.dayDate = dayDate;
    }

    public int getCurrently_infected() {
        return currently_infected;
    }

    public int getRecovered() {
        return recovered;
    }

    public int getDeath() {
        return death;
    }

    public int getTampons() {
        return tampons;
    }

    public String getDayDate() {
        return dayDate;
    }
}
