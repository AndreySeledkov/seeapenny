package com.seeapenny.client.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SUtil {

    private static final Logger LOGGER = Logger.getLogger(SUtil.class.getName());
    public static final int[] DAY_IN_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final Map<Locale, String[]> mapMonthNames = new HashMap<Locale, String[]>();
    private static TimeZone serverTimeZone = TimeZone.getDefault();
    private static final Random rand = new SecureRandom();

    public static Random getRandom() {
        return rand;
    }

    public static TimeZone getTimeZone() {
        return serverTimeZone;
    }

    public static void setTimeZone(TimeZone zone) {
        serverTimeZone = zone;
    }

    public static boolean isLeapYear(int year) {
        int res = (4 - year % 4) / 4 - (100 - year % 100) / 100 + (400 - year % 400) / 400;
        return res == 1;
    }

    public static int trimstr2int(String str, int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        return str2int(str.trim(), defaultValue);
    }

    public static int str2int(String str, int defaultValue) {
        int res = defaultValue;
        try {
            res = Integer.parseInt(str);
        } catch (NumberFormatException ex) {
        }
        return res;
    }

    public static long str2long(String str, long defaultValue) {
        long res = defaultValue;
        try {
            res = Long.parseLong(str);
        } catch (NumberFormatException ex) {
        }
        return res;
    }

    public static double str2double(String str, double defaultValue) {
        double res = defaultValue;
        try {
            res = Double.parseDouble(str);
        } catch (NumberFormatException ex) {
        }
        return res;
    }

    public static Calendar createDefaultCalendar() {
        return Calendar.getInstance(serverTimeZone);
    }

    public static int dayDiff(Date date, Date date2) {
        return dayDiff(date, date2.getTime());
    }

    public static int dayDiff(Date date, long date2) {
        Calendar cal = createDefaultCalendar();
        if (date != null) {
            cal.setTime(date);
        }
        int year1 = cal.get(Calendar.YEAR);
        int day1 = cal.get(Calendar.DAY_OF_YEAR);
        int max1 = cal.getActualMaximum(Calendar.DAY_OF_YEAR);

        cal.setTimeInMillis(date2);
        int year2 = cal.get(Calendar.YEAR);
        int day2 = cal.get(Calendar.DAY_OF_YEAR);
        int max2 = cal.getActualMaximum(Calendar.DAY_OF_YEAR);

        if (year1 == year2) {
            return day1 - day2;
        } else if (year1 > year2) {
            return (max2 - day2) + day1;
        } else {
            return -((max1 - day1) + day2);
        }
    }

    public static String floatToString2(float r) {
        return String.format("%.2f", r).replace(',', '.');
    }

    public static String replaceHTMLtext(String text) {
        if (text == null) {
            return null;
        }
        text = replaceAll(text, "&", "&amp;");
        text = replaceAll(text, "<", "&lt;");
        text = replaceAll(text, ">", "&gt;");
        return text;
    }

    public static String replaceAll(String str, String pattern, String replacement) {
        if (pattern.length() == 0) {
            return str;
        }

        int s = 0;
        int e = str.indexOf(pattern, s);
        if (e < 0) {
            return str;
        }

        StringBuilder result = new StringBuilder(str.length() + replacement.length());
        while (e >= 0) {
            result.append(str.substring(s, e));
            result.append(replacement);
            s = e + pattern.length();
            e = str.indexOf(pattern, s);
        }

        result.append(str.substring(s));
        return result.toString();
    }

    public static StringBuilder replaceAll(StringBuilder str, String pattern, String replacement) {
        int len = pattern.length();
        if (len == 0) {
            return str;
        }
        int rlen = replacement.length();
        int s = str.indexOf(pattern, 0);
        while (s >= 0) {
            str.replace(s, s + len, replacement);
            s = str.indexOf(pattern, s + rlen);
        }
        return str;
    }

    public static String removeLineBreaks(String s) {
        if (s == null) {
            return null;
        }

        s = replaceAll(s, "\r\n", " ");
        s = replaceAll(s, "\n\r", " ");
        s = replaceAll(s, "\r", " ");
        s = replaceAll(s, "\n", " ");

        return s.trim();
    }

    private static final Calendar calendar = Calendar.getInstance();

    public static Date previousDate(int days) {
        synchronized (calendar) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.DAY_OF_YEAR, -days);
            return calendar.getTime();
        }
    }

    private static final long TIME_20130101;
    private static final long MILLIS_IN_DAY;
    private static final TimeZone serverTimezone = Calendar.getInstance().getTimeZone();

    static {
        Calendar c = Calendar.getInstance();
        c.set(2013, Calendar.JANUARY, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        TIME_20130101 = c.getTimeInMillis();
        MILLIS_IN_DAY = TimeUnit.DAYS.toMillis(1);
    }

    public static long diffNowSince20130101() {
        return diffSince20130101(new Date());
    }

    public static long diffSince20130101(Date date) {
        long t1 = date.getTime();
        if (serverTimezone.inDaylightTime(date)) {
            t1 += serverTimezone.getDSTSavings();
        }
        long d = (t1 - TIME_20130101) / MILLIS_IN_DAY;
        return d;
    }

    public static Date getDate(long daynumber) {
        Calendar c = Calendar.getInstance();
        c.set(2013, Calendar.JANUARY, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DAY_OF_YEAR, (int) daynumber);
        return c.getTime();
    }

    public static Date now() {
        return new Date(System.currentTimeMillis());
    }

    public static boolean isTotay(Calendar calendar, Date date) {
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year1 = calendar.get(Calendar.YEAR);
        int dayOfYear1 = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTime(date);
        int year2 = calendar.get(Calendar.YEAR);
        int dayOfYear2 = calendar.get(Calendar.DAY_OF_YEAR);
        return (year1 == year2) && (dayOfYear1 == dayOfYear2);
    }

    public static boolean isYesterday(Calendar calendar, Date date) {
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year1 = calendar.get(Calendar.YEAR);
        int dayOfYear1 = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        int year2 = calendar.get(Calendar.YEAR);
        int dayOfYear2 = calendar.get(Calendar.DAY_OF_YEAR);
        return (year1 == year2) && (dayOfYear1 == dayOfYear2);
    }

    public static int dateToInt(Calendar calendar, Date date) {
        calendar.setTime(date);
        int fid = calendar.get(Calendar.YEAR);
        fid = fid * 100 + calendar.get(Calendar.MONTH);
        fid = fid * 100 + calendar.get(Calendar.DAY_OF_MONTH);
        return fid;
    }

    private static String digit2(long x) {
        return (x < 10) ? "0" + x : String.valueOf(x);
    }

    public static String toTime(long milis) {
        long seconds = milis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long hour = hours % 24;
        long minute = minutes % 60;
        long second = seconds % 60;

        return digit2(days) + " " + digit2(hour) + ":" + digit2(minute) + ":" + digit2(second);
    }

    /**
     * inclusive, [start..end)
     *
     * @param <E>
     * @param list
     * @param start
     * @param end
     * @return
     */
    public static <E> List<E> getSafeSubList(List<E> list, int start, int end) {
        if (start < 0) {
            start = 0;
        }
        if (end > list.size()) {
            end = list.size();
        }
        if (start > end) {
            start = end;
        }
        return list.subList(start, end);
    }

    /**
     * @param <E>
     * @param list
     * @param page
     * @param perPage
     * @return getSafeSubList(list, page * perPage, (page + 1) * perPage);
     */
    public static <E> List<E> getSafePage(List<E> list, int page, int perPage) {
        return getSafeSubList(list, page * perPage, (page + 1) * perPage);
    }

    public static boolean stringEquals(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return true;
        } else if (s1 != null && s2 != null) {
            return s1.equals(s2);
        } else {
            return false;
        }
    }

    public static boolean objectEquals(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        } else if (o1 != null && o2 != null) {
            return o1.equals(o2);
        } else {
            return false;
        }
    }

    public static boolean isBirthday(Calendar calendar, Date birthday, int daysAfterNow) {
        calendar.setTime(birthday);
        int m1 = calendar.get(Calendar.MONTH);
        int d1 = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DAY_OF_YEAR, daysAfterNow);
        int m2 = calendar.get(Calendar.MONTH);
        int d2 = calendar.get(Calendar.DAY_OF_MONTH);

        return (d1 == d2) && (m1 == m2);
    }

    public static int getAge(Date birthday) {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar userBirth = Calendar.getInstance();

        userBirth.setTime(birthday);

        currentCalendar.add(Calendar.DAY_OF_MONTH, -userBirth.get(Calendar.DAY_OF_MONTH));
        currentCalendar.add(Calendar.MONTH, -userBirth.get(Calendar.MONTH));
        currentCalendar.add(Calendar.YEAR, -userBirth.get(Calendar.YEAR));

        return currentCalendar.get(Calendar.YEAR);
    }

    public static int compareDouble(double a, double b) {
        return a > b ? 1 : a < b ? -1 : 0;
    }

    /**
     * @return <br>+1, if a &gt; b,
     *         <br>-1, if a &lt; b,
     *         <br> 0, if a==b
     */
    public static int compareLong(long a, long b) {
        return a > b ? 1 : a < b ? -1 : 0;
    }

    public static int compareString(String a, String b) {
        if (a != null) {
            return a.compareTo(b);
        } else if (b != null) {
            return b.compareTo(a);
        }
        return 0;
    }

    public static String md5(byte[] bytes) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(bytes);
            String hash = new BigInteger(1, digest).toString(16).toLowerCase();
            while (hash.length() < 32) {
                hash = "0" + hash;
            }
            return hash;
        } catch (NoSuchAlgorithmException ex) {
            return "";
        }
    }

    public static Date validateDate(Date date, int minYear, int maxYear) {
        Calendar c = createDefaultCalendar();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        if (year < minYear) {
            c.set(Calendar.YEAR, minYear);
        } else if (year > maxYear) {
            c.set(Calendar.YEAR, maxYear);
        }
        return c.getTime();
    }

    public static int getPageCount(int items, int perPage) {
        return items / perPage + (items % perPage != 0 ? 1 : 0);
    }

    //    //in meters
//    public static double getDistance(UserLocation loc1, UserLocation loc2) {
//        return getDistance(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude());
//    }
//    public static double getDistanceMiles(UserLocation loc1, UserLocation loc2) {
//        return 0.000621371192 * getDistance(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude());
//    }
    public static int convertMilesToMeters(int miles) {
        return (int) (miles / 0.000621371192);
    }

    public static boolean isValidCoordinates(double lat, double lng) {
        return (lat >= -90) && (lat <= 90) && (lng >= -180) && (lng <= 180);
    }

    //in meters

    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        lat1 = lat1 * Math.PI / 180;
        lng1 = lng1 * Math.PI / 180;
        lat2 = lat2 * Math.PI / 180;
        lng2 = lng2 * Math.PI / 180;
        double res = Math.sin((lat2 - lat1) / 2) * Math.sin((lat2 - lat1) / 2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin((lng2 - lng1) / 2) * Math.sin((lng2 - lng1) / 2);
        return 2 * Math.atan2(Math.sqrt(res), Math.sqrt(1 - res)) * 6372795;
    }

    public static void saveFile(String dir, String filename, byte data[]) {
        if (dir == null || filename == null || data == null) {
            return;
        }
        try {
            File d = new File(dir);
            if (!d.exists()) {
                d.mkdirs();
            }
            if (d.isDirectory()) {
                FileOutputStream fout = new FileOutputStream(d.getAbsolutePath() + "/" + filename);
                fout.write(data);
                fout.close();
            }
        } catch (Exception ex) {
        }
    }

    private static final char[] digits = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'
    };  //from Integer.digits

    public static String toReversedHex(long i) {
        //from Long.toHex()
        char[] buf = new char[16];
        int charPos = 0;
        final int shift = 4;
        final long mask = (1 << shift) - 1;
        do {
            buf[charPos++] = digits[(int) (i & mask)];
            i >>>= shift;
        } while (i != 0);

        return new String(buf, 0, charPos);
    }

    public static long parseReversedHex(String s) {
        if (s == null) {
            throw new NumberFormatException("null");
        }

        long result = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            int digit = Character.digit(s.charAt(i), 16);
            if (digit < 0) {
                throw new NumberFormatException("wrong reversed hex '" + s + "'");
            }

            result <<= 4;
            result |= digit;
        }
        return result;
    }

    public static String readFile(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("resources/create-messages.sql"));
            StringBuilder buf = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buf.append(line);
                buf.append("\r\n");
            }
            reader.close();
            return buf.toString();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "", ex);
            return null;
        }
    }

    public static byte[] serializeObject(Object o) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(bout);
            out.writeObject(o);
            out.close();
            return bout.toByteArray();
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "", ex);
            return null;
        }
    }

    public static Object deserializeObject(byte[] raw) {
        try {
            ObjectInputStream din = new ObjectInputStream(new ByteArrayInputStream(raw));
            return din.readObject();
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "", ex);
            return null;
        }
    }

    public static double score(int up, int down) {
        int n = up + down;
        double z = 1.64485;
        double phat = up / n;
        return (phat + z * z / (2 * n) - z * Math.sqrt((phat * (1 - phat) + z * z / (4 * n)) / n)) / (1 + z * z / n);
    }
}
