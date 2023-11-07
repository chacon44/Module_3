package com.epam.esm.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateCalculation {
    static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public final static String lastUpdateDate = LastUpdateDate();
    public final static String createDate = df.format(getDate());
    public final static long duration = getDuration();

    public static String LastUpdateDate(){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
        return df.format(new Date());
    }

    public static Date getDate() {

        int year = 2018;
        int month = 8;
        int day = 29;
        int hour = 6;
        int minute = 12;
        int second = 15;
        int millisecond = 156;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour+2);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);

        return calendar.getTime();
    }

    public static long getDuration(){

        return Math.abs(new Date().getTime() - getDate().getTime()) / (24 * 60 * 60 * 1000);
    }
}
