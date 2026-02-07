package com.wealthwise.app.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

    private static final ThreadLocal<SimpleDateFormat> FORMAT_FULL =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("MMM dd, yyyy", Locale.US));

    private static final ThreadLocal<SimpleDateFormat> FORMAT_SHORT =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("MM/dd", Locale.US));

    private static final ThreadLocal<SimpleDateFormat> FORMAT_MONTH_YEAR =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("MMM yyyy", Locale.US));

    private DateUtils() { }

    public static Date getStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * @param month 1-based month (1 = January, 12 = December)
     * @param year  the full year (e.g. 2024)
     */
    public static Date getStartOfMonth(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1); // Calendar months are 0-based
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * @param month 1-based month (1 = January, 12 = December)
     * @param year  the full year (e.g. 2024)
     */
    public static Date getEndOfMonth(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public static Date getStartOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        return getStartOfMonth(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
    }

    public static Date getEndOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        return getEndOfMonth(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
    }

    /** @return current month 1-12 */
    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /** Formats as "MMM dd, yyyy" e.g. "Jan 15, 2024" */
    public static String formatDate(Date date) {
        return FORMAT_FULL.get().format(date);
    }

    /** Formats as "MM/dd" e.g. "01/15" */
    public static String formatShortDate(Date date) {
        return FORMAT_SHORT.get().format(date);
    }

    /**
     * Formats month and year as "MMM yyyy" e.g. "Jan 2024".
     *
     * @param month 1-based month
     * @param year  the full year
     */
    public static String formatMonthYear(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.YEAR, year);
        return FORMAT_MONTH_YEAR.get().format(cal.getTime());
    }

    /**
     * Returns the start-of-month Date for the month that is {@code monthsBack} months ago.
     *
     * @param monthsBack number of months to go back (0 = current month)
     */
    public static Date getMonthsAgo(int monthsBack) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -monthsBack);
        return getStartOfMonth(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    public static Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }
}
