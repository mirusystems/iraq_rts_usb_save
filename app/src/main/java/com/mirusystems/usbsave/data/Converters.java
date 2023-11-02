package com.mirusystems.usbsave.data;

import androidx.room.TypeConverter;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String integerToText(Integer number) {
        return number == null ? null : Integer.toString(number);
    }

    @TypeConverter
    public static int textToInteger(String str) {
        return str == null ? null : Integer.parseInt(str);
    }
}
