package com.module3;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Main {
    public static void main(String[] args) {

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());



        System.out.println("now as ISO " + nowAsISO);



        ZonedDateTime now_zoned = ZonedDateTime.now();
        LocalDateTime now_local = LocalDateTime.now();
        System.out.println(now_zoned);
        System.out.println(now_local);

        int year = 2018;
        int month = 8;
        int day = 29;
        int hour = 6;
        int minute = 12;
        int second = 15;
        int millisecond = 50;

        Date specificDateIn2020 = getDate(year,month,day,hour,minute,second,millisecond);
        String createDate = df.format(specificDateIn2020);
        System.out.println("create " + createDate);

        long diffInMillies = Math.abs(new Date().getTime() - specificDateIn2020.getTime());
        long diffDays = diffInMillies / (24 * 60 * 60 * 1000);

        System.out.println("Days between " + createDate + " and " + nowAsISO + " is: " + diffDays);
    }



    private static Date getDate(int year, int month, int day, int hour, int minute, int second, int millisecond) {
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
}
