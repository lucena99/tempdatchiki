package ru.psv4.dto;

public class DateRow {
    public String date1;
    public String date2;
    public String date3;

    public DateRow(String date1, String date2, String date3) {
        this.date1 = date1;
        this.date2 = date2;
        this.date3 = date3;
    }

    public String getDate1() {
        return date1;
    }

    public String getDate2() {
        return date2;
    }

    public String getDate3() {
        return date3;
    }
}
