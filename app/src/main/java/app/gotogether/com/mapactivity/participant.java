package app.gotogether.com.mapactivity;

// 참석자 리스트

/**
 * Created by user on 2017-07-26.
 */

public class participant {

    private int year;
    private int month;
    private int day;
    private String part;

    public participant(int year, int month, int day, String part) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.part = part;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part += part;
    }
}
