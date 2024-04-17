package com.example.greenplate.viewmodels.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static Date str2Date(String str) throws ParseException {
        if ("forever away".equals(str)) {
            return new Date(Long.MAX_VALUE);
        }
        Date d = null;
        if (!str.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            d = sdf.parse(str);
        }
        return d == null ? new Date(Long.MAX_VALUE) : d;
    }

    public static String date2Str(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(new Date());
        currentCalendar.add(Calendar.DAY_OF_YEAR, 5);
        if (date == null || date.after(currentCalendar.getTime())) {
            return "forever away";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        return sdf.format(date);
    }
}
