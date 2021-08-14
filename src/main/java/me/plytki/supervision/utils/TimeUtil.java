package me.plytki.supervision.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static String parseLongToDate(long time, TimeUnit type) {
        int seconds = (int) Math.floor((time / 1000) % 60);
        int minutes = (int) Math.floor((time / (1000 * 60)) % 60);
        int hours = (int) Math.floor((time / (1000 * 60 * 60) % 24));
        switch (type) {
            case SECONDS: {
                return (seconds == 0 ? "" : (seconds < 10 ? "" + seconds : seconds) + "");
            }
            case MINUTES: {
                return (minutes == 0 ? "" : (minutes < 10 ? "" + minutes : minutes) + "");
            }
            case HOURS: {
                return (hours == 0 ? "" : (hours < 10 ? "" + hours : hours) + "");
            }
            case MILLISECONDS: {
                return setLastThreeCharks(String.valueOf(time)) + "";
            }
        }
        return (hours == 0 ? "" : (hours < 10 ? "" + hours : hours)) + ""
                + (minutes == 0 ? "" : (minutes < 10 ? "" + minutes : minutes))
                + (seconds == 0 ? "" : (seconds < 10 ? "" + seconds : seconds))
                + setLastThreeCharks(String.valueOf(time));
    }

    public static String setLastThreeCharks(String word) {
        if (word.length() == 3)
            return word;
        else if (word.length() > 3)
            return word.substring(word.length() - 3);
        else
            return word;
    }

}
