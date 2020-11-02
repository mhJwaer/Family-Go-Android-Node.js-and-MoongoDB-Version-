package com.mh.jwaer.familygo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Converters {
    public static String unixToDateConverter(long unixSeconds) {

// convert seconds to milliseconds
        Date date = new java.util.Date((unixSeconds +10800 )* 1000L);
// the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd    HH:mm ", Locale.getDefault());

// give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    public static String chatUnixToDateConverter(long unixSeconds) {

// convert seconds to milliseconds
        Date date = new java.util.Date((unixSeconds +10800 )* 1000L);
// the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm ", Locale.getDefault());

// give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }
}
