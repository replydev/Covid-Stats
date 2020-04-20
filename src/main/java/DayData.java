import java.time.LocalDate;

public class DayData {

    private final int currently_infected;
    private final int recovered;
    private final int death;
    private final int tampons;
    private final LocalDate day;

    public DayData(int currently_infected, int recovered, int death, int tampons, LocalDate day) {
        this.currently_infected = currently_infected;
        this.recovered = recovered;
        this.death = death;
        this.tampons = tampons;
        this.day = day;
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

    public LocalDate getDay() {
        return day;
    }
}
