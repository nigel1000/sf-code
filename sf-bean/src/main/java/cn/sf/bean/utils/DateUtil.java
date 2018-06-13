package cn.sf.bean.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Date类型只作为入参、入库、报表，中间运算尽量使用LocalDate&LocalDateTime
 */
public final class DateUtil {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter chinaDateFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    private static final DateTimeFormatter chinaSimpleDateFormatter = DateTimeFormatter.ofPattern("MM月dd日");

    private DateUtil() {}

    // Wed Jan 31 15:11:43 CST 2018
    public static Date getCurrentDate() {
        return new Date();
    }

    // 2018-01-31
    public static LocalDate toLocalDate(Date date) {
        if (date == null)
            return null;
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // 2018-01-31T15:11:43.592
    public static LocalDateTime toLocalDateTime(Date date) {

        if (date == null)
            return null;
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    // 2018-01-31T00:00
    public static LocalDateTime getStartOfDay(Date date) {

        if (date == null)
            return null;
        LocalDate localDate = toLocalDate(date);
        return localDate.atStartOfDay();
    }

    // 2018-01-31T23:59:59.999999999
    public static LocalDateTime getEndOfDay(Date date) {

        if (date == null)
            return null;
        LocalDate localDate = toLocalDate(date);
        return LocalDateTime.of(localDate, LocalTime.MAX);
    }

    // Wed Jan 31 00:00:00 CST 2018
    public static Date toDate(LocalDate localDate) {
        if (localDate == null)
            return null;
        Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    // Wed Jan 31 15:11:43 CST 2018
    public static Date toDate(LocalDateTime localDateTime) {

        if (localDateTime == null)
            return null;
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static Date parseDateTime(String dateTime) {
        return toDate(LocalDateTime.parse(dateTime, dateTimeFormatter));
    }

    public static Date parseDate(String date) {
        return toDate(LocalDate.parse(date, dateFormatter));
    }

    // 2018-01-31 15:11:43
    public static String formatByDateTimeFormatter(Date date) {
        return format(date, dateTimeFormatter);
    }

    // 2018-01-31
    public static String formatByDateFormatter(Date date) {
        return format(date, dateFormatter);
    }

    // 2018年01月31日
    public static String formatByChinaDateFormatter(Date date) {
        return format(date, chinaDateFormatter);
    }

    // 01月31日
    public static String formatByChinaSimpleDateFormatter(Date date) {
        return format(date, chinaSimpleDateFormatter);
    }

    public static String getDayOfWeek(LocalDate date) {
        switch (date.getDayOfWeek()) {
            case MONDAY:
                return "一";
            case TUESDAY:
                return "二";
            case WEDNESDAY:
                return "三";
            case THURSDAY:
                return "四";
            case FRIDAY:
                return "五";
            case SATURDAY:
                return "六";
            case SUNDAY:
                return "日";
        }
        return "";
    }

    private static String format(Date date, DateTimeFormatter formatter) {
        if (date == null)
            return "";
        LocalDateTime localDateTime = toLocalDateTime(date);
        return formatter.format(localDateTime);
    }

    public static void main(String[] args) {
        System.out.println("getCurrentDate:" + getCurrentDate());
        System.out.println("toLocalDate:" + toLocalDate(getCurrentDate()));
        System.out.println("toLocalDateTime:" + toLocalDateTime(getCurrentDate()));
        System.out.println("getStartOfDay:" + getStartOfDay(getCurrentDate()));
        System.out.println("getEndOfDay:" + getEndOfDay(getCurrentDate()));
        System.out.println("toDate:" + toDate(LocalDate.now()));
        System.out.println("toDate:" + toDate(LocalDateTime.now()));
        System.out.println("formatByDateTimeFormatter:" + formatByDateTimeFormatter(getCurrentDate()));
        System.out.println("formatByDateFormatter:" + formatByDateFormatter(getCurrentDate()));
        System.out.println("formatByChinaDateFormatter:" + formatByChinaDateFormatter(getCurrentDate()));
        System.out.println("formatByChinaSimpleDateFormatter:" + formatByChinaSimpleDateFormatter(getCurrentDate()));
        System.out.println("getDayOfWeek:" + getDayOfWeek(LocalDate.now()));
    }

}
