package ru.psv4.tempdatchiki.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateUtils {

    public static String formatInterval(LocalDate date1, LocalDate date2) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return f.format(date1) + " - " + f.format(date2);
    }

    public static List<LocalDate> getIntervalDates(LocalDate date1, LocalDate date2) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate date = LocalDate.from(date1);
        while (date.isBefore(date2) || date.isEqual(date2)) {
            dates.add(date);
            date = date.plusDays(1);
        }
        return dates;
    }
}
