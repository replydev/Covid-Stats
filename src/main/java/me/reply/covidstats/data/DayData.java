package me.reply.covidstats.data;

public class DayData {
    private final int currently_infected;
    private final int recovered;
    private final int death;
    private final int tampons;
    private final int hospitalized_with_symptoms;
    private final int intensive_therapy;
    private final int total_hospitalized;
    private final int household_isolation;
    private final String dayDate;

    public DayData(int currently_infected,
                   int recovered,
                   int death,
                   int tampons,
                   int hospitalized_with_symptoms,
                   int intensive_therapy,
                   int total_hospitalized,
                   int household_isolation,
                   String dayDate) {
        this.currently_infected = currently_infected;
        this.recovered = recovered;
        this.death = death;
        this.tampons = tampons;
        this.hospitalized_with_symptoms = hospitalized_with_symptoms;
        this.intensive_therapy = intensive_therapy;
        this.total_hospitalized = total_hospitalized;
        this.household_isolation = household_isolation;
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

    public int getHospitalized_with_symptoms() {
        return hospitalized_with_symptoms;
    }

    public int getIntensive_therapy() {
        return intensive_therapy;
    }

    public int getTotal_hospitalized() {
        return total_hospitalized;
    }

    public int getHousehold_isolation() {
        return household_isolation;
    }

    public String getDayDate() {
        return dayDate;
    }
}
