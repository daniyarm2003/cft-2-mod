package com.lildan42.cft.utils;

import java.time.Duration;

public class StringFormatUtils {
    public static String formatDuration(Duration elapsedTime) {
        long elapsedSeconds = elapsedTime.toSeconds();

        long elapsedMinutes = elapsedSeconds / 60;
        elapsedSeconds %= 60;

        long elapsedHours = elapsedMinutes / 60;
        elapsedMinutes %= 60;

        return elapsedHours > 0 ? "%d:%02d:%02d".formatted(elapsedHours, elapsedMinutes, elapsedSeconds)
                : "%d:%02d".formatted(elapsedMinutes, elapsedSeconds);
    }
}
