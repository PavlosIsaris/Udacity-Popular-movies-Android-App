package com.example.android.movies.utilities;

import android.net.ParseException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Handles date methods and conversions
 */

public class DateUtils {

    /**
     * Gets the year as String from a String data
     *
     * @param date the date as String
     * @return the year as String
     */
    public String getYearFromStringDate(String date) {
        DateFormat df = new SimpleDateFormat("yyyy");
        Date startDate;
        String newDateString = null;
        try {
            startDate = df.parse(date);
            newDateString = df.format(startDate);
            System.out.println(newDateString);
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }
        return newDateString;
    }
}
